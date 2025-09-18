package com.kbyb.know_before_you_buy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Represents a Device entity in the database.
 * This class is a JPA entity that maps to a database table. It uses Lombok's @Data annotation to
 * automatically generate boilerplate code like getters, setters, toString, equals, and hashCode.
 */
@Entity
@Data
public class Device {
    /**
     * The @Id annotation marks this field as the primary key of the entity.
     * @GeneratedValue configures the primary key generation strategy to be IDENTITY,
     * which relies on an auto-incremented database column.
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String deviceType;

    private String functionalClassification;

    @Column(length = 2048) // to support long urls
    private String imageUrl;

    // The URL for the device's product page.
    @Column(length = 2048) // to support long urls
    private String url;
}
