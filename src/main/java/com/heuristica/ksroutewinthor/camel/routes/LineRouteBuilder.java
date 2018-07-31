package com.heuristica.ksroutewinthor.camel.routes;

import org.springframework.stereotype.Component;

@Component
class LineRouteBuilder extends ApplicationRouteBuilder {   
    
    @Override
    public void configure() {
        super.configure();
        
        from("direct:process-line").routeId("process-line")                
                .log("Processando linha ${body.line.erpId}");        
    }
}
