package com.hospitalSystem.HospitalsFiles;

import com.hospitalSystem.*;
import com.hospitalSystem.TreatmentsFiles.TreatmentRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class HospitalService {
    private final HospitalRepository hosRepo;
    private final TreatmentRepository trtRepo;

    @Autowired
    public HospitalService(HospitalRepository hosRepo, TreatmentRepository trtRepo) {
        this.hosRepo = hosRepo;
        this.trtRepo = trtRepo;
    }

    @Transactional
    public Hospital addHospital(AddProto_Hospital hosInRequest) {

        if (hosInRequest.getName().isBlank()) {//we choose blank because if the name is only spaces we want to throw error
            throw new IllegalStateException("Hospital name cannot be empty, white space or null");
        }
        Hospital hospital = fromPrototoHospital(hosInRequest);
        hosRepo.save(hospital);

        return hospital;
    }

    @Transactional //in order to roll back if anything happened during the execution
    public List<Hospital> addHospitals(AddProto_Hospitals hosInRequest) {
        List<Hospital> hospitals = new ArrayList<>();
        List<AddProto_Hospital> temp = hosInRequest.getAddProtoHospitalRequestsList();

        for (AddProto_Hospital element : temp) {
            hospitals.add(addHospital(element));
        }

        return hospitals;
    }

    @Transactional
    public Hospital getHospitalBYid(Long id) {
        boolean exists = hosRepo.existsById(id);
        if (!exists) {
            throw new StatusRuntimeException(Status.NOT_FOUND.withDescription("Hospital with id " + id + " does not exist"));
        } else {
            Optional<Hospital> optionalHospital = hosRepo.findById(id);
            Hospital hospital;
            //exists condition assures that it is present, so we are safe from the warning
            hospital = optionalHospital.get();
            return hospital;
        }
    }

    @Transactional
    public List<Hospital> getAllHospitals() {
        return hosRepo.findAll();
    }

    @Transactional
    public void deleteHospital(Long id) {
        boolean exists = hosRepo.existsById(id);
        if (!exists) {
            throw new StatusRuntimeException(Status.NOT_FOUND.withDescription("Hospital with id " + id + " does not exist"));
        } else {
            //exists condition assures that it is present, so we are safe from the warning
            if (trtRepo.existsByTreatmentHospital(hosRepo.findById(id).get())) {
                trtRepo.deleteAllByTreatmentHospital(hosRepo.findById(id).get());
            }
            hosRepo.deleteById(id);
        }
    }

    @Transactional
    public void deleteHospitals(HospitalIds hospitalIds) {
        List<HospitalId> ids = hospitalIds.getIdsList();
        for (HospitalId element : ids) {
            deleteHospital(element.getId());
        }
    }

    @Transactional//in order to roll back if anything happened during the execution
    public Hospital updateHospital(Proto_Hospital proto_hospital) {
        //check if the Hospital exist
        boolean exists = hosRepo.existsById(proto_hospital.getId());
        if (!exists) {
            throw new StatusRuntimeException(Status.NOT_FOUND.withDescription("Hospital with id " + proto_hospital.getId() + " does not exist"));
        }

        if (proto_hospital.getName().isBlank() && !proto_hospital.getName().isEmpty()) {//we are checking that this is a white space
            throw new IllegalStateException("Hospital name cannot be updated to a white space ");
        }

        //Here we are sure that the Hospital we want to update already exist and does not violate any constrains
        //exists condition assures that it is present, so we are safe from the warning
        Hospital hospital = hosRepo.findById(proto_hospital.getId()).get();

        //check if it is the same as the old, or it is "" most probably because it wasn't sent in the request
        if (!proto_hospital.getName().isEmpty() && !Objects.equals(hospital.getName(), proto_hospital.getName())) {
            hospital.setName(proto_hospital.getName());
        }

        // check if the request sent include address or no/ we used empty to give the user the chance
        //to send white space if he wants to remove his address
        if (!proto_hospital.getAddress().isEmpty() && !Objects.equals(hospital.getAddress(), proto_hospital.getAddress())) {
            hospital.setAddress(proto_hospital.getAddress());
        }

        if (!proto_hospital.getSpecialization().isEmpty() && !Objects.equals(hospital.getSpecialization(), proto_hospital.getSpecialization())) {
            hospital.setSpecialization(proto_hospital.getSpecialization());
        }

        return hospital;
    }

    @Transactional
    public List<Hospital> updateHospitals(Proto_Hospitals proto_hospitals) {
        List<Hospital> hospitals = new ArrayList<>();
        List<Proto_Hospital> temp = proto_hospitals.getHospitalsInDbList();

        for (Proto_Hospital element : temp) {
            hospitals.add(updateHospital(element));
        }

        return hospitals;
    }

    public static Hospital fromPrototoHospital(AddProto_Hospital hosInRequest) {
        Hospital hospital;
        hospital = new Hospital(hosInRequest.getName(), hosInRequest.getAddress(), hosInRequest.getSpecialization());
        return hospital;
    }

    public static Proto_Hospital fromHospitalToProto(Hospital hospital) {
        Proto_Hospital hosInResponse;
        //because proto hospital cannot set to be null
        if (hospital.getAddress() == null) {
            hospital.setAddress("null");
        }
        if (hospital.getSpecialization() == null) {
            hospital.setSpecialization("null");
        }
        hosInResponse = Proto_Hospital.newBuilder().setId(hospital.getId()).setName(hospital.getName()).setAddress(hospital.getAddress()).setSpecialization(hospital.getSpecialization()).build();
        return hosInResponse;
    }

    public static Proto_Hospitals createRepeatedHospitals(List<Hospital> L) {
        Proto_Hospitals protoHospitalsResponse;
        List<Proto_Hospital> temp = new ArrayList<>();
        for (Hospital element : L) {
            temp.add(fromHospitalToProto(element));
        }

        Proto_Hospitals.Builder builder = Proto_Hospitals.newBuilder().addAllHospitalsInDb(temp);
        protoHospitalsResponse = builder.build();

        return protoHospitalsResponse;
    }

}
