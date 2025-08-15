package com.kbyb.know_before_you_buy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kbyb.know_before_you_buy.dto.PrivacyValueDTO;
import com.kbyb.know_before_you_buy.model.DevicePrivacyValue;

import jakarta.transaction.Transactional;

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

    //Retrieves all privacy values for a given deviceId as lightweight DTOs.
    @Query("""
        SELECT new com.kbyb.know_before_you_buy.dto.PrivacyValueDTO(
            pp.id,
            pp.name,
            dpv.value,
            dpv.isMock
        )
        FROM DevicePrivacyValue dpv
        JOIN dpv.privacyProperty pp
        WHERE dpv.device.id = :deviceId
    """)
    List<PrivacyValueDTO> findPrivacyValuesByDeviceId(@Param("deviceId") Integer deviceId);

    //Retrieves all privacy values for a given deviceName as lightweight DTOs.
    @Query("""
        SELECT new com.kbyb.know_before_you_buy.dto.PrivacyValueDTO(
            pp.id,
            pp.name,
            dpv.value,
            dpv.isMock
        )
        FROM DevicePrivacyValue dpv
        JOIN dpv.privacyProperty pp
        JOIN dpv.device d
        WHERE d.name = :deviceName
    """)
    List<PrivacyValueDTO> findPrivacyValuesByDeviceName(@Param("deviceName") String deviceName);

    @Query("SELECT dpv FROM DevicePrivacyValue dpv WHERE dpv.device.id = :deviceId AND dpv.privacyProperty.id = :propertyId")
    Optional<DevicePrivacyValue> getDpvByDeviceIdAndPropertyId(@Param("deviceId") Integer deviceId, @Param("propertyId") Integer propertyId);

    @Query("SELECT dpv FROM DevicePrivacyValue dpv WHERE dpv.device.name = :deviceName AND dpv.privacyProperty.id = :propertyId")
    Optional<DevicePrivacyValue> getDpvByDeviceNameAndPropertyId(@Param("deviceName") String deviceName, @Param("propertyId") Integer propertyId);

    @Modifying // tells Spring Data this query modifies data instead of selecting it
    @Transactional // ensures the delete happens in a transaction, so either the whole transaction works or not at all
    @Query("DELETE FROM DevicePrivacyValue dpv WHERE dpv.device.id = :deviceId")
    void deleteByDeviceId(@Param("deviceId") Integer deviceId);

    @Modifying // tells Spring Data this query modifies data instead of selecting it
    @Transactional // ensures the delete happens in a transaction, so either the whole transaction works or not at all
    @Query("DELETE FROM DevicePrivacyValue dpv WHERE dpv.device.name = :deviceName")
    void deleteByDeviceName(@Param("deviceName") String deviceName);

    @Modifying // tells Spring Data this query modifies data instead of selecting it
    @Transactional // ensures the delete happens in a transaction, so either the whole transaction works or not at all
    @Query("DELETE FROM DevicePrivacyValue dpv WHERE dpv.privacyProperty.id = :privacyPropertyId")
    void deleteByPrivacyPropertyId(@Param("privacyPropertyId") Integer privacyPropertyId);

    @Modifying // tells Spring Data this query modifies data instead of selecting it
    @Transactional // ensures the delete happens in a transaction, so either the whole transaction works or not at all
    @Query("DELETE FROM DevicePrivacyValue dpv WHERE dpv.privacyProperty.name = :privacyPropertyName")
    void deleteByPrivacyPropertyName(@Param("privacyPropertyName") String privacyPropertyName);

    @Query("DELETE FROM DevicePrivacyValue dpv WHERE dpv.device.id = :deviceId AND dpv.privacyProperty.id = :propertyId")
    void deleteByPrivacyPropertyIdAndDeviceId(@Param("deviceId") Integer deviceId, @Param("propertyId") Integer propertyId);
}
