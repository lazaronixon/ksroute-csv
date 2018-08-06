package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.apis.DriverApi;
import com.heuristica.ksroutewinthor.models.order.Line;
import com.heuristica.ksroutewinthor.models.vehicle.Driver;
import java.util.List;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
class DriverRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();

        from("direct:process-driver").routeId("process-driver")
                .transform(simple("body.driver")).filter(simple("${body.erpId} != null"))
                .choice().when(simple("${body.id} == null")).to("direct:create-driver")
                .otherwise().to("direct:update-driver")
                .unmarshal().json(JsonLibrary.Jackson, Line.class);

        from("direct:find-driver").routeId("find-driver")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .setBody(constant(null)).throttle(5).to("https4:{{ksroute.api.url}}/drivers.json")
                .unmarshal(new ListJacksonDataFormat(DriverApi.class));

        from("direct:create-driver").routeId("create-driver")
                .convertBodyTo(DriverApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4:{{ksroute.api.url}}/drivers.json");

        from("direct:update-driver").routeId("update-driver")                
                .setHeader("id", simple("body.id"))
                .setHeader("CamelHttpMethod", constant("PUT"))
                .convertBodyTo(DriverApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4:{{ksroute.api.url}}/drivers/${header.id}.json"));
    }

    public class DriverEnricher {

        public Driver setId(Driver local, List<DriverApi> remoteList) {
            local.setId(remoteList.isEmpty() ? local.getId() : remoteList.get(0).getId());
            return local;
        }
    }
}
