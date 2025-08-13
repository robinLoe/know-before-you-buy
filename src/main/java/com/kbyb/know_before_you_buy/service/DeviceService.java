package com.kbyb.know_before_you_buy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kbyb.know_before_you_buy.dto.DeviceWithPrivacyValuesDTO;
import com.kbyb.know_before_you_buy.dto.PrivacyValueDTO;
import com.kbyb.know_before_you_buy.model.Device;
import com.kbyb.know_before_you_buy.repository.DevicePrivacyValueRepository;
import com.kbyb.know_before_you_buy.repository.DeviceRepository;

@Service
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final DevicePrivacyValueRepository devicePrivacyValueRepository;
    
    public DeviceService(DeviceRepository deviceRepository, DevicePrivacyValueRepository devicePrivacyValueRepository) {
        this.deviceRepository = deviceRepository;
        this.devicePrivacyValueRepository = devicePrivacyValueRepository;
    }
    
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
}
