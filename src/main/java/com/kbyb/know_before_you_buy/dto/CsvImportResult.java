package com.kbyb.know_before_you_buy.dto;

import java.util.ArrayList;
import java.util.List;

public class CsvImportResult {
    private List<String> successfulImports = new ArrayList<>();
    private List<FailedImportEntry> failedImports = new ArrayList<>();

    public void addSuccess(String deviceName) {
        this.successfulImports.add(deviceName);
    }

    public void addFailure(FailedImportEntry failure) {
        this.failedImports.add(failure);
    }

    public List<String> getSuccessfulImports() {
        return successfulImports;
    }

    public List<FailedImportEntry> getFailedImports() {
        return failedImports;
    }
}
