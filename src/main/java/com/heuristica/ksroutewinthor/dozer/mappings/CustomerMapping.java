package com.heuristica.ksroutewinthor.dozer.mappings;

import com.heuristica.ksroutewinthor.apis.CustomerApi;
import com.heuristica.ksroutewinthor.models.order.Customer;
import org.dozer.loader.api.BeanMappingBuilder;

public class CustomerMapping extends BeanMappingBuilder {

    @Override
    protected void configure() {
        mapping(Customer.class, CustomerApi.class).fields("subregion.id", "subregionId");
    }

}
