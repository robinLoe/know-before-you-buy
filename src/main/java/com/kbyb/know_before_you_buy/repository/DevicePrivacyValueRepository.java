package com.kbyb.know_before_you_buy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kbyb.know_before_you_buy.model.DevicePrivacyValue;

@Repository
public interface DevicePrivacyValueRepository extends JpaRepository<DevicePrivacyValue, Integer>{
    @Query("""
        SELECT dpv 
        FROM DevicePrivacyValue dpv
        JOIN FETCH dpv.privacyProperty pp
        JOIN dpv.device d
        WHERE d.name = :deviceName
    """)
    List<DevicePrivacyValue> findAllByDeviceName(@Param("deviceName") String deviceName);
}
