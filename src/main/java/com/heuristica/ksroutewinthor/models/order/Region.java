package com.heuristica.ksroutewinthor.models.order;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.Link;

@Data
@Link
public class Region {
    
    private Long id;
    
    @DataField(pos = 22, required = true)
    private String description;
    
    @DataField(pos = 23, required = true)
    private String state;
    
    @DataField(pos = 24, required = true)
    private String erpId;
    
    @DataField(pos = 25, defaultValue = "true")
    private Boolean active;

}
