package com.kbyb.know_before_you_buy.dto;

import lombok.Data;
import java.util.List;

import com.kbyb.know_before_you_buy.model.Device;

@Data
public class DeviceWithPrivacyValuesDTO {
    private Integer deviceId;
    private String name;
    private String deviceType;
    private String functionalClassification;
    private String imageUrl;
    private String url;
    private List<PrivacyValueDTO> privacyValues;

    public DeviceWithPrivacyValuesDTO(Integer deviceId, String deviceName, List<PrivacyValueDTO> privacyValues){
        this.deviceId = deviceId;
        this.name = deviceName;
        this.privacyValues = privacyValues;
    }

    public DeviceWithPrivacyValuesDTO(Device device, List<PrivacyValueDTO> privacyValues){
        this.deviceId  = device.getId();
        this.name = device.getName();
        this.deviceType = device.getDeviceType();
        this.functionalClassification = device.getFunctionalClassification();
        this.imageUrl = device.getImageUrl();
        this.url = device.getUrl();
        this.privacyValues = privacyValues;
    }
}