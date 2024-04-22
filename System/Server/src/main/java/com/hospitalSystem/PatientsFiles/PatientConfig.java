package com.hospitalSystem.PatientsFiles;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class PatientConfig {

    @Bean
    @Order(1) // Ensures this bean run first
    CommandLineRunner CommandLineRunner(PatientRepository patRepo){
        return args -> {
            Patient ahmed = new Patient(
                    "Ahmed Rezk",
                    31,
                    "male",
                    "6666888444",
                    "Munich",
                    "+49578888888"
            );
            Patient anas = new Patient(
                    "Anas Adam",
                    19,
                    "male",
                    "12345877",
                    "Charlottenburg, Berlin",
                    "015788644444"
            );
            Patient muller= new Patient(
                    "Thomas Müller",
                    34,
                    null,
                    "12345",
                    "Alexanderplatz, Berlin",
                    "012766666"
            );
            Patient emilia= new Patient(
                    "Emilia",
                    16,
                    "female",
                    "678543",
                    "NA",
                    "017-555-999"
            );
            Patient mia= new Patient(
                    "Mia Finn",
                    4,
                    "female",
                    "A24018194",
                    "31 KeplerStr, Ulm, Germany",
                    ""
            );
            Patient lina= new Patient(
                    "Lina Felix",
                    72,
                    "female",
                    "3200",
                    "21 Jump street, USA",
                    ""
            );
            Patient marlo= new Patient(
                    "Marlo Samy",
                    37,
                    "Prefer no to say",
                    "88888",
                    "45 Edith Stien weg, Munich, Germany",
                    "01281110018"
            );


                patRepo.saveAll(
                        List.of(ahmed,anas,muller,emilia,mia,lina,marlo)
                );
        };
    }
}
