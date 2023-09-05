package com.farmmanager.farmmanager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    
    private String type;
    private String message;
    private int timeout;

    public Alert(String type, String message) {
        this.type = type;
        this.message = message;
        this.timeout = 3000;
    }
}
