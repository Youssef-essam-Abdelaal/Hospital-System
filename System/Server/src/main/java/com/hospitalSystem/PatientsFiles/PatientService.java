package com.hospitalSystem.PatientsFiles;


import com.hospitalSystem.*;
import com.hospitalSystem.TreatmentsFiles.TreatmentRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PatientService {
    private final PatientRepository patRepo;
    private final TreatmentRepository trtRepo;

    @Autowired
    public PatientService(PatientRepository patRepo, TreatmentRepository trtRepo) {
        this.patRepo = patRepo;
        this.trtRepo = trtRepo;
    }

    @Transactional
    public Patient addPatient(AddProto_Patient patInRequest) {

        Optional<Patient> patientOptional = patRepo.findPatientBySsn(patInRequest.getSsn());
        if (patientOptional.isPresent()) {
            throw new IllegalArgumentException("Patient with this social security number: " + patInRequest.getSsn() + " already exists");
        } else if (patInRequest.getName().isBlank()) {//we choose blank because if the name is only spaces we want to throw error
            throw new IllegalStateException("Patient name cannot be empty, white space or null");
        } else if (patInRequest.getSsn().isBlank()) {//we choose blank because if the name is only spaces we want to throw error
            throw new IllegalStateException("Patient Social Security Number cannot be empty, white space or null");
        } else if (patInRequest.getAge() < 0) {
            throw new IllegalArgumentException("Age specified : " + patInRequest.getAge() + ", where age can not be less than zero ");
        }

        Patient patient = fromPrototoPatient(patInRequest);
        patRepo.save(patient);

        return patient;
    }

    @Transactional//in order to roll back if anything happened during the execution
    public List<Patient> addPatients(AddProto_Patients patsInRequest) {
        List<Patient> patients = new ArrayList<>();
        List<AddProto_Patient> temp = patsInRequest.getAddProtoPatientRequestsList();

        for (AddProto_Patient element : temp) {
            patients.add(addPatient(element));
        }

        return patients;
    }

    @Transactional
    public Patient getPatientBYid(Long id) {
        boolean exists = patRepo.existsById(id);
        if (!exists) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Patient with id " + id + " does not exist")
            );
        } else {
            Optional<Patient> optionalPatient = patRepo.findById(id);
            Patient patient;
            //exists condition assures that it is present, so we are safe from the warning
            patient = optionalPatient.get();
            return patient;
        }
    }

    @Transactional
    public List<Patient> getAllPatients() {
        return patRepo.findAll();
    }

    @Transactional
    public void deletePatient(Long id) {
        boolean exists = patRepo.existsById(id);
        if (!exists) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Patient with id " + id + " does not exist")
            );
        } else {
            //exists condition assures that it is present, so we are safe from the warning
            if (trtRepo.existsByTreatmentPatient(patRepo.findById(id).get())) {
                trtRepo.deleteAllByTreatmentPatient(patRepo.findById(id).get());
            }
            patRepo.deleteById(id);
        }
    }

    @Transactional
    public void deletePatients(PatientIds patientIds) {
        List<PatientId> ids = patientIds.getIdsList();

        for (PatientId element : ids) {
            deletePatient(element.getId());
        }
    }

    @Transactional//in order to roll back if anything happened during the execution
    public Patient updatePatient(Proto_Patient proto_patient) {
        //check if the Patient exist
        boolean exists = patRepo.existsById(proto_patient.getId());
        if (!exists) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Patient with id " + proto_patient.getId() + " does not exist")
            );
        }

        Optional<Patient> patientOptional = patRepo.findPatientBySsn(proto_patient.getSsn());
        // if the ssn exists, and it is not me, this is not acceptable
        if (patientOptional.isPresent() && patientOptional.get().getId() != proto_patient.getId()) {
            throw new IllegalArgumentException("Patient with this social security number: " + proto_patient.getSsn() + " already exists for another patient");
        } else if (proto_patient.getName().isBlank() && !proto_patient.getName().isEmpty()) {//we are checking that this is a white space
            throw new IllegalStateException("Patient name cannot be updated to a white space ");
        } else if (proto_patient.getSsn().isBlank() && !proto_patient.getSsn().isEmpty()) {//we are checking that this is a white space
            throw new IllegalStateException("Patient Social Security Number cannot be updated to empty or white space ");
        } else if (proto_patient.getAge() < 0) {
            throw new IllegalArgumentException("Age specified : " + proto_patient.getAge() + ", where age can not be less than zero ");
        }

        //Here we are sure that the patient we want to update already exist and does not violate any constrains
        //exists condition assures that it is present, so we are safe from the warning
        Patient patient = patRepo.findById(proto_patient.getId()).get();

        //check if it is the same as the old, or it is "" most probably because it wasn't sent in the request
        if (!proto_patient.getName().isEmpty()
                && !Objects.equals(patient.getName(), proto_patient.getName())) {
            patient.setName(proto_patient.getName());
        }

        if (!proto_patient.getSsn().isEmpty()
                && !Objects.equals(patient.getSsn(), proto_patient.getSsn())) {
            patient.setSsn(proto_patient.getSsn());
        }
        // check if the request sent include address or no/ we used empty to give the user the chance
        //to send white space if he wants to remove his address
        if (!proto_patient.getAddress().isEmpty()
                && !Objects.equals(patient.getAddress(), proto_patient.getAddress())) {
            patient.setAddress(proto_patient.getAddress());
        }
        if (!proto_patient.getGender().isEmpty()
                && !Objects.equals(patient.getGender(), proto_patient.getGender())) {
            patient.setGender(proto_patient.getGender());
        }
        if (!proto_patient.getPhonenum().isEmpty()
                && !Objects.equals(patient.getPhonenum(), proto_patient.getPhonenum())) {
            patient.setPhonenum(proto_patient.getPhonenum());
        }
        // the zero condition here assure that when we get an update request missing the age will be interpreted as
        //keep the age the same not change the age to 0 , because age default is 0
        if (proto_patient.getAge() != 0 && proto_patient.getAge() != patient.getAge()) {
            patient.setAge(proto_patient.getAge());
        }

        return patient;
    }

    @Transactional
    public List<Patient> updatePatients(Proto_Patients proto_patients) {
        List<Patient> patients = new ArrayList<>();
        List<Proto_Patient> temp = proto_patients.getPatientsInDbList();

        for (Proto_Patient element : temp) {
            patients.add(updatePatient(element));
        }

        return patients;
    }


    public static Patient fromPrototoPatient(AddProto_Patient patInRequest) {
        Patient patient;
        patient = new Patient(
                patInRequest.getName(),
                patInRequest.getAge(),
                patInRequest.getGender(),
                patInRequest.getSsn(),
                patInRequest.getAddress(),
                patInRequest.getPhonenum()
        );
        return patient;
    }

    public static Proto_Patient fromPatientToProto(Patient patient) {
        Proto_Patient patInResponse;
        //because proto patient cannot set to be null
        if (patient.getGender() == null) {
            patient.setGender("null");
        }
        if (patient.getAddress() == null) {
            patient.setAddress("null");
        }
        if (patient.getPhonenum() == null) {
            patient.setPhonenum("null");
        }
        patInResponse = Proto_Patient.newBuilder()
                .setId(patient.getId())
                .setName(patient.getName())
                .setAge(patient.getAge())
                .setGender(patient.getGender())
                .setSsn(patient.getSsn())
                .setAddress(patient.getAddress())
                .setPhonenum(patient.getPhonenum())
                .build();
        return patInResponse;
    }

    public static Proto_Patients createRepeatedPatients(List<Patient> L) {
        Proto_Patients protoPatientsResponse;
        List<Proto_Patient> temp = new ArrayList<>();
        for (Patient element : L) {
            temp.add(fromPatientToProto(element));
        }

        Proto_Patients.Builder builder = Proto_Patients.newBuilder().addAllPatientsInDb(temp);
        protoPatientsResponse = builder.build();

        return protoPatientsResponse;
    }
}

