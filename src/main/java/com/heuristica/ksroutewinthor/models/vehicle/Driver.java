package com.heuristica.ksroutewinthor.models.vehicle;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.Link;

@Data
@Link
public class Driver {
    
    private Long id;
    
    @DataField(pos = 8)
    private String name;  
    
    @DataField(pos = 9)
    private String phone;
    
    @DataField(pos = 10)
    private String erpId;    
    
}
