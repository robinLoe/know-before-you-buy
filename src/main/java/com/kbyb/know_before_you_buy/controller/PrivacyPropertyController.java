package com.kbyb.know_before_you_buy.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kbyb.know_before_you_buy.model.PrivacyProperty;
import com.kbyb.know_before_you_buy.service.PrivacyPropertyService;

@RestController
@RequestMapping("/privacy-properties")
public class PrivacyPropertyController {

    private final PrivacyPropertyService service;

    public PrivacyPropertyController(PrivacyPropertyService service) {
        this.service = service;
    }

    @GetMapping
    public List<PrivacyProperty> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PrivacyProperty getById(@PathVariable Integer id) {
        return service.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    @PostMapping
    public PrivacyProperty create(@RequestBody PrivacyProperty pp) {
        return service.save(pp);
    }

    @PutMapping("/{id}")
    public PrivacyProperty update(@PathVariable Integer id, @RequestBody PrivacyProperty pp) {
        pp.setId(id);
        return service.save(pp);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteById(id);
    }
}