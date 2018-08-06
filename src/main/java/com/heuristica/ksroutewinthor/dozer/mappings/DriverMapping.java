package com.heuristica.ksroutewinthor.dozer.mappings;

import com.heuristica.ksroutewinthor.apis.DriverApi;
import com.heuristica.ksroutewinthor.models.vehicle.Driver;
import org.dozer.loader.api.BeanMappingBuilder;
public class DriverMapping extends BeanMappingBuilder {

    @Override
    protected void configure() {
        mapping(Driver.class, DriverApi.class);
    }

}
