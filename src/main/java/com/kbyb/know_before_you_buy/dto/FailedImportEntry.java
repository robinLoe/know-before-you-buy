package com.kbyb.know_before_you_buy.dto;

/**
 * Data Transfer Object (DTO) to represent a single failed entry during a CSV import.
 * This object holds information about the failed entry, such as the device name or line number and the error message
 */
public class FailedImportEntry {
    private String deviceNameOrLine;
    private String errorMessage;

    public FailedImportEntry(String deviceNameOrLine, String errorMessage) {
        this.deviceNameOrLine = deviceNameOrLine;
        this.errorMessage = errorMessage;
    }

    public String getDeviceNameOrLine() {
        return deviceNameOrLine;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}