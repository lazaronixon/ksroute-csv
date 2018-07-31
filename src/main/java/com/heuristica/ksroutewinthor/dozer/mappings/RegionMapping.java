package com.heuristica.ksroutewinthor.dozer.mappings;

import com.heuristica.ksroutewinthor.apis.RegionApi;
import com.heuristica.ksroutewinthor.models.order.Region;
import org.dozer.loader.api.BeanMappingBuilder;

public class RegionMapping extends BeanMappingBuilder {

    @Override
    protected void configure() {
        mapping(Region.class, RegionApi.class);
    }

}
