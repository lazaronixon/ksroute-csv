package com.heuristica.ksroutewinthor.apis;

import lombok.Data;

@Data
public class VehicleApi {
    
    public static enum Status { available, traveling, blocked, returning, inactive }
    
    private Long id;
    private String description;
    private Boolean returnToDepot;
    private String erpId;
    private Status status;
    
    private Long startAddressId;
    private Long vehicleTypeId;
    private Long driverId;    
    
}
