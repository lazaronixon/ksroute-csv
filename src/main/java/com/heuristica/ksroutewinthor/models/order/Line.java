package com.heuristica.ksroutewinthor.models.order;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.Link;

@Data
@Link
public class Line { 
    
    private Long id;
    
    @DataField(pos = 26, required = true)
    private String description;
    
    @DataField(pos = 27, required = true)
    private String erpId;
    
    @DataField(pos = 28, defaultValue = "true")
    private Boolean active;
    
}
