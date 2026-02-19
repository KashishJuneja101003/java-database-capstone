package com.project.back_end.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.DTO.Login;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class ServiceClass {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public ServiceClass(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 1️⃣ Validate token
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        response.put("message", "Token is valid");
        return ResponseEntity.ok(response);
    }

    // 2️⃣ Validate admin login
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
        if (admin == null || !admin.getPassword().equals(receivedAdmin.getPassword())) {
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String token = tokenService.generateToken(admin.getId(), "ADMIN");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // 3️⃣ Filter doctors by name, specialty, and time
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        if ((name == null || name.isEmpty()) &&
            (specialty == null || specialty.isEmpty()) &&
            (time == null || time.isEmpty())) {
            return doctorService.getDoctors().stream()
                    .collect(HashMap::new, (m, d) -> m.put("doctors", doctorService.getDoctors()), HashMap::putAll);
        }
        return doctorService.filterDoctorsByNameSpecilityandTime(
                name != null ? name : "",
                specialty != null ? specialty : "",
                time != null ? time : "");
    }

    // 4️⃣ Validate appointment availability
    public int validateAppointment(Appointment appointment) {
        if (!doctorRepository.existsById(appointment.getDoctorId())) return -1;
        var availableSlots = doctorService.getDoctorAvailability(appointment.getDoctorId(), appointment.getAppointmentTime().toLocalDate());
        if (availableSlots.contains(appointment.getAppointmentTime().toLocalTime().toString())) return 1;
        return 0;
    }

    // 5️⃣ Validate patient existence
    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    // 6️⃣ Validate patient login
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(login.getIdentifier());
        if (patient == null || !patient.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String token = tokenService.generateToken(patient.getId(), "PATIENT");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // 7️⃣ Filter patient appointments
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.getEmailFromToken(token);
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            response.put("message", "Patient not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (condition != null && !condition.isEmpty() && (name == null || name.isEmpty())) {
            return patientService.filterByCondition(condition, patient.getId());
        } else if (name != null && !name.isEmpty() && (condition == null || condition.isEmpty())) {
            return patientService.filterByDoctor(name, patient.getId());
        } else if (condition != null && !condition.isEmpty() && name != null && !name.isEmpty()) {
            return patientService.filterByDoctorAndCondition(condition, name, patient.getId());
        } else {
            // Return all appointments if no filters provided
            return patientService.getPatientAppointment(patient.getId(), token);
        }
    }
}
