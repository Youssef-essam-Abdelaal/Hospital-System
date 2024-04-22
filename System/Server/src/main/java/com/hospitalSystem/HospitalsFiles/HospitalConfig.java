package com.hospitalSystem.HospitalsFiles;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;
@Configuration
public class HospitalConfig {

    @Bean
    @Order(2) // Ensures this bean runs after the patientRunner
    CommandLineRunner CommandLineRunnerHospital(HospitalRepository hosRepo){
        return args -> {
            Hospital elsalam = new Hospital(
                    "EL-Salam",
                    "Cairo, Egypt",
                    "General"
            );
            Hospital heidelberg = new Hospital(
                    "University Hospital Heidelberg",
                    "Heidelberg, Germany",
                    "Surgery"
            );
            Hospital gesundheit = new Hospital(
                    "Gesundheit",
                    "31 Marienplatz, Munich, Germany",
                    ""
            );
            Hospital recover = new Hospital(
                    "Speedy Recovery",
                    "New York, USA",
                    "Emergency"
            );
            Hospital Paul = new Hospital(
                    "Paul Matheo Hospital",
                    "Berlin, Germany",
                    null
            );
            hosRepo.saveAll(
                    List.of(elsalam,heidelberg,gesundheit,recover,Paul)
            );
        };
    }
}
