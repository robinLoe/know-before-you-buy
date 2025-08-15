package com.kbyb.know_before_you_buy.service;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kbyb.know_before_you_buy.model.PrivacyProperty;
import com.kbyb.know_before_you_buy.repository.PrivacyPropertyRepository;

@Service
public class PrivacyPropertyService {

    private final PrivacyPropertyRepository repository;

    public PrivacyPropertyService(PrivacyPropertyRepository repository) {
        this.repository = repository;
    }

    public List<PrivacyProperty> findAll() {
        return repository.findAll();
    }

    public PrivacyProperty findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("PrivacyProperty with the ID: " + id + " was not found"));
    }

    public PrivacyProperty findByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new RuntimeException("PrivacyProperty with the name: " + name + " was not found"));
    } 

    public PrivacyProperty save(PrivacyProperty pp) {
        return repository.save(pp);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public void deleteByName(String name) {
        Integer id = findByName(name).getId();
        repository.deleteById(id);
    }
}