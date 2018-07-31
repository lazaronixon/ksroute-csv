package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.apis.BranchApi;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

@Component
class BranchRouteBuilder extends ApplicationRouteBuilder {    
    
    private final ListJacksonDataFormat jsonListDataformat = new ListJacksonDataFormat(BranchApi.class);
    
    private class BranchAggregate implements AggregationStrategy {
        @Override
        public Exchange aggregate(Exchange localExchange, Exchange remoteExchange) {
            List<BranchApi> remoteBranches = remoteExchange.getIn().getBody(List.class);
            if (remoteBranches.isEmpty() == false) {
                BranchApi localBranch = localExchange.getIn().getBody(BranchApi.class);
                localBranch.setId(remoteBranches.get(0).getId());
                localExchange.getIn().setBody(localBranch);
            }
            return localExchange;
        }        
    }
    
    @Override
    public void configure() {
        super.configure();
        
        from("direct:process-branch").routeId("process-branch")
                .log("Processando filial ${body.erpId}")                
                .convertBodyTo(BranchApi.class)
                .enrich("direct:find-branch", new BranchAggregate())
                .choice().when(simple("${body.id} == null")).to("direct:create-branch")
                .otherwise().to("direct:update-branch");
        
        from("direct:find-branch").routeId("find-branch")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("X-User-Email", constant("{{ksroute.api.email}}"))
                .setHeader("X-User-Token", constant("{{ksroute.api.token}}"))                       
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))               
                .setBody(constant(""))
                .throttle(5).to("https4://{{ksroute.api.url}}/branches.json")
                .unmarshal(jsonListDataformat);
        
        from("direct:create-branch").routeId("create-branch")
                .setHeader("CamelHttpMethod", constant("POST"))
                .setHeader("X-User-Email", constant("{{ksroute.api.email}}"))
                .setHeader("X-User-Token", constant("{{ksroute.api.token}}"))
                .marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4://{{ksroute.api.url}}/branches.json")
                .unmarshal().json(JsonLibrary.Jackson, BranchApi.class);
        
        from("direct:update-branch").routeId("update-filial")
                .setHeader("CamelHttpMethod", constant("PUT"))
                .setHeader("X-User-Email", constant("{{ksroute.api.email}}"))
                .setHeader("X-User-Token", constant("{{ksroute.api.token}}"))
                .setHeader("id", simple("body.id"))
                .marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4://{{ksroute.api.url}}/branches/${header.id}.json"));
    }
}
