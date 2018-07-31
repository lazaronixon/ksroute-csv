package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.LocalizedBindyDataFormat;
import com.heuristica.ksroutewinthor.models.order.Order;
import org.springframework.stereotype.Component;

@Component
class OrderRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();

        from("file://files/orders?move=done").routeId("process-order-file")
                .log(">>>>>>> Inicio arquivo ${file:path}")
                .unmarshal(new LocalizedBindyDataFormat(Order.class))
                .split(body()).to("direct:process-order")
                .log(">>>>>>> Fim arquivo ${file:name}");
        
        from("direct:process-order").routeId("process-order")
                .log(">>>> Inicio pedido ${body.erpId}")
                .split(simple("body.branch")).to("direct:process-branch")
                .log(">>>> Fim pedido");
    }
}
