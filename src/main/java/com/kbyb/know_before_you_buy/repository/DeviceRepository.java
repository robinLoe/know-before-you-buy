package com.kbyb.know_before_you_buy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kbyb.know_before_you_buy.model.Device;

public interface DeviceRepository extends JpaRepository<Device, Integer>{
    
}
