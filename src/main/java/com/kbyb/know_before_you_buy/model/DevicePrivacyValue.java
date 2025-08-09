package com.kbyb.know_before_you_buy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class DevicePrivacyValue {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name= "device_id")
    private Device device;

    @ManyToOne
    @JoinColumn(name= "privacy_property_id")
    private PrivacyProperty privacyProperty;

    private String value;
}
