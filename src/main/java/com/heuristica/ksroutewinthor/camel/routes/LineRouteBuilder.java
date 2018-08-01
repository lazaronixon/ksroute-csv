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
                .transform(simple("body.line")).convertBodyTo(LineApi.class)
                .enrich("direct:find-line", AggregationStrategies.bean(LineEnricher.class))                
                .choice().when(simple("${body.id} == null")).to("direct:create-line")
                .otherwise().to("direct:update-line")
                .unmarshal().json(JsonLibrary.Jackson, Line.class);   
        
        from("direct:find-line").routeId("find-line")
                .setHeader("CamelHttpMethod", constant("GET"))                               
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .setBody(constant("")).throttle(50).timePeriodMillis(10000).to("https4://{{ksroute.api.url}}/lines.json")
                .unmarshal(jsonListDataformat);

        from("direct:create-line").routeId("create-line")
                .setHeader("CamelHttpMethod", constant("POST"))
                .marshal().json(JsonLibrary.Jackson)
                .throttle(50).timePeriodMillis(10000).to("https4://{{ksroute.api.url}}/lines.json");

        from("direct:update-line").routeId("update-line")
                .setHeader("CamelHttpMethod", constant("PUT"))               
                .setHeader("lineId", simple("body.id"))
                .marshal().json(JsonLibrary.Jackson)
                .throttle(50).timePeriodMillis(10000).recipientList(simple("https4://{{ksroute.api.url}}/lines/${header.lineId}.json"));        
    }
    
    public class LineEnricher {

        public LineApi setId(LineApi local, List<LineApi> remoteList) {
            local.setId(remoteList.isEmpty() ? local.getId() : remoteList.get(0).getId());
            return local;
        }
    }    
}
