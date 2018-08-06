package com.heuristica.ksroutewinthor.dozer.mappings;

import com.heuristica.ksroutewinthor.apis.VehicleApi;
import com.heuristica.ksroutewinthor.models.vehicle.Vehicle;
import org.dozer.loader.api.BeanMappingBuilder;
public class VehicleMapping extends BeanMappingBuilder {

    @Override
    protected void configure() {
        mapping(Vehicle.class, VehicleApi.class).fields("driver.id", "driverId");
    }

}
