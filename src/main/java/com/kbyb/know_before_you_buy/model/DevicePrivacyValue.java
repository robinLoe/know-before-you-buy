package com.kbyb.know_before_you_buy.model;

import jakarta.persistence.Column;
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

    @Column(length = 2048) // for potentially long text fields
    private String value;

    // The type is boolean and not Boolean, because this field cant be null. Default value is false
    // So if someone mocks data, it must be marked as such but not if the data is real
    @Column(columnDefinition = "boolean default false")
    private boolean isMock;
}
