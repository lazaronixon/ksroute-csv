package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.apis.BranchApi;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.springframework.stereotype.Component;

@Component
class BranchRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();    

        from("direct:find-branch").routeId("find-branch")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.branchId}"))
                .setBody(constant(null)).throttle(5).to("https4:{{ksroute.api.url}}/branches.json")
                .unmarshal(new ListJacksonDataFormat(BranchApi.class));            
    }
    
}
