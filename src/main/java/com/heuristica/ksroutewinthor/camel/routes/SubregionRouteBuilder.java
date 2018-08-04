package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.apis.SubregionApi;
import com.heuristica.ksroutewinthor.models.order.Line;
import com.heuristica.ksroutewinthor.models.order.Region;
import com.heuristica.ksroutewinthor.models.order.Subregion;
import java.util.List;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

@Component
class SubregionRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();
        
        ListJacksonDataFormat jsonListDataformat = new ListJacksonDataFormat(SubregionApi.class);        

        from("direct:process-subregion").routeId("process-subregion")
                .transform(simple("body.subregion"))
                .enrich("direct:process-region", AggregationStrategies.bean(SubregionEnricher.class, "setRegion"))                
                .enrich("direct:process-line", AggregationStrategies.bean(SubregionEnricher.class, "setLine"))
                .enrich("direct:cached-subregion", AggregationStrategies.bean(SubregionEnricher.class, "setId"))                
                .choice().when(simple("${body.id} == null")).to("direct:create-subregion")
                .otherwise().to("direct:update-subregion");
        
        from("direct:cached-subregion").routeId("cached-subregion")
                .setHeader("CamelEhcacheKey", simple("subregions/${body.erpId}"))
                .to("ehcache://primary-cache?action=GET&valueType=java.lang.String")
                .choice().when((header("CamelEhcacheActionHasResult").isEqualTo(true))).unmarshal(jsonListDataformat)
                .otherwise().to("direct:find-subregion").unmarshal(jsonListDataformat);         
        
        from("direct:find-subregion").routeId("find-subregion")
                .log("sem cache")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .setBody(constant("")).throttle(5).to("https4://{{ksroute.api.url}}/subregions.json")
                .to("ehcache://primary-cache?action=PUT&valueType=java.lang.String")
                .to("ehcache://primary-cache?action=GET&valueType=java.lang.String");                

        from("direct:create-subregion").routeId("create-subregion")
                .convertBodyTo(SubregionApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4://{{ksroute.api.url}}/subregions.json")
                .unmarshal().json(JsonLibrary.Jackson, Subregion.class);

        from("direct:update-subregion").routeId("update-subregion")
                .idempotentConsumer(simple("subregions/${body.id}"), getIdempotentExpirableCache())
                .setHeader("id", simple("body.id"))
                .setHeader("CamelHttpMethod", constant("PUT")) 
                .convertBodyTo(SubregionApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4://{{ksroute.api.url}}/subregions/${header.id}.json"))
                .unmarshal().json(JsonLibrary.Jackson, Subregion.class);          
        
    }
    
    public class SubregionEnricher {
        
        public Subregion setId(Subregion local, List<SubregionApi> remoteList) {
            local.setId(remoteList.isEmpty() ? local.getId() : remoteList.get(0).getId());
            return local;
        }        

        public Subregion setRegion(Subregion subregion, Region region) {
            subregion.setRegion(region);
            return subregion;
        }
        
        public Subregion setLine(Subregion subregion, Line line) {
            subregion.setLine(line);
            return subregion;
        }
    }      
}
