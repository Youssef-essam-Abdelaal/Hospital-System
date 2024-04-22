package com.hospitalSystem.HospitalsFiles;

import com.google.protobuf.Empty;
import com.hospitalSystem.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class GrpcHospitalService extends GrpcHospitalServiceGrpc.GrpcHospitalServiceImplBase {
    private final HospitalService MyService;

    @Autowired
    public GrpcHospitalService(HospitalService MyService) {
        this.MyService = MyService;
    }

    @Override
    public void getAllHospitals(Empty request, StreamObserver<Proto_Hospitals> responseObserver) {
        List<Hospital> hospitalsRetrieved = MyService.getAllHospitals();
        Proto_Hospitals reply = HospitalService.createRepeatedHospitals(hospitalsRetrieved);
        responseObserver.onNext(reply);
        responseObserver.onCompleted();

    }

    @Override
    public void getHospital(HospitalId request, StreamObserver<Proto_Hospital> responseObserver) {
        try {
            Hospital hospital = MyService.getHospitalBYid(request.getId());
            Proto_Hospital reply = HospitalService.fromHospitalToProto(hospital);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void addHospital(AddProto_Hospital request, StreamObserver<Proto_Hospital> responseObserver) {
        try {
            Hospital hospital = MyService.addHospital(request);
            Proto_Hospital reply = HospitalService.fromHospitalToProto(hospital);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }

    @Override
    public void addHospitals(AddProto_Hospitals request, StreamObserver<Proto_Hospitals> responseObserver) {
        try {
            List<Hospital> hospitals = MyService.addHospitals(request);
            Proto_Hospitals reply = HospitalService.createRepeatedHospitals(hospitals);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateHospital(Proto_Hospital request, StreamObserver<Proto_Hospital> responseObserver) {
        try {
            Hospital hospital = MyService.updateHospital(request);
            Proto_Hospital reply = HospitalService.fromHospitalToProto(hospital);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateHospitals(Proto_Hospitals request, StreamObserver<Proto_Hospitals> responseObserver) {
        try {
            List<Hospital> hospitals = MyService.updateHospitals(request);
            Proto_Hospitals reply = HospitalService.createRepeatedHospitals(hospitals);
            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("An error occurred: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteHospital(HospitalId request, StreamObserver<DeleteHospitalResponse> responseObserver) {
        try {
            MyService.deleteHospital(request.getId());
            String msg = "Deleted Successfully";
            DeleteHospitalResponse reply = DeleteHospitalResponse.newBuilder().setIsDelete(msg).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteHospitals(HospitalIds request, StreamObserver<DeleteHospitalResponse> responseObserver) {
        try {
            MyService.deleteHospitals(request);
            String msg = "Deleted Successfully";
            DeleteHospitalResponse reply = DeleteHospitalResponse.newBuilder().setIsDelete(msg).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

}
