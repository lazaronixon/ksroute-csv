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
        
        ListJacksonDataFormat jsonListDataformat = new ListJacksonDataFormat(RegionApi.class);        

        from("direct:process-region").routeId("process-region")
                .log("Processando região ${body.region.erpId}")
                .transform(simple("body.region")).convertBodyTo(RegionApi.class)
                .enrich("direct:find-region", AggregationStrategies.bean(RegionEnricher.class))                
                .choice().when(simple("${body.id} == null")).to("direct:create-region")
                .otherwise().to("direct:update-region")
                .unmarshal().json(JsonLibrary.Jackson, Region.class);
        
        from("direct:find-region").routeId("find-region")
                .setHeader("CamelHttpMethod", constant("GET"))                               
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .throttle(5).setBody(constant("")).to("https4://{{ksroute.api.url}}/regions.json")
                .unmarshal(jsonListDataformat);

        from("direct:create-region").routeId("create-region")
                .setHeader("CamelHttpMethod", constant("POST"))
                .marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4://{{ksroute.api.url}}/regions.json");

        from("direct:update-region").routeId("update-region")
                .setHeader("CamelHttpMethod", constant("PUT"))               
                .setHeader("regionId", simple("body.id"))
                .marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4://{{ksroute.api.url}}/regions/${header.regionId}.json"));
        
    }
    
    public class RegionEnricher {

        public RegionApi setId(RegionApi local, List<RegionApi> remoteList) {
            local.setId(remoteList.isEmpty() ? local.getId() : remoteList.get(0).getId());
            return local;
        }
    }      
}
