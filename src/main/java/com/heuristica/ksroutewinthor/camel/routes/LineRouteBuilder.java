package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.apis.LineApi;
import com.heuristica.ksroutewinthor.models.order.Line;
import java.util.List;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

@Component
class LineRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();

        ListJacksonDataFormat jsonListDataformat = new ListJacksonDataFormat(LineApi.class);

        from("direct:process-line").routeId("process-line")
                .transform(simple("body.line"))
                .enrich("direct:cached-line", AggregationStrategies.bean(LineEnricher.class))
                .choice().when(simple("${body.id} == null")).to("direct:create-line")
                .otherwise().to("direct:update-line");

        from("direct:cached-line").routeId("cached-line")
                .setHeader("CamelEhcacheKey", simple("lines/${body.erpId}"))
                .to("ehcache://primary-cache?action=GET&valueType=java.lang.String")
                .choice().when((header("CamelEhcacheActionHasResult").isEqualTo(true))).unmarshal(jsonListDataformat)
                .otherwise().to("direct:find-line").unmarshal(jsonListDataformat);

        from("direct:find-line").routeId("find-line")
                .log("sem cache")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .setBody(constant("")).throttle(5).to("https4://{{ksroute.api.url}}/lines.json")
                .to("ehcache://primary-cache?action=PUT&valueType=java.lang.String")
                .to("ehcache://primary-cache?action=GET&valueType=java.lang.String");

        from("direct:create-line").routeId("create-line")
                .convertBodyTo(LineApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4://{{ksroute.api.url}}/lines.json")
                .unmarshal().json(JsonLibrary.Jackson, Line.class);

        from("direct:update-line").routeId("update-line")
                .idempotentConsumer(simple("lines/${body.id}"), getIdempotentExpirableCache())
                .setHeader("id", simple("body.id"))
                .setHeader("CamelHttpMethod", constant("PUT"))
                .convertBodyTo(LineApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4://{{ksroute.api.url}}/lines/${header.id}.json"))
                .unmarshal().json(JsonLibrary.Jackson, Line.class);
    }

    public class LineEnricher {

        public Line setId(Line local, List<LineApi> remoteList) {
            local.setId(remoteList.isEmpty() ? local.getId() : remoteList.get(0).getId());
            return local;
        }
    }
}
