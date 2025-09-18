package com.kbyb.know_before_you_buy.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kbyb.know_before_you_buy.model.Device;

/**
 * Repository interface for Device entities.
 * Extends JpaRepository to provide standard CRUD operations for the Device entity.
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer>{
    // Finds a single Device entity by its name.
    Optional<Device> findByName(String name);

    // Retrieves a list of all device names.
    @Query("SELECT d.name FROM Device d")
    ArrayList<String> findAllNames();
}
