package com.kbyb.know_before_you_buy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Needed for JSON deserialization by Jackson
public class PrivacyValueDTO {
    
    private Integer propertyId;
    private String propertyName;
    private String value;
    
    public PrivacyValueDTO(String propertyName, String value) {
        this.propertyName = propertyName;
        this.value = value;
    }
}