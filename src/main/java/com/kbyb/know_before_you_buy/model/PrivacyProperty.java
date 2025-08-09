package com.kbyb.know_before_you_buy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class PrivacyProperty {

    @Id
    private Integer id;

    private String name;

    private String description;

    private String category;

    private String metric;
    
}
