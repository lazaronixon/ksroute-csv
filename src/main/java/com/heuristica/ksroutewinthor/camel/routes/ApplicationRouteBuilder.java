package com.heuristica.ksroutewinthor.camel.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpOperationFailedException;

public class ApplicationRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        errorHandler(defaultErrorHandler().logExhaustedMessageHistory(false));
        
        onException(HttpOperationFailedException.class)
                .filter(simple("${exception.statusCode} == 422"))
                .log(LoggingLevel.WARN, "Erro de validação: ${body}")
                .log(LoggingLevel.WARN, "Detalhe: ${exception.responseBody}");
    }

}
