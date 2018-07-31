package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.apis.CustomerApi;
import com.heuristica.ksroutewinthor.models.order.Customer;
import com.heuristica.ksroutewinthor.models.order.Subregion;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

@Component
class CustomerRouteBuilder extends ApplicationRouteBuilder {
    
    @Override
    public void configure() {
        super.configure();
        
        ListJacksonDataFormat jsonListDataformat = new ListJacksonDataFormat(CustomerApi.class);   
        
        from("direct:process-customer").routeId("process-customer")
                .log("Processando cliente ${body.customer.erpId}")
                .transform(simple("body.customer"))
                .enrich("direct:process-subregion", AggregationStrategies.bean(CustomerEnricher.class));        
    }
    
    public class CustomerEnricher {

        public Customer setSubregion(Customer customer, Subregion subregion) {
            customer.setSubregion(subregion);
            return customer;
        }      
    }      
}
