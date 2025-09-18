package com.kbyb.know_before_you_buy.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kbyb.know_before_you_buy.model.PrivacyProperty;
import com.kbyb.know_before_you_buy.repository.PrivacyPropertyRepository;

// Service class for managing PrivacyProperty entities.
@Service
public class PrivacyPropertyService {

    private final PrivacyPropertyRepository repository;

    public PrivacyPropertyService(PrivacyPropertyRepository repository) {
        this.repository = repository;
    }

    // Retrieves all PrivacyProperty entities from the database.
    public List<PrivacyProperty> findAll() {
        return repository.findAll();
    }

    // Finds a PrivacyProperty by its ID. Throws a RuntimeException if not found.
    public PrivacyProperty findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("PrivacyProperty with the ID: " + id + " was not found"));
    }

    // Finds a PrivacyProperty by its name. Throws a RuntimeException if not found.
    public PrivacyProperty findByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new RuntimeException("PrivacyProperty with the name: " + name + " was not found"));
    } 

    // Saves a PrivacyProperty entity to the database.
    public PrivacyProperty save(PrivacyProperty pp) {
        return repository.save(pp);
    }

    // Deletes a PrivacyProperty by its ID.
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    // Deletes a PrivacyProperty by its name.
    public void deleteByName(String name) {
        Integer id = findByName(name).getId();
        repository.deleteById(id);
    }
}