package com.heuristica.ksroutewinthor.models.order;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.Link;

@Data
@Link
public class Subregion {
    
    private Long id;
    
    @DataField(pos = 19, required = true)
    private String description;
    
    @DataField(pos = 20, required = true)
    private String erpId;
    
    @DataField(pos = 21, defaultValue = "true")
    private Boolean active;
    
    @Link   
    private Region region;
    
    @Link   
    private Line line;
    
}
