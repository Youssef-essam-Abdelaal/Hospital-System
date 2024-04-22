package com.hospitalSystem.TreatmentsFiles;

import com.google.protobuf.Empty;
import com.hospitalSystem.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class GrpcTreatmentService extends GrpcTreatmentServiceGrpc.GrpcTreatmentServiceImplBase {
    private final TreatmentService MyService;

    @Autowired
    public GrpcTreatmentService(TreatmentService MyService) {
        this.MyService = MyService;
    }

    @Override
    public void getAllTreatments(Empty request, StreamObserver<Proto_Treatments> responseObserver) {
        List<Treatment> treatmentsRetrieved = MyService.getAllTreatments();
        Proto_Treatments reply = TreatmentService.createRepeatedTreatments(treatmentsRetrieved);
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void getTreatments(TreatmentId request, StreamObserver<Proto_Treatment> responseObserver) {
        try {
            Treatment treatment = MyService.getTreatmentBYid(request.getId());
            Proto_Treatment reply = TreatmentService.fromTreatmentToProto(treatment);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void addTreatment(AddProto_Treatment request, StreamObserver<Proto_Treatment> responseObserver) {
        try {
            Treatment treatment = MyService.addTreatment(request);
            Proto_Treatment reply = TreatmentService.fromTreatmentToProto(treatment);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }


    @Override
    public void addTreatments(AddProto_Treatments request, StreamObserver<Proto_Treatments> responseObserver) {
        try {
            List<Treatment> treatments = MyService.addTreatments(request);
            Proto_Treatments reply = TreatmentService.createRepeatedTreatments(treatments);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateTreatment(Proto_Treatment request, StreamObserver<Proto_Treatment> responseObserver) {
        try {
            Treatment treatment = MyService.updateTreatment(request);
            Proto_Treatment reply = TreatmentService.fromTreatmentToProto(treatment);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateTreatments(Proto_Treatments request, StreamObserver<Proto_Treatments> responseObserver) {
        try {
            List<Treatment> treatments = MyService.updateTreatments(request);
            Proto_Treatments reply = TreatmentService.createRepeatedTreatments(treatments);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteTreatment(TreatmentId request, StreamObserver<DeleteTreatmentResponse> responseObserver) {
        try {
            MyService.deleteTreatment(request.getId());
            String msg = "Deleted Successfully";
            DeleteTreatmentResponse reply = DeleteTreatmentResponse.newBuilder().setIsDelete(msg).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }


    @Override
    public void deleteTreatments(TreatmentIds request, StreamObserver<DeleteTreatmentResponse> responseObserver) {
        try {
            MyService.deleteTreatments(request);
            String msg = "Deleted Successfully";
            DeleteTreatmentResponse reply = DeleteTreatmentResponse.newBuilder().setIsDelete(msg).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void listPatientsOfHospital(HospitalId request, StreamObserver<Proto_Patients> responseObserver) {
        try {
            Proto_Patients reply = MyService.listPatientsOfHospital(request.getId());
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void listHospitalsPatientRegisteredIn(PatientId request, StreamObserver<Proto_Hospitals> responseObserver) {
        try {
            Proto_Hospitals reply = MyService.listHospitalsOfPatient(request.getId());
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }
}
