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
public class PatientTest {
    @GrpcClient("inProcess")
    GrpcPatientServiceGrpc.GrpcPatientServiceBlockingStub service;

    // we added a couple of patients in the config file they are 7 records
    @Test
    @Order(1)
    void Test1() {
        //get all patients
        Proto_Patients a = service.getAllPatients(Empty.newBuilder().build());
        assertNotNull(a);
        assertFalse(a.getPatientsInDbList().isEmpty());
        assertEquals(7, a.getPatientsInDbList().size(), "patients in db equals 7");
    }

    @Test
    @Order(2)
    void Test2() {
        //get patient by id
        PatientId id = PatientId.newBuilder().setId(5).build();
        Proto_Patient patient = service.getPatient(id);
        assertNotNull(patient);
        assertEquals("Mia Finn", patient.getName(), "Name should be Mia");
        assertEquals(4, patient.getAge(), "Age should be 4");
        assertEquals("female", patient.getGender(), "Gender should be female");
        assertEquals("A24018194", patient.getSsn(), "SSN should be A24018194");
        assertEquals("31 KeplerStr, Ulm, Germany", patient.getAddress(), "Address should be Ulm");
        assertEquals("", patient.getPhonenum(), "");
    }

    @Test
    @Order(3)
    void Test3() {
        //no patient id with 10 so get error
        PatientId id = PatientId.newBuilder().setId(10).build();
        // We expect a StatusRuntimeException with NOT_FOUND or similar
        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.getPatient(id); // The operation that should cause an error
                }
        );
    }

    @Test
    @Order(4)
    void Test4() {
        // add patient
        AddProto_Patient request = AddProto_Patient.newBuilder().setName("Maher").setAge(25).setSsn("888").build();
        Proto_Patient patient = service.addPatient(request);
        assertNotNull(patient);

        // Assert that the fields contain the expected values
        assertEquals(8, patient.getId());
        assertEquals("Maher", patient.getName(), "Name should be Maher");
        assertEquals(25, patient.getAge(), "Age should be 30");
        assertEquals("", patient.getGender(), "Gender should be empty");
        assertEquals("888", patient.getSsn(), "SSN should be 888");
        assertEquals("", patient.getAddress(), "Address should be empty");
        assertEquals("", patient.getPhonenum(), "");
    }


    @Test
    @Order(5)
    void Test5() {
        //age cannot be negative so add error
        AddProto_Patient request = AddProto_Patient.newBuilder()
                .setName("Rostom")
                .setAge(-5)
                .setGender("Male")
                .setSsn("123-45-6789")
                .setAddress("123 Main St")
                .setPhonenum("123-456-7890")
                .build();

        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.addPatient(request);
                }
        );
    }

    @Test
    @Order(6)
    void Test6() {
        //add 2 patients
        AddProto_Patient request1 = AddProto_Patient.newBuilder()
                .setName("Ahmed Essam")
                .setAge(27)
                .setGender("Other")
                .setSsn("30001311400291")
                .setAddress("Ulm")
                .setPhonenum("1230")
                .build();
        AddProto_Patient request2 = AddProto_Patient.newBuilder()
                .setName("Mohamed Essam")
                .setAge(26)
                .setGender("Male")
                .setSsn("123-p")
                .setAddress("123 Tall St")
                .setAddress("")
                .build();
        AddProto_Patients request = AddProto_Patients.newBuilder().
                addAddProtoPatientRequests(request1)
                .addAddProtoPatientRequests(request2)
                .build();

        //check if the size increased by 2
        service.addPatients(request);
        Proto_Patients a = service.getAllPatients(Empty.newBuilder().build());
        assertEquals(10, a.getPatientsInDbList().size(), "patients in db equals 10");
    }

    @Test
    @Order(7)
    void Test7() {
        //can not have same ssn
        AddProto_Patient request1 = AddProto_Patient.newBuilder()
                .setName("Rostom")
                .setAge(10)
                .setGender("Male")
                .setSsn("123-45-6789")
                .setAddress("123 Main St")
                .setPhonenum("123-456-7890")
                .build();
        AddProto_Patient request2 = AddProto_Patient.newBuilder()
                .setName("Amr")
                .setAge(50)
                .setGender("Male")
                .setSsn("123-45-6789")
                .setAddress("123 Main St")
                .setPhonenum("123-456-7890")
                .build();
        AddProto_Patients request = AddProto_Patients.newBuilder().
                addAddProtoPatientRequests(request1)
                .addAddProtoPatientRequests(request2)
                .build();

        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.addPatients(request);
                }
        );

    }

    @Test
    @Order(8)
    void Test8() {
        //delete patient
        PatientId id = PatientId.newBuilder().setId(1).build();
        DeletePatientResponse r = service.deletePatient(id);
        assertEquals(r.getIsDelete(), "Deleted Successfully");
        Proto_Patients a = service.getAllPatients(Empty.newBuilder().build());
        assertEquals(9, a.getPatientsInDbList().size(), "patients in db equals 9");
    }

    @Test
    @Order(9)
    void Test9() {
        //delete patient error id not found
        PatientId id = PatientId.newBuilder().setId(1).build();
        //DeletePatientResponse r = service.deletePatient(id);
        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.deletePatient(id);
                }
        );
    }

    @Test
    @Order(10)
    void Test10() {
        //update patient
        Proto_Patient patient = Proto_Patient.newBuilder()
                .setId(2)
                .setName("Updated")
                .setAge(5)
                .setGender("other")
                .setSsn("9686")
                .setAddress("cairo")
                .setPhonenum("NA")
                .build();
        Proto_Patient returnedPatient = service.updatePatient(patient);
        assertEquals(2, returnedPatient.getId());
        assertEquals("Updated", returnedPatient.getName());
        assertEquals(5, returnedPatient.getAge());
        assertEquals("other", returnedPatient.getGender());
        assertEquals("9686", returnedPatient.getSsn());
        assertEquals("cairo", returnedPatient.getAddress());
        assertEquals("NA", returnedPatient.getPhonenum(), "");
    }

    @Test
    @Order(11)
    void Test11() {
        //update patient with id that does not exist
        Proto_Patient patient = Proto_Patient.newBuilder()
                .setId(20)
                .setName("Updated")
                .setAge(5)
                .setGender("other")
                .setSsn("777")
                .setAddress("cairo")
                .setPhonenum("NA")
                .build();

        assertThrows(
                StatusRuntimeException.class,
                () -> {
                    service.updatePatient(patient);
                }
        );
    }


}

