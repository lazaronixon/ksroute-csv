package com.heuristica.ksroutewinthor.apis;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Order {
    
    public static enum Status { available, blocked, mounted, billed }    
    
    private Long id;
    private Double amount;
    private Double weight;
    private Double volume;
    private Status status;
    private String erpId;
    private LocalDate issuedAt;    
    
    private Long branchId;    
    private Branch branch;

    private Long customerId;    
    private Customer customer;    
}
