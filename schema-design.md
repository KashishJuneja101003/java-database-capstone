# MySQL Database Design

## 1. patients
| Column Name        | Data Type        | Constraints                                   |
|-------------------|----------------|-----------------------------------------------|
| patient_id        | INT            | PRIMARY KEY, AUTO_INCREMENT                   |
| first_name        | VARCHAR(50)    | NOT NULL                                     |
| last_name         | VARCHAR(50)    | NOT NULL                                     |
| date_of_birth     | DATE           | NOT NULL                                     |
| email             | VARCHAR(100)   | UNIQUE, NOT NULL                             |
| phone_number      | VARCHAR(15)    | UNIQUE, NOT NULL                             |
| address           | VARCHAR(255)   |                                               |
| created_at        | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP                     |

**Notes:**  
- Deleting a patient may cascade to appointments.  
- Email and phone formats should be validated in code.

---

## 2. doctors
| Column Name        | Data Type       | Constraints                                   |
|-------------------|----------------|-----------------------------------------------|
| doctor_id         | INT            | PRIMARY KEY, AUTO_INCREMENT                   |
| first_name        | VARCHAR(50)    | NOT NULL                                     |
| last_name         | VARCHAR(50)    | NOT NULL                                     |
| specialty         | VARCHAR(100)   | NOT NULL                                     |
| email             | VARCHAR(100)   | UNIQUE, NOT NULL                             |
| phone_number      | VARCHAR(15)    | UNIQUE, NOT NULL                             |
| created_at        | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP                     |

**Notes:**  
- Avoid overlapping appointments via application logic.

---

## 3. appointments
| Column Name        | Data Type       | Constraints                                   |
|-------------------|----------------|-----------------------------------------------|
| appointment_id    | INT            | PRIMARY KEY, AUTO_INCREMENT                   |
| patient_id        | INT            | FOREIGN KEY REFERENCES patients(patient_id) ON DELETE CASCADE |
| doctor_id         | INT            | FOREIGN KEY REFERENCES doctors(doctor_id) ON DELETE CASCADE |
| appointment_date  | DATETIME       | NOT NULL                                     |
| duration_minutes  | INT            | NOT NULL                                     |
| status            | ENUM('Scheduled','Completed','Cancelled') | DEFAULT 'Scheduled' |

**Notes:**  
- Cascading deletion applies when patient or doctor is removed.  
- Prevent scheduling conflicts in application logic.

---

## 4. admin
| Column Name        | Data Type       | Constraints                                   |
|-------------------|----------------|-----------------------------------------------|
| admin_id          | INT            | PRIMARY KEY, AUTO_INCREMENT                   |
| username          | VARCHAR(50)    | UNIQUE, NOT NULL                             |
| password_hash     | VARCHAR(255)   | NOT NULL                                     |
| email             | VARCHAR(100)   | UNIQUE, NOT NULL                             |
| created_at        | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP                     |

**Notes:**  
- Passwords should always be stored hashed.

---

## 5. clinic_locations
| Column Name        | Data Type       | Constraints                                   |
|-------------------|----------------|-----------------------------------------------|
| location_id       | INT            | PRIMARY KEY, AUTO_INCREMENT                   |
| name              | VARCHAR(100)   | NOT NULL                                     |
| address           | VARCHAR(255)   | NOT NULL                                     |
| phone_number      | VARCHAR(15)    | UNIQUE                                       |

**Notes:**  
- Doctors and appointments may reference this table for location info.

---

## 6. payments
| Column Name        | Data Type       | Constraints                                   |
|-------------------|----------------|-----------------------------------------------|
| payment_id        | INT            | PRIMARY KEY, AUTO_INCREMENT                   |
| appointment_id    | INT            | FOREIGN KEY REFERENCES appointments(appointment_id) ON DELETE CASCADE |
| amount            | DECIMAL(10,2)  | NOT NULL                                     |
| payment_date      | DATETIME       | DEFAULT CURRENT_TIMESTAMP                     |
| payment_method    | ENUM('Cash','Card','Insurance') | NOT NULL |

**Notes:**  
- Deleting an appointment deletes the related payment.  



## MongoDB Collection Design

### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "patientId": 101,
  "patientName": "John Smith",
  "appointmentId": 51,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "every 6 hours",
      "durationDays": 5
    },
    {
      "name": "Amoxicillin",
      "dosage": "250mg",
      "frequency": "3 times a day",
      "durationDays": 7
    }
  ],
  "doctorNotes": "Patient has mild fever. Ensure hydration.",
  "tags": ["fever", "infection", "urgent"],
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street, San Francisco, CA",
    "contact": "+1-415-555-1234"
  },
  "attachments": [
    {
      "type": "pdf",
      "fileName": "lab_results.pdf",
      "url": "/files/lab_results_51.pdf"
    }
  ],
  "createdAt": "2026-02-17T10:30:00Z",
  "updatedAt": "2026-02-17T12:00:00Z"
}
