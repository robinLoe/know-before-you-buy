package com.kbyb.know_before_you_buy.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kbyb.know_before_you_buy.repository.PrivacyPropertyRepository;
import com.kbyb.know_before_you_buy.service.PrivacyPropertyCsvImportService;

@Configuration
public class DataLoaderConfig {

    /**
     * Initializes the database with privacy properties from a CSV file
     * if the corresponding table is empty.
     * The CommandLineRunner is a Spring Boot feature that runs a block of code
     * right after the application context is loaded. This is useful for one-time
     * setup tasks like populating a database with initial data.
     */
    @Bean
    @SuppressWarnings("unused")
    CommandLineRunner initDatabase(PrivacyPropertyCsvImportService importService,
                                    PrivacyPropertyRepository repository) {
        return args -> {
            if (repository.count() == 0) { // runs only if the table is empty
                importService.importFromCsv("src/main/resources/PrivacyPropertiesCSV.csv");
                System.out.println("Privacy Properties are imported.");
            } else {
                System.out.println("Privacy Properties already exist.");
            }
        };
    }
}
