package com.heuristica.ksroutewinthor.models.order;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.Link;

@Data
@Link
public class Customer {    
    
    private Long id;
    
    @DataField(pos = 8, required = true)
    private String name;
    
    @DataField(pos = 9, required = true)
    private String trade;
    
    @DataField(pos = 10, required = true)
    private String state;
    
    @DataField(pos = 11, required = true)
    private String city;
    
    @DataField(pos = 12, required = true)
    private String neighborhood;
    
    @DataField(pos = 13, required = true)
    private String address;
    
    @DataField(pos = 14, required = true)
    private String zipcode;
    
    @DataField(pos = 15)
    private Float latitude;
    
    @DataField(pos = 16)
    private Float longitude;
    
    @DataField(pos = 17, required = true)
    private String erpId; 
    
    @DataField(pos = 18, defaultValue = "true")
    private Boolean active;   
    
    @Link
    private Subregion subregion;
    
}
