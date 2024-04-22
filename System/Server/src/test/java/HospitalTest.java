import com.google.protobuf.Empty;
import com.hospitalSystem.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        classes = GrpcServerApp.class,
        properties = {
                "grpc.server.inProcessName=test", // (1)
                "grpc.server.port=-1", // (2)
                "grpc.client.inProcess.address=in-process:test" // (3)
        })
@DirtiesContext
public class HospitalTest {
    @GrpcClient("inProcess")
    GrpcHospitalServiceGrpc.GrpcHospitalServiceBlockingStub service;

    // we added a couple of hospitals in the config file they are 5 records
    @Test
    @Order(1)
    void Test1() {
        //get all Hospitals
        Proto_Hospitals a = service.getAllHospitals(Empty.newBuilder().build());
        assertNotNull(a);
        assertEquals(5, a.getHospitalsInDbList().size(), "hospitals in db equals 7");
    }

    @Test
    @Order(2)
    void Test2() {
        //get Hospital by id
        HospitalId id = HospitalId.newBuilder().setId(5).build();
        Proto_Hospital hospital = service.getHospital(id);
        assertNotNull(hospital);

        assertEquals("Paul Matheo Hospital", hospital.getName(), "Name should be Paul Matheo Hospital");
        assertEquals("Berlin, Germany", hospital.getAddress(), "Address should be Berlin, Germany");
    }

    @Test
    @Order(3)
    void Test3() {
        //no hospital id with 10 so get error
        HospitalId id = HospitalId.newBuilder().setId(10).build();
        // We expect a StatusRuntimeException with NOT_FOUND or similar
        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.getHospital(id); // The operation that should cause an error
                }
        );
    }

    @Test
    @Order(4)
    void Test4() {
        // add hospital
        AddProto_Hospital request = AddProto_Hospital.newBuilder().setName("hospitalll")
                .setAddress("alexandria").setSpecialization("all").build();
        Proto_Hospital hospital = service.addHospital(request);
        assertNotNull(hospital);

        // Assert that the fields contain the expected values
        assertEquals(6, hospital.getId());
        assertEquals("hospitalll", hospital.getName());
        assertEquals("alexandria", hospital.getAddress());
        assertEquals("all", hospital.getSpecialization());
    }

    @Test
    @Order(5)
    void Test5() {
        // add hospital with error because name is blank
        AddProto_Hospital request = AddProto_Hospital.newBuilder().setName("")
                .setAddress("Roma").setSpecialization("general").build();

        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.addHospital(request);
                }
        );
    }

    @Test
    @Order(6)
    void Test6() {
        //add 2 hospitals
        AddProto_Hospital request1 = AddProto_Hospital.newBuilder().setName("Care")
                .setAddress("Usa").setSpecialization("teeth").build();
        AddProto_Hospital request2 = AddProto_Hospital.newBuilder().setName("Jesus Care")
                .setAddress("BonnerPlatz").setSpecialization("Heart").build();

        AddProto_Hospitals request = AddProto_Hospitals.newBuilder().
                addAddProtoHospitalRequests(request1)
                .addAddProtoHospitalRequests(request2)
                .build();

        //check if the size increased by 2
        service.addHospitals(request);
        Proto_Hospitals a = service.getAllHospitals(Empty.newBuilder().build());
        assertEquals(8, a.getHospitalsInDbList().size(), "hospitals in db equals 8");
    }

    @Test
    @Order(7)
    void Test7() {
        //delete hospital
        HospitalId id = HospitalId.newBuilder().setId(1).build();
        DeleteHospitalResponse r = service.deleteHospital(id);
        assertEquals(r.getIsDelete(), "Deleted Successfully");
        Proto_Hospitals a = service.getAllHospitals((Empty.newBuilder().build()));
        assertEquals(7, a.getHospitalsInDbList().size(), "hospitals in db equals 7");
    }

    @Test
    @Order(8)
    void Test8() {
        //delete hospital error id not found
        HospitalId id = HospitalId.newBuilder().setId(1).build();
        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.deleteHospital(id);
                }
        );
    }

    @Test
    @Order(9)
    void Test9() {
        //update hospital
        Proto_Hospital hospital = Proto_Hospital.newBuilder()
                .setId(2)
                .setName("Updated")
                .setAddress("cairo")
                .setSpecialization("NA")
                .build();
        Proto_Hospital returnedHospital = service.updateHospital(hospital);
        assertEquals(2, returnedHospital.getId());
        assertEquals("Updated", returnedHospital.getName());
        assertEquals("cairo", returnedHospital.getAddress());
        assertEquals("NA", returnedHospital.getSpecialization());
    }

    @Test
    @Order(10)
    void Test10() {
        //update hospital with id that does not exist
        Proto_Hospital hospital = Proto_Hospital.newBuilder()
                .setId(20)
                .setName("Updated")
                .setAddress("cairo")
                .build();

        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.updateHospital(hospital);
                }
        );
    }


}
