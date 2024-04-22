package com.hospitalSystem.TreatmentsFiles;

import com.hospitalSystem.HospitalsFiles.Hospital;
import com.hospitalSystem.PatientsFiles.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment,Long> {
    Optional<Treatment> findById(Long id);
    boolean existsByTreatmentPatient(Patient patient);
    void deleteAllByTreatmentPatient(Patient patient);

    boolean existsByTreatmentHospital(Hospital hospital);
    void deleteAllByTreatmentHospital(Hospital hospital);

    List<Treatment> findAllByTreatmentHospital(Hospital hospital);
    List<Treatment> findAllByTreatmentPatient(Patient patient);
}
