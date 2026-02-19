package com.project.back_end.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.services.TokenService;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    // 1️⃣ Book a new appointment
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1; // success
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // error
        }
    }

    // 2️⃣ Update an existing appointment
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
        if (!existingOpt.isPresent()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existing = existingOpt.get();

        // Validate if the update is allowed (custom validation)
        if (!validateAppointment(appointment)) {
            response.put("message", "Invalid appointment data.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            existing.setAppointmentTime(appointment.getAppointmentTime());
            existing.setStatus(appointment.getStatus());
            existing.setPatient(appointment.getPatient());
            existing.setDoctor(appointment.getDoctor());
            appointmentRepository.save(existing);

            response.put("message", "Appointment updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Failed to update appointment.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 3️⃣ Cancel an existing appointment
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (!appointmentOpt.isPresent()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = appointmentOpt.get();
        Long patientIdFromToken = tokenService.getUserIdFromToken(token);

        if (!appointment.getPatient().getId().equals(patientIdFromToken)) {
            response.put("message", "Unauthorized to cancel this appointment.");
            return ResponseEntity.status(403).body(response);
        }

        try {
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment canceled successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Failed to cancel appointment.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 4️⃣ Retrieve appointments for a doctor on a specific date, optionally filtered by patient name
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();

        Long doctorId = tokenService.getUserIdFromToken(token);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;

        if (pname == null || pname.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId, pname, start, end);
        }

        result.put("appointments", appointments);
        return result;
    }

    // ✅ Helper method to validate appointment before updating
    private boolean validateAppointment(Appointment appointment) {
        // Example: check doctor and patient exist
        if (appointment.getDoctor() == null || appointment.getPatient() == null) return false;
        if (!doctorRepository.existsById(appointment.getDoctor().getId())) return false;
        if (!patientRepository.existsById(appointment.getPatient().getId())) return false;

        // Example: check appointment time is in the future
        return appointment.getAppointmentTime() != null &&
               appointment.getAppointmentTime().isAfter(LocalDateTime.now());
    }
}
