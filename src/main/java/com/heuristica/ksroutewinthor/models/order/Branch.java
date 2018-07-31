package com.heuristica.ksroutewinthor.models.order;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.Link;

@Data
@Link
public class Branch {  
    
    private Long id;
    
    @DataField(pos = 7, required = true)
    private String description;
    
    @DataField(pos = 8, required = true)
    private String erpId;
    
}
