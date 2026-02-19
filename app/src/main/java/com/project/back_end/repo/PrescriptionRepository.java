package com.project.back_end.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.project.back_end.models.Prescription;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    // Find prescriptions by appointment ID
    List<Prescription> findByAppointmentId(Long appointmentId);

}
