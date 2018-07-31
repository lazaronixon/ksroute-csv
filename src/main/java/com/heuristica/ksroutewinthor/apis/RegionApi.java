package com.heuristica.ksroutewinthor.apis;

import lombok.Data;

@Data
public class RegionApi {

    private Long id;
    private String description;
    private String state;
    private String erpId;
    private Boolean active;

}
