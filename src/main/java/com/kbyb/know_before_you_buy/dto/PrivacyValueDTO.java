package com.kbyb.know_before_you_buy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for transferring privacy property values.
 * This class is used to structure data for API responses and requests,
 * containing details about a specific privacy property and its associated value.
 */
@Data
@NoArgsConstructor // Needed for JSON deserialization by Jackson
@AllArgsConstructor // Generates a constructor with ALL fields as parameters, so JPA/JPQL can directly map query results into this DTO
public class PrivacyValueDTO {
    
    private Integer propertyId;
    private String propertyName;
    private String value;
    private boolean isMock;
    
    public PrivacyValueDTO(Integer id, String propertyName, String value) {
        this.propertyId = id;
        this.propertyName = propertyName;
        this.value = value;
    }
}