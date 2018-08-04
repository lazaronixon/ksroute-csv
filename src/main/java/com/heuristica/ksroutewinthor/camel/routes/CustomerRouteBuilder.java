package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.apis.CustomerApi;
import com.heuristica.ksroutewinthor.models.order.Customer;
import com.heuristica.ksroutewinthor.models.order.Subregion;
import java.util.List;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

@Component
class CustomerRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();

        ListJacksonDataFormat jsonListDataformat = new ListJacksonDataFormat(CustomerApi.class);

        from("direct:process-customer").routeId("process-customer")
                .transform(simple("body.customer"))
                .enrich("direct:process-subregion", AggregationStrategies.bean(CustomerEnricher.class, "setSubregion"))
                .enrich("direct:find-customer", AggregationStrategies.bean(CustomerEnricher.class, "setIdAndLatLng"))
                .choice().when(simple("${body.id} == null")).to("direct:create-customer")
                .otherwise().to("direct:update-customer")
                .unmarshal().json(JsonLibrary.Jackson, Customer.class);

        from("direct:find-customer").routeId("find-customer")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .setBody(constant("")).throttle(5).to("https4:{{ksroute.api.url}}/customers.json")
                .unmarshal(jsonListDataformat);

        from("direct:create-customer").routeId("create-customer")
                .convertBodyTo(CustomerApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4:{{ksroute.api.url}}/customers.json");

        from("direct:update-customer").routeId("update-customer")
                .setHeader("id", simple("body.id"))
                .setHeader("CamelHttpMethod", constant("PUT"))
                .convertBodyTo(CustomerApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4:{{ksroute.api.url}}/customers/${header.id}.json"));
    }

    public class CustomerEnricher {

        public Customer setIdAndLatLng(Customer local, List<CustomerApi> remoteList) {
            if (remoteList.isEmpty() == false) {
                local.setId(remoteList.get(0).getId());
                local.setLatitude(remoteList.get(0).getLatitude());
                local.setLatitude(remoteList.get(0).getLongitude());
            }
            return local;
        }

        public Customer setSubregion(Customer customer, Subregion subregion) {
            customer.setSubregion(subregion);
            return customer;
        }
    }
}
