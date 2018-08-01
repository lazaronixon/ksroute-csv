package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.apis.BranchApi;
import com.heuristica.ksroutewinthor.models.order.Branch;
import java.util.List;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

@Component
class BranchRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();
        
        ListJacksonDataFormat jsonListDataformat = new ListJacksonDataFormat(BranchApi.class);  

        from("direct:process-branch").routeId("process-branch")
                .transform(simple("body.branch")).convertBodyTo(BranchApi.class)
                .enrich("direct:find-branch", AggregationStrategies.bean(BranchEnricher.class))                
                .choice().when(simple("${body.id} == null")).to("direct:create-branch")
                .otherwise().to("direct:update-branch")
                .unmarshal().json(JsonLibrary.Jackson, Branch.class);

        from("direct:find-branch").routeId("find-branch")
                .setHeader("CamelHttpMethod", constant("GET"))                               
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .setBody(constant("")).throttle(5).to("https4://{{ksroute.api.url}}/branches.json")
                .unmarshal(jsonListDataformat);

        from("direct:create-branch").routeId("create-branch")
                .setHeader("CamelHttpMethod", constant("POST"))
                .marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4://{{ksroute.api.url}}/branches.json");

        from("direct:update-branch").routeId("update-branch")
                .setHeader("CamelHttpMethod", constant("PUT"))               
                .setHeader("branchId", simple("body.id"))
                .marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4://{{ksroute.api.url}}/branches/${header.branchId}.json"));                
    }
    
    
    public class BranchEnricher {

        public BranchApi setId(BranchApi local, List<BranchApi> remoteList) {
            local.setId(remoteList.isEmpty() ? local.getId() : remoteList.get(0).getId());
            return local;
        }
    }    
    
}
