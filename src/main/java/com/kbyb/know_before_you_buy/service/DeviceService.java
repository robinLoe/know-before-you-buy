package com.kbyb.know_before_you_buy.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kbyb.know_before_you_buy.dto.CsvImportResult;
import com.kbyb.know_before_you_buy.dto.DeviceWithPrivacyValuesDTO;
import com.kbyb.know_before_you_buy.dto.FailedImportEntry;
import com.kbyb.know_before_you_buy.dto.PrivacyValueDTO;
import com.kbyb.know_before_you_buy.model.Device;
import com.kbyb.know_before_you_buy.model.DevicePrivacyValue;
import com.kbyb.know_before_you_buy.model.PrivacyProperty;
import com.kbyb.know_before_you_buy.repository.DevicePrivacyValueRepository;
import com.kbyb.know_before_you_buy.repository.DeviceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

// Service class for managing Device entities and related operations.
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DevicePrivacyValueRepository devicePrivacyValueRepository;
    private final PrivacyPropertyService privacyPropertyService;
    private final DevicePrivacyValueService devicePrivacyValueService;

    // Finds and returns a list of all devices.
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    // Finds and returns a list of the names of all devices.
    public ArrayList<String> findAllNames() {
        return deviceRepository.findAllNames();
    }

    // Retrieves all devices along with their associated privacy values, mapped to a DTO.
    public List<DeviceWithPrivacyValuesDTO> getAllDevicesWithPrivacyProperties() {
        List<DeviceWithPrivacyValuesDTO> devicesWithPrivacyValuesDTOs = new ArrayList<>();
        List<Device> allDevicesWithoutPP = deviceRepository.findAll();
        for (Device deviceWithoutPP : allDevicesWithoutPP) {
            List<PrivacyValueDTO> privacyValues = devicePrivacyValueRepository.findPrivacyValuesByDeviceId(deviceWithoutPP.getId());
            devicesWithPrivacyValuesDTOs.add(new DeviceWithPrivacyValuesDTO(deviceWithoutPP, privacyValues));
        }
        return devicesWithPrivacyValuesDTOs;
    }

    // Finds a single device by its ID.
    public Optional<Device> findById(Integer id) {
        return deviceRepository.findById(id);
    }

    // Saves a new device to the database. Throws a RuntimeException if a device with the same name already exists.
    public Device save(Device device) {
        if (findAllNames().contains(device.getName())) {
            throw new RuntimeException("Device with the name " + device.getName() + " already exists.");
        } else {
            return deviceRepository.save(device);
        }
    }

    // Deletes a device by its ID.
    public void deleteById(Integer id) {
        deviceRepository.deleteById(id);
    }

    public DeviceWithPrivacyValuesDTO getDeviceWithPrivacyValues(String name) {
        Device device = deviceRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        List<PrivacyValueDTO> privacyValues = devicePrivacyValueRepository
                .findPrivacyValuesByDeviceName(name);

        return new DeviceWithPrivacyValuesDTO(device, privacyValues);
    }

    // Retrieves a device and its associated privacy values by the device name, mapped to a DTO.
    // Throws a RuntimeException if the device is not found.
    @Transactional
    public Device createDeviceWithValues(DeviceWithPrivacyValuesDTO dto) {
        // Creates the base Device object from DTO
        Device device = buildDevice(dto);
        Device savedDevice = save(device);

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
     * Finds the PrivacyProperty either by ID or by name. Throws an exception if
     * neither is provided or the property is not found.
     */
    private PrivacyProperty findPrivacyProperty(PrivacyValueDTO pv) {
        if (pv.getPropertyId() != null) {
            return privacyPropertyService.findById(pv.getPropertyId());
        }
        if (pv.getPropertyName() != null) {
            return privacyPropertyService.findByName(pv.getPropertyName());
        }
        throw new IllegalArgumentException("privacyValue must include propertyId or propertyName");
    }

    /**
     * Creates and validates a DevicePrivacyValue for the given device and
     * property. If the property is marked as "validatable", the value must
     * match one of the allowed values.
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
                        + ".Has to be one of these:" + prop.getAllowedValues()
                        + "but was" + pv.getValue()
                );
            }
        } else {
            // No validation needed
            dpv.setValue(pv.getValue());
        }

        return dpv;
    }

    @Transactional
    public CsvImportResult createDevicesFromCSV(String csvContent) throws IOException {
        CsvImportResult result = new CsvImportResult();
        try (BufferedReader br = new BufferedReader(new StringReader(csvContent))) {
            String line;
            boolean firstLine = true;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                // First line are the column names
                if (firstLine) {
                    String[] columnNames = line.split(";", -1);
                    checkCsvPattern(columnNames);
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(";", -1);
                // if fields.length is not equal to 57, the csv has not the correct format (5 Device attributes
                // + 52 privacy properties = 57)
                if (fields.length != 57) {
                    result.addFailure(new FailedImportEntry("Line " + lineNumber, "Invalid CSV format: Expected 57 fields, but found " + fields.length));
                    //throw new IOException("Invalid CSV format on line " + lineNumber + ": Expected 57 fields, but found " + fields.length);
                    continue;
                }
                Device savedDevice = null;
                try {
                    Device device = new Device();
                    device.setName(fields[0]);
                    device.setDeviceType(fields[1]);
                    device.setFunctionalClassification(fields[2]);
                    device.setImageUrl(fields[3]);
                    device.setUrl(fields[4]);
                    savedDevice = save(device);

                    for (int i = 5; i < fields.length; i++) {
                        if (!(fields[i].equalsIgnoreCase("not applicable") || fields[i].equalsIgnoreCase("not findable"))) {
                            PrivacyValueDTO privacyValueDTO = new PrivacyValueDTO(null, getColumnName(i - 5), fields[i], false);
                            PrivacyProperty property = findPrivacyProperty(privacyValueDTO);
                            DevicePrivacyValue dpv = buildDevicePrivacyValue(savedDevice, property, privacyValueDTO);
                            devicePrivacyValueService.save(dpv);
                        }
                    }
                    result.addSuccess(savedDevice.getName());
                } catch (RuntimeException e) {
                    System.err.println("Fehler beim Verarbeiten der Zeile: " + e.getMessage());
                    // if the device was saved successfully, but the creation of devicePrivacyValues doesnt, the device gets deleted
                    if (savedDevice != null) {
                        deleteById(savedDevice.getId());
                    }
                    result.addFailure(new FailedImportEntry(fields[0], e.getMessage()));
                }
            }
        }
        return result;
    }

    // Checks if the CSV header matches the expected pattern.
    private void checkCsvPattern(String[] columnNames) throws IOException {
        String[] correctPattern = getColumnNamesPattern();
        for (int i = 0; i < columnNames.length; i++) {
            if (!columnNames[i].toLowerCase().trim().equals(correctPattern[i].toLowerCase().trim())) {
                throw new IOException("Invalid CSV Pattern. Column " + i + " should be " + correctPattern[i] + " but was " + columnNames[i]);
            }
        }
    }

    // Returns the name of the privacy property for a given index.
    private String getColumnName(int i) {
        String[] columnNames = {
            "Compliance with Cyber Resilience Act",
            "Compliance Support",
            "Separate Data Protection Information from Terms & Conditions",
            "Secure Default Settings",
            "Resetting to Secure Default Settings",
            "Data Minimization",
            "Legal Grounds",
            "Control of Sent Data",
            "Control of Data Recipient",
            "Special Categories of Data",
            "Actors Involved in the Processing",
            "Availability of Contracts for Joint Controllers/Processors",
            "Clear and Transparent Personal Data Processing Information",
            "Recipient of Information",
            "App Permissions Required",
            "Clarification of Data Processing Process",
            "Data Sharing",
            "Encryption Status for User Passwords",
            "Optional Security Review",
            "Authentication Information",
            "Intuitive Privacy Control Interface",
            "Visual Representation of Privacy Flow",
            "User Controlled Data Processing Location",
            "Contextual Data Disclosure",
            "Additional Transparency Mechanisms",
            "Different Granularity of Privacy Settings of UI",
            "Profiling",
            "Subject Rights",
            "Listing Non-Personally Identifiable Information",
            "(Security) Update Status Display",
            "Exploration Functionality",
            "Investigation Functionality",
            "Being Notified When Something Needs Attention",
            "User Control",
            "User Interaction and Engagement",
            "Privacy Enhancing Technologies",
            "Data Portability",
            "Backend Service Demands",
            "Updateability",
            "History of Data Leakage",
            "Privacy Breaches",
            "Open Source",
            "TLS Certificate Validation",
            "Duration and the Company/Entity of Data Being Stored",
            "Purpose of Data Being Collected",
            "Frequency of Data Being Sent",
            "Data Processing Outside the EU",
            "Location of Data Being Sent To",
            "Controllable Discovery Function",
            "On-device Data Processing",
            "Account Required for Device Bootstrapping",
            "Ensure Informed Consent to Changes in Data Processing"
        };

        return columnNames[i];
    }

    // Returns the expected CSV header pattern.
    private String[] getColumnNamesPattern() {
        String[] columnNamesPatter = {
            "name",
            "devicetype",
            "functionalclassification",
            "url",
            "imageurl",
            "Compliance with Cyber Resilience Act",
            "Compliance Support",
            "Separate Data Protection Information from Terms & Conditions",
            "Secure Default Settings",
            "Resetting to Secure Default Settings",
            "Data Minimization",
            "Legal Grounds",
            "Control of Sent Data",
            "Control of Data Recipient",
            "Special Categories of Data",
            "Actors Involved in the Processing",
            "Availability of Contracts for Joint Controllers/Processors",
            "Clear and Transparent Personal Data Processing Information",
            "Recipient of Information",
            "App Permissions Required",
            "Clarification of Data Processing Process",
            "Data Sharing",
            "Encryption Status for User Passwords",
            "Optional Security Review",
            "Authentication Information",
            "Intuitive Privacy Control Interface",
            "Visual Representation of Privacy Flow",
            "User Controlled Data Processing Location",
            "Contextual Data Disclosure",
            "Additional Transparency Mechanisms",
            "Different Granularity of Privacy Settings of UI",
            "Profiling",
            "Subject Rights",
            "Listing Non-Personally Identifiable Information",
            "(Security) Update Status Display",
            "Exploration Functionality",
            "Investigation Functionality",
            "Being Notified When Something Needs Attention",
            "User Control",
            "User Interaction and Engagement",
            "Privacy Enhancing Technologies",
            "Data Portability",
            "Backend Service Demands",
            "Updateability",
            "History of Data Leakage",
            "Privacy Breaches",
            "Open Source",
            "TLS Certificate Validation",
            "Duration and the Company/Entity of Data Being Stored",
            "Purpose of Data Being Collected",
            "Frequency of Data Being Sent",
            "Data Processing Outside the EU",
            "Location of Data Being Sent To",
            "Controllable Discovery Function",
            "On-device Data Processing",
            "Account Required for Device Bootstrapping",
            "Ensure Informed Consent to Changes in Data Processing"
        };
        return columnNamesPatter;
    }

}
