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
                .enrich("direct:find-line", AggregationStrategies.bean(LineEnricher.class))
                .idempotentConsumer(simple("lines/${body.id}"), getIdempotentCache())
                .log("sem cacheee")
                .choice().when(simple("${body.id} == null")).to("direct:create-line")
                .otherwise().to("direct:update-line")
                .unmarshal().json(JsonLibrary.Jackson, Line.class);   
        
        from("direct:find-line").routeId("find-line")                             
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .setBody(constant("")).throttle(5).to("https4://{{ksroute.api.url}}/lines.json")
                .unmarshal(jsonListDataformat);

        from("direct:create-line").routeId("create-line")
                .convertBodyTo(LineApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4://{{ksroute.api.url}}/lines.json");

        from("direct:update-line").routeId("update-line")                              
                .setHeader("id", simple("body.id"))
                .setHeader("CamelHttpMethod", constant("PUT")) 
                .convertBodyTo(LineApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4://{{ksroute.api.url}}/lines/${header.id}.json"));        
    }
    
    public class LineEnricher {

        public Line setId(Line local, List<LineApi> remoteList) {
            local.setId(remoteList.isEmpty() ? local.getId() : remoteList.get(0).getId());
            return local;
        }
    }    
}
