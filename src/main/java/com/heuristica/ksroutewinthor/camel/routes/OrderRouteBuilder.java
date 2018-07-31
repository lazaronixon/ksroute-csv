package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.LocalizedBindyDataFormat;
import com.heuristica.ksroutewinthor.models.order.Branch;
import com.heuristica.ksroutewinthor.models.order.Order;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

@Component
class OrderRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();

        from("file://files/orders?move=done").routeId("process-order-file")
                .log(">>>>>>> Inicio arquivo ${file:path}")
                .setHeader("X-User-Email", constant("{{ksroute.api.email}}"))
                .setHeader("X-User-Token", constant("{{ksroute.api.token}}"))                                 
                .unmarshal(new LocalizedBindyDataFormat(Order.class))
                .split(body()).to("direct:process-order");

        from("direct:process-order").routeId("process-order")
                .log(">>>> Inicio pedido ${body}")
                .split(simple("${body.branch}")).to("direct:process-branch").end()
                .log("${body}");
    }
}
