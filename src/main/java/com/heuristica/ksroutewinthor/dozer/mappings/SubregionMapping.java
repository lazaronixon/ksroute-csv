package com.heuristica.ksroutewinthor.dozer.mappings;

import com.heuristica.ksroutewinthor.apis.SubregionApi;
import com.heuristica.ksroutewinthor.models.order.Subregion;
import org.dozer.loader.api.BeanMappingBuilder;

public class SubregionMapping extends BeanMappingBuilder {

    @Override
    protected void configure() {
        mapping(Subregion.class, SubregionApi.class);
    }

}
