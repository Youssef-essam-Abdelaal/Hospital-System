import com.google.protobuf.Empty;
import com.hospitalSystem.*;
import com.hospitalSystem.PatientsFiles.PatientService;
import com.hospitalSystem.TreatmentsFiles.Treatment;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        classes = GrpcServerApp.class,
        properties = {
                "grpc.server.inProcessName=test", // (1)
                "grpc.server.port=-1", // (2)
                "grpc.client.inProcess.address=in-process:test" // (3)
        })
@DirtiesContext
public class TreatmentTest {
    @GrpcClient("inProcess")
    GrpcTreatmentServiceGrpc.GrpcTreatmentServiceBlockingStub service;

    // we added a couple of treatments in the config file they are 10 records
    @Test
    @Order(1)
    void Test1() {
        //get all treatments
        Proto_Treatments a = service.getAllTreatments(Empty.newBuilder().build());
        assertNotNull(a);
        assertEquals(10, a.getTreatmentsInDbList().size(), "hospitals in db equals 10");
    }

    @Test
    @Order(2)
    void Test2() {
        //get Treatment by id
        TreatmentId id = TreatmentId.newBuilder().setId(7).build();
        Proto_Treatment treatment = service.getTreatments(id);
        assertNotNull(treatment);
        assertEquals(2L, treatment.getPatientId());
        assertEquals(3L, treatment.getHospitalId());
        assertEquals("Headache", treatment.getTreatmentDetails());
        assertEquals("", treatment.getTreatmentDate());

    }

    @Test
    @Order(3)
    void Test3() {
        //no treatment id with 15 so get error
        TreatmentId id = TreatmentId.newBuilder().setId(15).build();
        // We expect a StatusRuntimeException with NOT_FOUND or similar
        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.getTreatments(id); // The operation that should cause an error
                }
        );
    }

    @Test
    @Order(4)
    void Test4() {
        // add treatment
        AddProto_Treatment request = AddProto_Treatment.newBuilder()
                .setPatientId(7L)
                .setHospitalId(4L)
                .setTreatmentDetails("check up")
                .setTreatmentDate("2024")
                .build();
        Proto_Treatment treatment = service.addTreatment(request);
        assertNotNull(treatment);

        // Assert that the fields contain the expected values
        assertEquals(11, treatment.getId());
        assertEquals(7, treatment.getPatientId());
        assertEquals(4, treatment.getHospitalId());
        assertEquals("check up", treatment.getTreatmentDetails());
        assertEquals("2024", treatment.getTreatmentDate());
    }

    @Test
    @Order(5)
    void Test5() {
        // add treatment with error patient id is not found
        AddProto_Treatment request = AddProto_Treatment.newBuilder()
                .setPatientId(17L)
                .setHospitalId(4L)
                .setTreatmentDetails("check up")
                .setTreatmentDate("2024")
                .build();
        ;

        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.addTreatment(request);
                }
        );
    }

    @Test
    @Order(6)
    void Test6() {
        //delete treatment
        TreatmentId id = TreatmentId.newBuilder().setId(1).build();
        DeleteTreatmentResponse r = service.deleteTreatment(id);
        assertEquals(r.getIsDelete(), "Deleted Successfully");
        Proto_Treatments a = service.getAllTreatments((Empty.newBuilder().build()));
        assertEquals(10, a.getTreatmentsInDbList().size(), "treatments in db equals 10");
    }

    @Test
    @Order(7)
    void Test7() {
        //delete treatment error id not found
        TreatmentId id = TreatmentId.newBuilder().setId(21).build();
        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.deleteTreatment(id);
                }
        );
    }

    @Test
    @Order(8)
    void Test8() {
        //update Treatment
        Proto_Treatment treatment = Proto_Treatment.newBuilder()
                .setId(2)
                .setPatientId(4)
                .setHospitalId(4)
                .setTreatmentDetails("updated")
                .setTreatmentDate("2023")
                .build();
        Proto_Treatment returnedTreatment = service.updateTreatment(treatment);
        assertEquals(2, returnedTreatment.getId());
        assertEquals(4, returnedTreatment.getPatientId());
        assertEquals(4, returnedTreatment.getHospitalId());
        assertEquals("updated", returnedTreatment.getTreatmentDetails());
        assertEquals("2023", returnedTreatment.getTreatmentDate());
    }

    @Test
    @Order(10)
    void Test10() {
        //update Treatment with error as no hospital id
        Proto_Treatment treatment = Proto_Treatment.newBuilder()
                .setId(2)
                .setPatientId(4)
                .setHospitalId(16)
                .setTreatmentDetails("updated")
                .setTreatmentDate("2023")
                .build();

        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.updateTreatment(treatment);
                }
        );
    }


    @Test
    @Order(11)
    void Test11() {
        //get all patients of a hospital
        HospitalId id = HospitalId.newBuilder().setId(1).build();
        Proto_Patients patients = service.listPatientsOfHospital(id);
        List<Proto_Patient> L = patients.getPatientsInDbList();
        //creating another modifiable version of l
        List<Proto_Patient> temp = new ArrayList<>(L);
        //sorting based on the id
        temp.sort(Comparator.comparingLong(Proto_Patient::getId));
        System.out.println(temp);
        assertEquals(3, temp.size());
        assertEquals("Ahmed Rezk", temp.get(0).getName());//id 1
        assertEquals("Anas Adam", temp.get(1).getName());//id 2
        assertEquals("Emilia", temp.get(2).getName());//id 4
    }

    @Test
    @Order(12)
    void Test12() {
        //get all patients of a hospital with error because no hospital with id 9
        HospitalId id = HospitalId.newBuilder().setId(9).build();
        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.listPatientsOfHospital(id);
                }
        );
    }

    @Test
    @Order(13)
    void Test13() {
        // get all hospitals patient registered in
        PatientId id = PatientId.newBuilder().setId(1).build();
        Proto_Hospitals hospitals = service.listHospitalsPatientRegisteredIn(id);

        List<Proto_Hospital> L = hospitals.getHospitalsInDbList();
        //creating another modifiable version of l
        List<Proto_Hospital> temp = new ArrayList<>(L);
        //sorting based on the id
        temp.sort(Comparator.comparingLong(Proto_Hospital::getId));
        System.out.println(temp);
        assertEquals(3, temp.size());
        assertEquals("EL-Salam", temp.get(0).getName());//id 1
        assertEquals("Speedy Recovery", temp.get(1).getName());//id 4
        assertEquals("Paul Matheo Hospital", temp.get(2).getName());//id 5
    }

    @Test
    @Order(14)
    void Test14() {
        // get all hospitals patient registered in,  with error because id is not found
        PatientId id = PatientId.newBuilder().setId(18).build();
        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.listHospitalsPatientRegisteredIn(id);
                }
        );
    }


}
