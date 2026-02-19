package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // 1️⃣ Find patient by email
    Patient findByEmail(String email);

    // 2️⃣ Find patient by email OR phone number
    Patient findByEmailOrPhone(String email, String phone);

}
