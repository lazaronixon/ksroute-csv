package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.apis.BranchApi;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.springframework.stereotype.Component;

@Component
class BranchRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();
        
        ListJacksonDataFormat jsonListDataformat = new ListJacksonDataFormat(BranchApi.class);
        
        from("direct:cached-branch").routeId("cached-branch")
                .setHeader("CamelEhcacheKey", simple("branches/${body.branchId}"))
                .to("ehcache:primary-cache?action=GET&valueType=java.lang.String")
                .choice().when((header("CamelEhcacheActionHasResult").isEqualTo(true))).unmarshal(jsonListDataformat)
                .otherwise().to("direct:find-branch").unmarshal(jsonListDataformat);        

        from("direct:find-branch").routeId("find-branch")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.branchId}"))
                .setBody(constant("")).throttle(5).to("https4:{{ksroute.api.url}}/branches.json")
                .to("ehcache:primary-cache?action=PUT&valueType=java.lang.String")
                .to("ehcache:primary-cache?action=GET&valueType=java.lang.String");                
            
    }
    
}
