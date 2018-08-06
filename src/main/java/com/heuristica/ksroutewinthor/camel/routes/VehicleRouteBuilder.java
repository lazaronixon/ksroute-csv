package com.heuristica.ksroutewinthor.camel.routes;

import com.heuristica.ksroutewinthor.LocalizedBindyDataFormat;
import com.heuristica.ksroutewinthor.apis.VehicleApi;
import com.heuristica.ksroutewinthor.models.vehicle.Vehicle;
import com.heuristica.ksroutewinthor.models.vehicle.Driver;
import java.util.List;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

@Component
class VehicleRouteBuilder extends ApplicationRouteBuilder {

    @Override
    public void configure() {
        super.configure();
                
        from("file:files/vehicles?move=done").routeId("process-vehicle-file")
                .log(">>>>>>> Inicio arquivo ${file:path}")
                .unmarshal(new LocalizedBindyDataFormat(Vehicle.class))
                .split(body())
                .to("direct:process-vehicle").end()
                .log(">>>>>>> Fim arquivo ${file:path}");        

        from("direct:process-vehicle").routeId("process-vehicle")                
                .log("Processando veículo ${body.erpId}")
                .enrich("direct:find-vehicle", AggregationStrategies.bean(VehicleEnricher.class, "setId"))
                .enrich("direct:process-driver", AggregationStrategies.bean(VehicleEnricher.class, "setDriver"))                
                .choice().when(simple("${body.id} == null")).to("direct:create-vehicle")
                .otherwise().to("direct:update-vehicle")
                .unmarshal().json(JsonLibrary.Jackson, Vehicle.class);
        
        from("direct:find-vehicle").routeId("find-vehicle")                          
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("CamelHttpQuery", simple("q[erp_id_eq]=${body.erpId}"))
                .setBody(constant(null)).throttle(5).to("https4:{{ksroute.api.url}}/vehicles.json")
                .unmarshal(new ListJacksonDataFormat(VehicleApi.class));

        from("direct:create-vehicle").routeId("create-vehicle")
                .convertBodyTo(VehicleApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).to("https4:{{ksroute.api.url}}/vehicles.json");

        from("direct:update-vehicle").routeId("update-vehicle")                              
                .setHeader("id", simple("body.id"))
                .setHeader("CamelHttpMethod", constant("PUT")) 
                .convertBodyTo(VehicleApi.class).marshal().json(JsonLibrary.Jackson)
                .throttle(5).recipientList(simple("https4:{{ksroute.api.url}}/vehicles/${header.id}.json"));          
    }
    
    public class VehicleEnricher {
        
        public Vehicle setId(Vehicle local, List<VehicleApi> remoteList) {
            local.setId(remoteList.isEmpty() ? local.getId() : remoteList.get(0).getId());
            return local;
        }
        
        public Vehicle setDriver(Vehicle vehicle, Driver driver) {
            vehicle.setDriver(driver);
            return vehicle;
        }        
    }    
    
}
