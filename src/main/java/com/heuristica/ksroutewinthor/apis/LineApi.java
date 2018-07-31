package com.heuristica.ksroutewinthor.apis;

import lombok.Data;

@Data
public class LineApi {
    
    private Long id;
    private String description;
    private String erpId;
    private Boolean active;
    
}
