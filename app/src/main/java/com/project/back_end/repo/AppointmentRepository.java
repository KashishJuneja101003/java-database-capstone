package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 1️⃣ Retrieve appointments for a doctor within a given time range
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH d.availability " +
           "WHERE d.id = :doctorId " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId,
                                                              LocalDateTime start,
                                                              LocalDateTime end);

    // 2️⃣ Filter appointments by doctor ID, partial patient name, and time range
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH a.patient p " +
           "WHERE d.id = :doctorId " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            Long doctorId,
            String patientName,
            LocalDateTime start,
            LocalDateTime end);

    // 3️⃣ Delete all appointments for a specific doctor
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    // 4️⃣ Find all appointments for a specific patient
    List<Appointment> findByPatientId(Long patientId);

    // 5️⃣ Retrieve appointments for a patient by status, ordered by time
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId,
                                                                          int status);

    // 6️⃣ Search appointments by partial doctor name and patient ID
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "WHERE a.patient.id = :patientId " +
           "AND LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))")
    List<Appointment> filterByDoctorNameAndPatientId(String doctorName,
                                                     Long patientId);

    // 7️⃣ Filter appointments by doctor name, patient ID, and status
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "WHERE a.patient.id = :patientId " +
           "AND a.status = :status " +
           "AND LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName,
                                                              Long patientId,
                                                              int status);
}
