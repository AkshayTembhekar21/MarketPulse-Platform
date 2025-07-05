
package com.server.uidesk.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Trade {
    private String ticker;
    private double price;
    private LocalDateTime timestamp;
}
