package com.hospitalSystem.Client;

import com.hospitalSystem.GrpcPatientServiceGrpc;
import com.hospitalSystem.Proto_Patients;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class GrpcPatientClientService {
    @GrpcClient("Server")
    private GrpcPatientServiceGrpc.GrpcPatientServiceBlockingStub blockingStub;

    public GrpcPatientClientService() {
    }

    public Proto_Patients getAllPatients() {
//        try {
//            final HelloReply response = this.simpleStub.sayHello(HelloRequest.newBuilder().setName(name).build());
//            return response.getMessage();
//        } catch (final StatusRuntimeException e) {
//            return "FAILED with " + e.getStatus().getCode().name();
//        }
        return blockingStub.getAllPatients(com.google.protobuf.Empty.getDefaultInstance());
    }
}
