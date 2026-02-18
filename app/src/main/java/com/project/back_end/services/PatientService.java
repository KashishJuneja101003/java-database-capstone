package com.project.back_end.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 1️⃣ Create a new patient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1; // success
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // failure
        }
    }

    // 2️⃣ Get all appointments for a patient
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        String emailFromToken = tokenService.getEmailFromToken(token);

        Optional<Patient> patientOpt = patientRepository.findByEmail(emailFromToken) != null ?
                Optional.of(patientRepository.findByEmail(emailFromToken)) :
                Optional.empty();

        if (!patientOpt.isPresent() || !patientOpt.get().getId().equals(id)) {
            response.put("message", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<AppointmentDTO> appointments = appointmentRepository.findByPatientId(id)
                .stream()
                .map(AppointmentDTO::new) // assuming AppointmentDTO has a constructor accepting Appointment
                .collect(Collectors.toList());

        response.put("appointments", appointments);
        return ResponseEntity.ok(response);
    }

    // 3️⃣ Filter appointments by condition (past/future)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        List<AppointmentDTO> filtered = appointmentRepository.findByPatientId(id)
                .stream()
                .filter(a -> {
                    if ("past".equalsIgnoreCase(condition)) return a.getAppointmentTime().isBefore(now);
                    if ("future".equalsIgnoreCase(condition)) return a.getAppointmentTime().isAfter(now);
                    return false;
                })
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        response.put("appointments", filtered);
        return ResponseEntity.ok(response);
    }

    // 4️⃣ Filter appointments by doctor's name
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        List<AppointmentDTO> filtered = appointmentRepository
                .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, 0) // assuming status=0 for active
                .stream()
                .filter(a -> a.getDoctorName().toLowerCase().contains(name.toLowerCase()))
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        response.put("appointments", filtered);
        return ResponseEntity.ok(response);
    }

    // 5️⃣ Filter appointments by doctor's name and condition (past/future)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        List<AppointmentDTO> filtered = appointmentRepository.findByPatientId(patientId)
                .stream()
                .filter(a -> a.getDoctorName().toLowerCase().contains(name.toLowerCase()))
                .filter(a -> {
                    if ("past".equalsIgnoreCase(condition)) return a.getAppointmentTime().isBefore(now);
                    if ("future".equalsIgnoreCase(condition)) return a.getAppointmentTime().isAfter(now);
                    return false;
                })
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        response.put("appointments", filtered);
        return ResponseEntity.ok(response);
    }

    // 6️⃣ Get patient details by token
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        String emailFromToken = tokenService.getEmailFromToken(token);

        Patient patient = patientRepository.findByEmail(emailFromToken);
        if (patient == null) {
            response.put("message", "Patient not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("patient", patient);
        return ResponseEntity.ok(response);
    }
}
