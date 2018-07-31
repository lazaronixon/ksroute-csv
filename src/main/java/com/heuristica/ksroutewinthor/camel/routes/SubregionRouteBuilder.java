package com.heuristica.ksroutewinthor.camel.routes;

import org.springframework.stereotype.Component;

@Component
class SubregionRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();

        from("direct:process-subregion").routeId("process-subregion")
                .log("Processando subregião ${body.subregion.erpId}")
                .transform(simple("body.subregion"))
                .to("direct:process-region")
                .to("direct:process-line");
    }
}
