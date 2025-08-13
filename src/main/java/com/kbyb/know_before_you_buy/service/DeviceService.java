package com.kbyb.know_before_you_buy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kbyb.know_before_you_buy.dto.DeviceWithPrivacyValuesDTO;
import com.kbyb.know_before_you_buy.dto.PrivacyValueDTO;
import com.kbyb.know_before_you_buy.model.Device;
import com.kbyb.know_before_you_buy.model.DevicePrivacyValue;
import com.kbyb.know_before_you_buy.model.PrivacyProperty;
import com.kbyb.know_before_you_buy.repository.DevicePrivacyValueRepository;
import com.kbyb.know_before_you_buy.repository.DeviceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final DevicePrivacyValueRepository devicePrivacyValueRepository;
    private final PrivacyPropertyService privacyPropertyService;
    private final DevicePrivacyValueService devicePrivacyValueService;
    
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }
    
    public Optional<Device> findById(Integer id) {
        return deviceRepository.findById(id);
    }

    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    public void deleteById(Integer id) {
        deviceRepository.deleteById(id);
    }

    public DeviceWithPrivacyValuesDTO getDeviceWithPrivacyValues(String name) {
        Device device = deviceRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        List<PrivacyValueDTO> privacyValues = devicePrivacyValueRepository
                .findAllByDeviceName(name)
                .stream()
                .map(dpv -> new PrivacyValueDTO(
                        dpv.getPrivacyProperty().getName(),
                        dpv.getValue()
                ))
                .toList();

        return new DeviceWithPrivacyValuesDTO(device, privacyValues);
    }


    @Transactional
    public Device createDeviceWithValues(DeviceWithPrivacyValuesDTO dto) {
        // Creates the base Device object from DTO
        Device device = buildDevice(dto);
        Device savedDevice = deviceRepository.save(device);

        // Loop through all provided privacy values and validate + save them
        for (PrivacyValueDTO pv : dto.getPrivacyValues()) {
            PrivacyProperty property = findPrivacyProperty(pv);
            DevicePrivacyValue dpv = buildDevicePrivacyValue(savedDevice, property, pv);
            devicePrivacyValueService.save(dpv);
        }

        return savedDevice;
    }

    /**
     * Builds a Device entity from the given DTO.
     */
    private Device buildDevice(DeviceWithPrivacyValuesDTO dto) {
        Device device = new Device();
        device.setName(dto.getName());
        device.setDeviceType(dto.getDeviceType());
        device.setFunctionalClassification(dto.getFunctionalClassification());
        device.setImageUrl(dto.getImageUrl());
        device.setUrl(dto.getUrl());
        return device;
    }

    /**
     * Finds the PrivacyProperty either by ID or by name.
     * Throws an exception if neither is provided or the property is not found.
     */
    private PrivacyProperty findPrivacyProperty(PrivacyValueDTO pv) {
        if (pv.getPropertyId() != null) {
            return privacyPropertyService.findById(pv.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "The PrivacyProperty with the id = " + pv.getPropertyId() + " was not found"
                ));
        }
        if (pv.getPropertyName() != null) {
            return privacyPropertyService.findByName(pv.getPropertyName())
                .orElseThrow(() -> new IllegalArgumentException(
                    "The PrivacyProperty with the name = " + pv.getPropertyName() + " was not found"
                ));
        }
        throw new IllegalArgumentException("privacyValue must include propertyId or propertyName");
    }

    /**
     * Creates and validates a DevicePrivacyValue for the given device and property.
     * If the property is marked as "validatable", the value must match one of the allowed values.
     */
    private DevicePrivacyValue buildDevicePrivacyValue(Device device, PrivacyProperty prop, PrivacyValueDTO pv) {
        DevicePrivacyValue dpv = new DevicePrivacyValue();
        dpv.setDevice(device);
        dpv.setPrivacyProperty(prop);

        // Validation for allowed values
        if (prop.isValidatable()) {
            String[] allowed = prop.getAllowedValues().split(",");
            String trimmedValue = pv.getValue().trim();

            if (trimmedValue.equalsIgnoreCase(allowed[0].trim())) {
                dpv.setValue(allowed[0].trim());
            } else if (allowed.length > 1 && trimmedValue.equalsIgnoreCase(allowed[1].trim())) {
                dpv.setValue(allowed[1].trim());
            } else {
                throw new IllegalArgumentException(
                    "Invalid value for property: " + prop.getName()
                    + ".\nHas to be one of these:\n" + prop.getAllowedValues()
                    + "\nbut was\n" + pv.getValue()
                );
            }
        } else {
            // No validation needed
            dpv.setValue(pv.getValue());
        }

        return dpv;
    }
}
