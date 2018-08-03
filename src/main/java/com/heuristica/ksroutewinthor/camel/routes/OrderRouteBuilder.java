package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.LocalizedBindyDataFormat;
import com.heuristica.ksroutewinthor.apis.BranchApi;
import com.heuristica.ksroutewinthor.apis.OrderApi;
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
        
        LocalizedBindyDataFormat bindyDataFormat = new LocalizedBindyDataFormat(Order.class);
        ListJacksonDataFormat jsonListDataformat = new ListJacksonDataFormat(OrderApi.class);        
                
        from("file://files/orders?move=done").routeId("process-order-file")
                .log(">>>>>>> Inicio arquivo ${file:path}")
                .unmarshal(bindyDataFormat).split(body())
                .to("direct:process-order").end()
                .log(">>>>>>> Fim arquivo ${file:path}");        

        from("direct:process-order").routeId("process-order")                
                .log("Processando pedido ${body.erpId}")
                .enrich("direct:find-branch", AggregationStrategies.bean(OrderEnricher.class, "setBranch"))
                .enrich("direct:process-customer", AggregationStrategies.bean(OrderEnricher.class, "setCustomer"))
                .enrich("direct:find-order", AggregationStrategies.bean(OrderEnricher.class, "setId"))
                .choice().when(simple("${body.id} == null")).to("direct:create-order")
                .otherwise().to("direct:update-order")
                .unmarshal().json(JsonLibrary.Jackson, Order.class);
        
        from("direct:find-order").routeId("find-order")                          
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .setBody(constant("")).throttle(5).to("https4://{{ksroute.api.url}}/orders.json")
                .unmarshal(jsonListDataformat);

        from("direct:create-order").routeId("create-order")
                .convertBodyTo(OrderApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4://{{ksroute.api.url}}/orders.json");

        from("direct:update-order").routeId("update-order")                              
                .setHeader("id", simple("body.id"))
                .setHeader("CamelHttpMethod", constant("PUT")) 
                .convertBodyTo(OrderApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4://{{ksroute.api.url}}/orders/${header.id}.json"));          
    }
    
    public class OrderEnricher {
        
        public Order setId(Order local, List<OrderApi> remoteList) {
            local.setId(remoteList.isEmpty() ? local.getId() : remoteList.get(0).getId());
            return local;
        }        

        public Order setBranch(Order order, List<BranchApi> remoteList) {
            order.setBranchId(remoteList.isEmpty() ? order.getBranchId(): remoteList.get(0).getId());
            return order;
        }
        
        public Order setCustomer(Order order, Customer customer) {
            order.setCustomer(customer);
            return order;
        }        
    }    
    
}
