package com.kbyb.know_before_you_buy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


/**
 * Represents a Privacy Property entity in the database.
 * This class is a JPA entity that maps to a database table. It uses Lombok's @Data annotation to
 * automatically generate boilerplate code like getters, setters, toString, equals, and hashCode.
 */
@Entity
@Data
public class PrivacyProperty {

    // The id is not automatically generated, because the privacy properties have specific IDs in 
    // the underlying excel file of the task
    @Id
    private Integer id;

    private String name;

    // columnDefinition = "text", because the standard capacity for String is 255
    @Column(columnDefinition = "text")
    private String description;

    private String category;

    // columnDefinition = "text", because the standard capacity for String is 255
    @Column(columnDefinition = "text")
    private String metric;

    // standard value is false, but if allowed Values are applicable, the sent value can be checked
    private boolean validatable;

    // a comma-seperated String with two values
    private String allowedValues;
    
}
