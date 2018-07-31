package com.heuristica.ksroutewinthor.dozer.mappings;

import com.heuristica.ksroutewinthor.apis.BranchApi;
import com.heuristica.ksroutewinthor.models.order.Branch;
import org.dozer.loader.api.BeanMappingBuilder;

public class BranchMapping extends BeanMappingBuilder {

    @Override
    protected void configure() {
        mapping(Branch.class, BranchApi.class);
    }

}
