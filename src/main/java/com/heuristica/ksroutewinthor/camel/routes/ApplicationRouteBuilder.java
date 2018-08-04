package com.heuristica.ksroutewinthor.camel.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.ehcache.processor.idempotent.EhcacheIdempotentRepository;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationRouteBuilder extends RouteBuilder {

    @Autowired
    protected CacheManager cacheManager;

    @Override
    public void configure() {
        onException(HttpOperationFailedException.class)
                .filter(simple("${exception.statusCode} == 422"))
                .log(LoggingLevel.WARN, "Erro de validação: ${body}")
                .log(LoggingLevel.WARN, "Detalhe: ${exception.responseBody}");
    }

    protected EhcacheIdempotentRepository getIdempotentExpirableCache() {
        return new EhcacheIdempotentRepository(cacheManager, "idempotent-expirable-cache");
    }

}
