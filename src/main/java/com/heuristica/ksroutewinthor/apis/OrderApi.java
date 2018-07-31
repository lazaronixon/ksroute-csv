package com.heuristica.ksroutewinthor.apis;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class OrderApi {
    
    public static enum Status { available, blocked, mounted, billed }    
    
    private Long id;
    private BigDecimal amount;
    private BigDecimal weight;
    private BigDecimal volume;
    private Status status;
    private String erpId;
    private LocalDate issuedAt;        
    private Long branchId;
    private Long customerId; 
}
