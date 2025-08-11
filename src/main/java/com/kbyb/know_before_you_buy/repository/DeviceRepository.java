package com.kbyb.know_before_you_buy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kbyb.know_before_you_buy.model.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer>{
    Optional<Device> findByName(String name);
}
