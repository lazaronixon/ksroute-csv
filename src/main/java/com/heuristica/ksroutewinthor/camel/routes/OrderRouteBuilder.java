package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.LocalizedBindyDataFormat;
import com.heuristica.ksroutewinthor.apis.OrderApi;
import com.heuristica.ksroutewinthor.models.order.Branch;
import com.heuristica.ksroutewinthor.models.order.Customer;
import com.heuristica.ksroutewinthor.models.order.Order;
import java.util.List;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

@Component
class OrderRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();
        
        ListJacksonDataFormat jsonListDataformat = new ListJacksonDataFormat(OrderApi.class);        
                
        from("file://files/orders?move=done").routeId("process-order-file")
                .log(">>>>>>> Inicio arquivo ${file:path}")
                .setHeader("X-User-Email", constant("{{ksroute.api.email}}"))
                .setHeader("X-User-Token", constant("{{ksroute.api.token}}"))
                .unmarshal(new LocalizedBindyDataFormat(Order.class))
                .split(body()).to("direct:process-order").end()
                .log(">>>>>>> Fim arquivo ${file:path}");        

        from("direct:process-order").routeId("process-order")
                .log(">>>> Inicio pedido ${body.erpId}")
                .enrich("direct:process-branch", AggregationStrategies.bean(OrderEnricher.class, "setBranch"))
                .enrich("direct:process-customer", AggregationStrategies.bean(OrderEnricher.class, "setCustomer"))
                .convertBodyTo(OrderApi.class)
                .enrich("direct:find-order", AggregationStrategies.bean(OrderEnricher.class, "setId"))                
                .choice().when(simple("${body.id} == null")).to("direct:create-order")
                .otherwise().to("direct:update-order")
                .unmarshal().json(JsonLibrary.Jackson, Order.class)
                .log(">>>> Fim pedido ${body.erpId}");
        
        from("direct:find-order").routeId("find-order")
                .setHeader("CamelHttpMethod", constant("GET"))                               
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .throttle(5).setBody(constant("")).to("https4://{{ksroute.api.url}}/orders.json")
                .unmarshal(jsonListDataformat);

        from("direct:create-order").routeId("create-order")
                .setHeader("CamelHttpMethod", constant("POST"))
                .marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4://{{ksroute.api.url}}/orders.json");

        from("direct:update-order").routeId("update-order")
                .setHeader("CamelHttpMethod", constant("PUT"))               
                .setHeader("orderId", simple("body.id"))
                .marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4://{{ksroute.api.url}}/orders/${header.orderId}.json"));          
    }
    
    public class OrderEnricher {
        
        public OrderApi setId(OrderApi local, List<OrderApi> remoteList) {
            local.setId(remoteList.isEmpty() ? local.getId() : remoteList.get(0).getId());
            return local;
        }        

        public Order setBranch(Order order, Branch branch) {
            order.setBranch(branch);
            return order;
        }
        
        public Order setCustomer(Order order, Customer customer) {
            order.setCustomer(customer);
            return order;
        }        
    }    
    
}
