package com.hospitalSystem.TreatmentsFiles;

import com.hospitalSystem.HospitalsFiles.Hospital;
import com.hospitalSystem.PatientsFiles.Patient;
import jakarta.persistence.*;

@Entity
@Table
public class Treatment {
    @Id
    @SequenceGenerator(
            name="trt_seq",
            sequenceName = "trt_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator="trt_seq"
    )
    private long id;
    @ManyToOne//default is eager loading, unidirectional relationship with patient
    @JoinColumn(nullable = false)
    private Patient treatmentPatient;//Reference to the associated Patient
    @ManyToOne//default is eager loading, unidirectional relationship with hospital
    @JoinColumn(nullable = false)
    private Hospital treatmentHospital;//Reference to the associated Hospital
    private String treatmentDetails;
    private String treatmentDate;

    public Treatment() {
    }

    public Treatment(Patient treatmentPatient, Hospital treatmentHospital, String treatmentDetails, String treatmentDate) {
        this.treatmentPatient = treatmentPatient;
        this.treatmentHospital = treatmentHospital;
        this.treatmentDetails = treatmentDetails;
        this.treatmentDate = treatmentDate;
    }

    public long getId() {
        return id;
    }

    public Patient getTreatmentPatient() {
        return treatmentPatient;
    }

    public void setTreatmentPatient(Patient treatmentPatient) {
        this.treatmentPatient = treatmentPatient;
    }

    public Hospital getTreatmentHospital() {
        return treatmentHospital;
    }

    public void setTreatmentHospital(Hospital treatmentHospital) {
        this.treatmentHospital = treatmentHospital;
    }

    public String getTreatmentDetails() {
        return treatmentDetails;
    }

    public void setTreatmentDetails(String treatmentDetails) {
        this.treatmentDetails = treatmentDetails;
    }

    public String getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(String treatmentDate) {
        this.treatmentDate = treatmentDate;
    }

    @Override
    public String toString() {
        return "Treatment{" +
                "id=" + id +
                ", treatmentPatient=" + treatmentPatient +
                ", treatmentHospital=" + treatmentHospital +
                ", treatmentDetails='" + treatmentDetails + '\'' +
                ", treatmentDate='" + treatmentDate + '\'' +
                '}';
    }
}




