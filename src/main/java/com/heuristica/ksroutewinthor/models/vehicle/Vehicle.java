package com.heuristica.ksroutewinthor.models.vehicle;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.Link;

@Data
@CsvRecord(separator = ";")
public class Vehicle {
    
    public static enum Status { available, traveling, blocked, returning, inactive }
    
    private Long id;
    
    @DataField(pos = 1, required = true)
    private String description;
    
    @DataField(pos = 2, defaultValue = "true")
    private Boolean returnToDepot;
    
    @DataField(pos = 3, defaultValue = "available")
    private Status status;
    
    @DataField(pos = 4, required = true)
    private String erpId;

    @DataField(pos = 5, required = true)
    private Long vehicleTypeId;     
    
    @DataField(pos = 6, required = true)
    private Long startAddressId;
    
    @DataField(pos = 7)
    private Long endAddressId;    
    
    @Link
    private Driver driver;
  
}
