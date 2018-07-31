package com.heuristica.ksroutewinthor;

import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;


public class LocalizedBindyDataFormat extends BindyCsvDataFormat {

    public LocalizedBindyDataFormat(Class<?> type) {
        super(type);
        super.setLocale("pt-BR");
    }      
    
}
