package com.heuristica.ksroutewinthor.models.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.Link;

@Data
@CsvRecord(separator = ";")
public class Order {
    
    public static enum Status { available, blocked, mounted, billed }
    
    private Long id;
    
    @DataField(pos = 1, required = true, pattern = "#.##")
    private BigDecimal amount;
    
    @DataField(pos = 2, required = true, pattern = "#.##")
    private BigDecimal weight;
    
    @DataField(pos = 3, pattern = "#.###")
    private BigDecimal volume;
    
    @DataField(pos = 4, defaultValue = "available")
    private Status status;
    
    @DataField(pos = 5, required = true)
    private String erpId;
    
    @DataField(pos = 6, required = true, pattern = "dd/MM/yyyy")
    private LocalDate issuedAt;  
    
    @Link
    private Branch branch;    
       
    @Link
    private Customer customer;
}
