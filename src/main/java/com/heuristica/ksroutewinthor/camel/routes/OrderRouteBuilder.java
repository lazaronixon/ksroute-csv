package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.LocalizedBindyDataFormat;
import com.heuristica.ksroutewinthor.models.order.Branch;
import com.heuristica.ksroutewinthor.models.order.Order;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

@Component
class OrderRouteBuilder extends ApplicationRouteBuilder {

    public class OrderEnricher {

        public Order setBranch(Order order, Branch branch) {
            order.setBranch(branch);
            return order;
        }
    }

    @Override
    public void configure() {
        super.configure();

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
                .log(">>>> Fim pedido ${body.erpId}");
    }
}
