package com.project.back_end.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 1️⃣ Fetch available slots for a doctor on a specific date
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        List<String> allSlots = new ArrayList<>();
        LocalTime time = startTime;
        while (!time.isAfter(endTime.minusHours(1))) {
            allSlots.add(time.toString());
            time = time.plusHours(1);
        }

        // Fetch booked appointments
        List<Appointment> booked = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId, date.atStartOfDay(), date.atTime(23, 59));
        List<String> bookedSlots = booked.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());

        // Filter available
        allSlots.removeAll(bookedSlots);
        return allSlots;
    }

    // 2️⃣ Save a new doctor
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1; // doctor already exists
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // internal error
        }
    }

    // 3️⃣ Update an existing doctor
    public int updateDoctor(Doctor doctor) {
        if (!doctorRepository.existsById(doctor.getId())) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 4️⃣ Get all doctors
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // 5️⃣ Delete a doctor by ID
    public int deleteDoctor(long id) {
        if (!doctorRepository.existsById(id)) return -1;
        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 6️⃣ Validate doctor login
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.badRequest().body(response);
        }
        String token = tokenService.generateToken(doctor.getId(), "DOCTOR");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // 7️⃣ Find doctors by name
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctorRepository.findByNameLike(name));
        return result;
    }

    // 8️⃣ Filter doctors by name, specialty, and AM/PM availability
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> filtered = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        filtered = filterDoctorByTime(filtered, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    // 9️⃣ Filter doctors by name and time
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findByNameLike(name);
        filtered = filterDoctorByTime(filtered, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    // 10️⃣ Filter doctors by name and specialty
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specilty) {
        List<Doctor> filtered = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specilty);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    // 11️⃣ Filter doctors by specialty and time
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specilty, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        filtered = filterDoctorByTime(filtered, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    // 12️⃣ Filter doctors by specialty only
    public Map<String, Object> filterDoctorBySpecility(String specilty) {
        List<Doctor> filtered = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    // 13️⃣ Filter doctors by time only
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> filtered = filterDoctorByTime(doctorRepository.findAll(), amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    // 🔹 Private method to filter a list of doctors by time (AM/PM)
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.isEmpty()) return doctors;

        LocalTime start, end;
        if (amOrPm.equalsIgnoreCase("AM")) {
            start = LocalTime.of(0, 0);
            end = LocalTime.of(11, 59);
        } else {
            start = LocalTime.of(12, 0);
            end = LocalTime.of(23, 59);
        }

        List<Doctor> filtered = new ArrayList<>();
        for (Doctor doctor : doctors) {
            boolean hasAvailableSlot = doctor.getAvailability().stream()
                    .anyMatch(slot -> {
                        LocalTime slotTime = slot.toLocalTime();
                        return !slotTime.isBefore(start) && !slotTime.isAfter(end);
                    });
            if (hasAvailableSlot) filtered.add(doctor);
        }
        return filtered;
    }
}
