package com.kbyb.know_before_you_buy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Device {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String deviceType;

    private String functionalClassification;

    @Column(length = 2048) // to support long urls
    private String imageUrl;

    @Column(length = 2048) // to support long urls
    private String url;
}
