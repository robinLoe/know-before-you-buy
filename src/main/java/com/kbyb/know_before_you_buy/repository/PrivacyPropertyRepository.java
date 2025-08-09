package com.kbyb.know_before_you_buy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kbyb.know_before_you_buy.model.PrivacyProperty;

public interface PrivacyPropertyRepository extends JpaRepository<PrivacyProperty, Integer>{
    
}
