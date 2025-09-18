package com.kbyb.know_before_you_buy.dto;

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