package com.kbyb.know_before_you_buy.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kbyb.know_before_you_buy.repository.PrivacyPropertyRepository;
import com.kbyb.know_before_you_buy.service.PrivacyPropertyCsvImportService;

@Configuration
public class DataLoaderConfig {

    @Bean
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
