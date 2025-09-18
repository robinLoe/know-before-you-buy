package com.kbyb.know_before_you_buy.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.kbyb.know_before_you_buy.model.PrivacyProperty;
import com.kbyb.know_before_you_buy.repository.PrivacyPropertyRepository;

// Service class for importing privacy properties from a CSV file.
@Service
public class PrivacyPropertyCsvImportService {

    private final PrivacyPropertyRepository repository;

    public PrivacyPropertyCsvImportService(PrivacyPropertyRepository repository) {
        this.repository = repository;
    }

    /**
     * Imports privacy properties from a specified CSV file.
     * The method reads the file line by line, skips the header, and then
     * parses each line to create and save a new PrivacyProperty entity.
     *
     * @param filePath The path to the CSV file.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public void importFromCsv(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                // First line is the header which isnt needed
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(";", -1);

                PrivacyProperty property = new PrivacyProperty();
                property.setId(Integer.valueOf(fields[0].trim()));  // Property ID
                property.setName(fields[1].trim());                  // Name
                property.setDescription(fields[2].trim());           // Description
                property.setCategory(fields[3].trim());              // Category
                property.setMetric(fields[4].trim());                // Metric
                boolean validatable = Boolean.parseBoolean(fields[5].trim());
                property.setValidatable(validatable);
                if(validatable){
                    property.setAllowedValues(fields[6].trim());
                }

                repository.save(property);
            }
        }
    }
}
