package com.hospitalSystem.TreatmentsFiles;

import com.hospitalSystem.HospitalsFiles.HospitalRepository;
import com.hospitalSystem.PatientsFiles.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class TreatmentConfig {
    @Bean
    @Order(3) // Ensures this bean runs after the patientRunner and hospitalRunner
    CommandLineRunner CommandLineRunnerTreatment(TreatmentRepository trtRepo, PatientRepository patRepo, HospitalRepository hosRepo){
        return args -> {
            Treatment T1 =new Treatment(
                    //we already added them in the patient and hospital config so need for is present check
                    patRepo.findById(1L).get(),
                    hosRepo.findById(1L).get(),
                    "Heart Attack",
                    "31-01-2000"
            );
            Treatment T2 =new Treatment(
                    //we already added them in the patient and hospital config so need for is present check
                    patRepo.findById(7L).get(),
                    hosRepo.findById(2L).get(),
                    "Teeth",
                    "28-02-2019"
            );
            Treatment T3 =new Treatment(
                    //we already added them in the patient and hospital config so need for is present check
                    patRepo.findById(1L).get(),
                    hosRepo.findById(4L).get(),
                    "",
                    "13-07-2005"
            );
            Treatment T4 =new Treatment(
                    //we already added them in the patient and hospital config so need for is present check
                    patRepo.findById(1L).get(),
                    hosRepo.findById(5L).get(),
                    "Check up",
                    "04-12-2008"
            );
            Treatment T5 =new Treatment(
                    //we already added them in the patient and hospital config so need for is present check
                    patRepo.findById(7L).get(),
                    hosRepo.findById(3L).get(),
                    "Emergency",
                    ""
            );
            Treatment T6 =new Treatment(
                    //we already added them in the patient and hospital config so need for is present check
                    patRepo.findById(4L).get(),
                    hosRepo.findById(1L).get(),
                    "",
                    ""
            );
            Treatment T7 =new Treatment(
                    //we already added them in the patient and hospital config so need for is present check
                    patRepo.findById(2L).get(),
                    hosRepo.findById(3L).get(),
                    "Headache",
                    ""
            );
            Treatment T8 =new Treatment(
                    //we already added them in the patient and hospital config so need for is present check
                    patRepo.findById(6L).get(),
                    hosRepo.findById(2L).get(),
                    "broken leg",
                    "08-09-2023"
            );
            Treatment T9 =new Treatment(
                    //we already added them in the patient and hospital config so need for is present check
                    patRepo.findById(2L).get(),
                    hosRepo.findById(4L).get(),
                    "",
                    "21-03-2022"
            );
            Treatment T10 =new Treatment(
                    //we already added them in the patient and hospital config so need for is present check
                    patRepo.findById(2L).get(),
                    hosRepo.findById(1L).get(),
                    "NA",
                    "19-04-2024"
            );
            trtRepo.saveAll(
                    List.of(T1,T2,T3,T4,T5,T6,T7,T8,T9,T10)
            );
        };
    }
}


