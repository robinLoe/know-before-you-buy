package com.kbyb.know_before_you_buy.dto;

import lombok.Data;

@Data
public class PrivacyValueDTO {
    
    private Integer propertyId;
    private String propertyName;
    private String value;
    
    public PrivacyValueDTO(String propertyName, String value) {
        this.propertyName = propertyName;
        this.value = value;
    }
}