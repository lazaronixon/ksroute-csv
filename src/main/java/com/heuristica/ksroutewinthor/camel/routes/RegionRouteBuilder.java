package com.heuristica.ksroutewinthor.camel.routes;

import org.springframework.stereotype.Component;

@Component
class RegionRouteBuilder extends ApplicationRouteBuilder {   
    
    @Override
    public void configure() {
        super.configure();
        
        from("direct:process-region").routeId("process-region")
                .log("Processando região ${body.region.erpId}");                        
    }
}
