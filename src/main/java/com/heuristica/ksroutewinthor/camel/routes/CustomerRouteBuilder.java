package com.heuristica.ksroutewinthor.camel.routes;

import org.springframework.stereotype.Component;

@Component
class CustomerRouteBuilder extends ApplicationRouteBuilder {   
    
    @Override
    public void configure() {
        super.configure();
        
        from("direct:process-customer").routeId("process-customer")
                .log("Processando cliente ${body.customer.erpId}")
                .transform(simple("body.customer"))
                .to("direct:process-subregion");        
    }
}
