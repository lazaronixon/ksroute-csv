package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.apis.RegionApi;
import com.heuristica.ksroutewinthor.models.order.Region;
import java.util.List;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

@Component
class RegionRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();

        from("direct:process-region").routeId("process-region")
                .transform(simple("body.region"))
                .enrich("direct:find-region", AggregationStrategies.bean(RegionEnricher.class))                
                .idempotentConsumer(simple("regions/${body.id}"), getIdempotentExpirableCache())
                .choice().when(simple("${body.id} == null")).to("direct:create-region")
                .otherwise().to("direct:update-region")
                .unmarshal().json(JsonLibrary.Jackson, Region.class);       
        
        from("direct:find-region").routeId("find-region")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .setBody(constant(null)).throttle(5).to("https4:{{ksroute.api.url}}/regions.json")
                .unmarshal(new ListJacksonDataFormat(RegionApi.class));

        from("direct:create-region").routeId("create-region")
                .convertBodyTo(RegionApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4:{{ksroute.api.url}}/regions.json");

        from("direct:update-region").routeId("update-region")                
                .setHeader("id", simple("body.id"))
                .setHeader("CamelHttpMethod", constant("PUT")) 
                .convertBodyTo(RegionApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4:{{ksroute.api.url}}/regions/${header.id}.json"));        
    }
    
    public class RegionEnricher {

        public Region setId(Region local, List<RegionApi> remoteList) {
            local.setId(remoteList.isEmpty() ? local.getId() : remoteList.get(0).getId());
            return local;
        }
    }      
}
