package com.heuristica.ksroutewinthor.apis;

import lombok.Data;

@Data
public class SubregionApi {
    
    private Long id;
    private String description;
    private String erpId;
    private Boolean active;    
    private Long regionId;        
    private Long lineId;
    
}
