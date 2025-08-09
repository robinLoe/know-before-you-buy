package com.kbyb.know_before_you_buy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class PrivacyProperty {

    @Id
    private Integer id;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private String category;

    @Column(columnDefinition = "text")
    private String metric;
    
}
