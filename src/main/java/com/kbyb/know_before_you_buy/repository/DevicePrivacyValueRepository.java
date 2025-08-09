package com.kbyb.know_before_you_buy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kbyb.know_before_you_buy.model.DevicePrivacyValue;

public interface DevicePrivacyValueRepository extends JpaRepository<DevicePrivacyValue, Integer>{
    
}
