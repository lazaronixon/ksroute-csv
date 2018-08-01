package com.heuristica.ksroutewinthor.dozer.mappings;

import com.heuristica.ksroutewinthor.apis.OrderApi;
import com.heuristica.ksroutewinthor.models.order.Order;
import org.dozer.loader.api.BeanMappingBuilder;

public class OrderMapping extends BeanMappingBuilder {

    @Override
    protected void configure() {
        mapping(Order.class, OrderApi.class)
                .fields("branch.id", "branchId")
                .fields("customer.id", "customerId");
    }

}
