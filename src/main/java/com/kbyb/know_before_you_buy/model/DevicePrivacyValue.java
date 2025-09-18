package com.kbyb.know_before_you_buy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/**
 * Represents a link between a Device and a PrivacyProperty, storing a specific privacy value.
 * This class is a JPA entity that maps to a database table, representing the many-to-many relationship
 * between devices and privacy properties with an additional 'value' attribute.
 */
@Entity
@Data
public class DevicePrivacyValue {
    /**
     * The @Id annotation marks this field as the primary key of the entity.
     * @GeneratedValue configures the primary key generation strategy to be IDENTITY,
     * which relies on an auto-incremented database column.
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    /**
     * The device associated with this privacy value.
     * The @ManyToOne annotation defines a many-to-one relationship with the Device entity.
     * @JoinColumn specifies the foreign key column (device_id) in the database table.
     */
    @ManyToOne
    @JoinColumn(name= "device_id")
    private Device device;

    /**
     * The privacy property associated with this privacy value.
     * The @ManyToOne annotation defines a many-to-one relationship with the PrivacyProperty entity.
     * @JoinColumn specifies the foreign key column (privacy_property_id) in the database table.
     */
    @ManyToOne
    @JoinColumn(name= "privacy_property_id")
    private PrivacyProperty privacyProperty;

    // The actual value of the property
    @Column(length = 2048) // for potentially long text fields
    private String value;

    // The type is boolean and not Boolean, because this field cant be null. Default value is false
    // So if someone mocks data, it must be marked as such but not if the data is real
    @Column(columnDefinition = "boolean default false")
    private boolean isMock;
}
