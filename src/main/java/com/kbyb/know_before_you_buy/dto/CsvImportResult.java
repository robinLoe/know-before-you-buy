package com.kbyb.know_before_you_buy.dto;

import java.util.ArrayList;
import java.util.List;
/**
 * Data Transfer Object (DTO) to hold the results of a CSV import operation.
 * It separates successful and failed imports for a clear report to the user.
 */
public class CsvImportResult {
    // A list to store the names of devices that were successfully imported
    private List<String> successfulImports = new ArrayList<>();
    // A list to store details of imports that failed, including the reason
    private List<FailedImportEntry> failedImports = new ArrayList<>();

    /**
     * Adds a device name to the list of successful imports.
     *
     * @param deviceName The name of the device that was successfully processed.
     */
    public void addSuccess(String deviceName) {
        this.successfulImports.add(deviceName);
    }

    /**
     * Adds a failed import entry to the list of failures.
     *
     * @param failure An object containing details about the failed import, such as the device name or line number and the error message.
     */
    public void addFailure(FailedImportEntry failure) {
        this.failedImports.add(failure);
    }

    // These two are used to create the final response object sent to the client.
    public List<String> getSuccessfulImports() {
        return successfulImports;
    }

    public List<FailedImportEntry> getFailedImports() {
        return failedImports;
    }
}
