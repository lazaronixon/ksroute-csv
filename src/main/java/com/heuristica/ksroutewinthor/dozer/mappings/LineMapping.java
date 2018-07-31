package com.heuristica.ksroutewinthor.dozer.mappings;

import com.heuristica.ksroutewinthor.apis.LineApi;
import com.heuristica.ksroutewinthor.models.order.Line;
import org.dozer.loader.api.BeanMappingBuilder;
public class LineMapping extends BeanMappingBuilder {

    @Override
    protected void configure() {
        mapping(Line.class, LineApi.class);
    }

}
