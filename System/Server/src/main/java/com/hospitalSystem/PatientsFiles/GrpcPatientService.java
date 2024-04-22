package com.hospitalSystem.PatientsFiles;

import com.google.protobuf.Empty;
import com.hospitalSystem.*;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class GrpcPatientService extends GrpcPatientServiceGrpc.GrpcPatientServiceImplBase {
    private final PatientService MyService;

    @Autowired
    public GrpcPatientService(PatientService MyService) {
        this.MyService = MyService;
    }

    @Override
    public void getAllPatients(Empty request, StreamObserver<Proto_Patients> responseObserver) {
        List<Patient> patientsRetrieved = MyService.getAllPatients();
        Proto_Patients reply = PatientService.createRepeatedPatients(patientsRetrieved);
        responseObserver.onNext(reply);
        responseObserver.onCompleted();

    }

    @Override
    public void getPatient(PatientId request, StreamObserver<Proto_Patient> responseObserver) {
        try {
            Patient patient = MyService.getPatientBYid(request.getId());
            Proto_Patient reply = PatientService.fromPatientToProto(patient);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void addPatient(AddProto_Patient request, StreamObserver<Proto_Patient> responseObserver) {
        try {
            Patient patient = MyService.addPatient(request);
            Proto_Patient reply = PatientService.fromPatientToProto(patient);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }

    @Override
    public void addPatients(AddProto_Patients request, StreamObserver<Proto_Patients> responseObserver) {
        try {
            List<Patient> patients = MyService.addPatients(request);
            Proto_Patients reply = PatientService.createRepeatedPatients(patients);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }

    @Override
    public void updatePatient(Proto_Patient request, StreamObserver<Proto_Patient> responseObserver) {
        try {
            Patient patient = MyService.updatePatient(request);
            Proto_Patient reply = PatientService.fromPatientToProto(patient);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }

    @Override
    public void updatePatients(Proto_Patients request, StreamObserver<Proto_Patients> responseObserver) {
        try {
            List<Patient> patients = MyService.updatePatients(request);
            Proto_Patients reply = PatientService.createRepeatedPatients(patients);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }

    @Override
    public void deletePatient(PatientId request, StreamObserver<DeletePatientResponse> responseObserver) {
        try {
            MyService.deletePatient(request.getId());
            String msg = "Deleted Successfully";
            DeletePatientResponse reply = DeletePatientResponse.newBuilder().setIsDelete(msg).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void deletePatients(PatientIds request, StreamObserver<DeletePatientResponse> responseObserver) {
        try {
            MyService.deletePatients(request);
            String msg = "Deleted Successfully";
            DeletePatientResponse reply = DeletePatientResponse.newBuilder().setIsDelete(msg).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }


}








