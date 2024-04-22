package com.hospitalSystem.TreatmentsFiles;

import com.hospitalSystem.*;
import com.hospitalSystem.HospitalsFiles.Hospital;
import com.hospitalSystem.HospitalsFiles.HospitalRepository;
import com.hospitalSystem.HospitalsFiles.HospitalService;
import com.hospitalSystem.PatientsFiles.Patient;
import com.hospitalSystem.PatientsFiles.PatientRepository;
import com.hospitalSystem.PatientsFiles.PatientService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TreatmentService {
    private final PatientService patService;
    private final HospitalService hosService;
    private final TreatmentRepository trtRepo;
    private final PatientRepository patRepo;
    private final HospitalRepository hosRepo;
    @Autowired
    public TreatmentService(PatientService patService, HospitalService hosService, TreatmentRepository trtRepo
            , PatientRepository patRepo, HospitalRepository hosRepo ) {
        this.patService = patService;
        this.hosService = hosService;
        this.trtRepo = trtRepo;
        this.patRepo=patRepo;
        this.hosRepo=hosRepo;
    }

    @Transactional
    public Treatment getTreatmentBYid(Long id) {
        boolean exists = trtRepo.existsById(id);
        if (!exists) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Treatment with id " + id + " does not exist")
            );
        } else {
            Optional<Treatment> optionalTreatment = trtRepo.findById(id);
            Treatment treatment;
            //exists condition assures that it is present, so we are safe from the warning
            treatment = optionalTreatment.get();
            return treatment;
        }
    }

    @Transactional
    public List<Treatment> getAllTreatments() {
        return trtRepo.findAll();
    }


    @Transactional
    public Treatment addTreatment(AddProto_Treatment trtInRequest) {
        Treatment treatment = fromPrototoTreatment(trtInRequest);
        trtRepo.save(treatment);
        return treatment;
    }

    @Transactional//in order to roll back if anything happened during the execution
    public List<Treatment> addTreatments(AddProto_Treatments trtsInRequest) {
        List<Treatment> treatments = new ArrayList<>();
        List<AddProto_Treatment> temp = trtsInRequest.getAddProtoTreatmentRequestsList();

        for (AddProto_Treatment element : temp) {
            treatments.add(addTreatment(element));
        }

        return treatments;
    }

    @Transactional
    public void deleteTreatment(Long id) {
        boolean exists = trtRepo.existsById(id);
        if (!exists) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Treatment with id " + id + " does not exist")
            );
        }
        trtRepo.deleteById(id);
    }

    @Transactional
    public void deleteTreatments(TreatmentIds treatmentIds) {
        List<TreatmentId> ids = treatmentIds.getIdsList();

        for (TreatmentId element : ids) {
            deleteTreatment(element.getId());
        }
    }

    @Transactional//in order to roll back if anything happened during the execution
    public Treatment updateTreatment(Proto_Treatment proto_treatment) {
        //check if the treatment exist
        boolean treatmentExists = trtRepo.existsById(proto_treatment.getId());
        if (!treatmentExists) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Treatment with id " + proto_treatment.getId() + " does not exist")
            );
        }

        boolean patientExists = patRepo.existsById(proto_treatment.getPatientId());
        //!=0 condition is used because in case that there is no patient id sent its default will be 0
        if (!patientExists && proto_treatment.getPatientId()!=0) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Patient with id " + proto_treatment.getPatientId() + " does not exist")
            );
        }

        boolean hospitalExists = hosRepo.existsById(proto_treatment.getHospitalId());
        if (!hospitalExists && proto_treatment.getHospitalId()!=0) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Hospital with id " + proto_treatment.getHospitalId() + " does not exist")
            );
        }

        //Here we are sure that the Treatment we want to update already exists and the patient and hospital exist
        Treatment treatment = trtRepo.findById(proto_treatment.getId()).get();

        //keep the patient id the same not change the age to 0 , because age default is 0, so in case the request
        //does not contain and patient id we want to interpret this as we do not need to change the patient id
        if (proto_treatment.getPatientId() != 0 && proto_treatment.getPatientId() != treatment.getTreatmentPatient().getId()) {
            Patient patient = patRepo.findById((proto_treatment.getPatientId())).get();
            treatment.setTreatmentPatient(patient);
        }

        if (proto_treatment.getHospitalId() != 0 && proto_treatment.getHospitalId() != treatment.getTreatmentHospital().getId()) {
            Hospital hospital = hosRepo.findById((proto_treatment.getHospitalId())).get();
            treatment.setTreatmentHospital(hospital);
        }

        //check if it is the same as the old, or it is "" most probably because it wasn't sent in the request
        if (!proto_treatment.getTreatmentDetails().isEmpty()
                && !Objects.equals(treatment.getTreatmentDetails(), proto_treatment.getTreatmentDetails())) {
            treatment.setTreatmentDetails(proto_treatment.getTreatmentDetails());
        }

        // check if the request sent include date or no/ we used empty to give the user the chance
        //to send white space if he wants to remove his treatment date
        if (!proto_treatment.getTreatmentDate().isEmpty()
                && !Objects.equals(treatment.getTreatmentDate(), proto_treatment.getTreatmentDate())) {
            treatment.setTreatmentDate(proto_treatment.getTreatmentDate());
        }

        return treatment;
    }

    @Transactional
    public List<Treatment> updateTreatments(Proto_Treatments proto_treatments) {
        List<Treatment> treatments = new ArrayList<>();
        List<Proto_Treatment> temp = proto_treatments.getTreatmentsInDbList();

        for (Proto_Treatment element : temp) {
            treatments.add(updateTreatment(element));
        }

        return treatments;
    }

    @Transactional
    public Proto_Patients listPatientsOfHospital(Long hospitalID) {
        List<Patient> hospitalPatients = new ArrayList<>();
        // check if the hospital exists
        boolean exists = hosRepo.existsById(hospitalID);
        if (!exists) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Hospital with id " + hospitalID + " does not exist")
            );
        }
        if (trtRepo.existsByTreatmentHospital(hosRepo.findById(hospitalID).get())) {
            List<Treatment> treatmentsForHospital= trtRepo.findAllByTreatmentHospital(hosRepo.findById(hospitalID).get());
            //get the col of patients from treatments and use set to get only unique values
            Set<Patient> patients=
                    treatmentsForHospital.stream()
                            .map(Treatment::getTreatmentPatient).collect(Collectors.toSet());
            hospitalPatients.addAll(patients);
        }
        return PatientService.createRepeatedPatients(hospitalPatients);
    }

    @Transactional
    public Proto_Hospitals listHospitalsOfPatient(Long patientID) {
        List<Hospital> patientHospitals = new ArrayList<>();
        // check if the hospital exists
        boolean exists = patRepo.existsById(patientID);
        if (!exists) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Patient with id " + patientID + " does not exist")
            );
        }
        //exists condition assures that it is present, so we are safe from the warning
        if (trtRepo.existsByTreatmentPatient(patRepo.findById(patientID).get())) {
            List<Treatment> treatmentsForPatient= trtRepo.findAllByTreatmentPatient(patRepo.findById(patientID).get());
            //get the col of hospitals from treatments and use set to get only unique values
            Set<Hospital> hospitals=
                    treatmentsForPatient.stream()
                            .map(Treatment::getTreatmentHospital).collect(Collectors.toSet());
            patientHospitals.addAll(hospitals);
        }
        return HospitalService.createRepeatedHospitals(patientHospitals);
    }

    public Treatment fromPrototoTreatment(AddProto_Treatment trtInRequest) {
        Long patient_Id = trtInRequest.getPatientId();//get the id of patient in the request
        Patient patient = patService.getPatientBYid(patient_Id);// get the patient of the treatment

        Long hospital_Id = trtInRequest.getHospitalId();//get the id of hospital in the request
        Hospital hospital = hosService.getHospitalBYid(hospital_Id);// get the hospital of the treatment

        Treatment treatment;
        treatment = new Treatment(
                patient,
                hospital,
                trtInRequest.getTreatmentDetails(),
                trtInRequest.getTreatmentDate()
        );
        return treatment;
    }


    public static Proto_Treatment fromTreatmentToProto(Treatment treatment) {
        Proto_Treatment trtInResponse;
        //because proto patient cannot set to be null
        if (treatment.getTreatmentDate() == null) {
            treatment.setTreatmentDate("null");
        }
        if (treatment.getTreatmentDetails() == null) {
            treatment.setTreatmentDetails("null");
        }
        trtInResponse = Proto_Treatment.newBuilder()
                .setId(treatment.getId())
                .setPatientId(treatment.getTreatmentPatient().getId())
                .setHospitalId(treatment.getTreatmentHospital().getId())
                .setTreatmentDetails(treatment.getTreatmentDetails())
                .setTreatmentDate(treatment.getTreatmentDate())
                .build();
        return trtInResponse;
    }


    public static Proto_Treatments createRepeatedTreatments(List<Treatment> L) {
        Proto_Treatments protoTreatmentsResponse;
        List<Proto_Treatment> temp = new ArrayList<>();
        for (Treatment element : L) {
            temp.add(fromTreatmentToProto(element));
        }
        Proto_Treatments.Builder builder = Proto_Treatments.newBuilder().addAllTreatmentsInDb(temp);
        protoTreatmentsResponse = builder.build();

        return protoTreatmentsResponse;
    }

}
