package com.kbyb.know_before_you_buy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kbyb.know_before_you_buy.model.PrivacyProperty;

/**
 * Repository interface for PrivacyProperty entities.
 * Extends JpaRepository to provide standard CRUD operations for the PrivacyProperty entity.
 */
@Repository
public interface PrivacyPropertyRepository extends JpaRepository<PrivacyProperty, Integer>{
    // Finds a single PrivacyProperty entity by its name.
    Optional<PrivacyProperty> findByName(String name);
}
