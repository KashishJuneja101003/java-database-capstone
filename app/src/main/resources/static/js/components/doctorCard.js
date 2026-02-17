// doctorCard.js

import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "./modals.js";

/**
 * Creates a dynamic doctor card element
 * @param {Object} doctor - Doctor data {name, specialization, email, availability}
 * @returns {HTMLElement} - The fully constructed doctor card
 */
export function createDoctorCard(doctor) {
    // Main card container
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    // Fetch current user role
    const role = localStorage.getItem("userRole");

    // Doctor info section
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialization = document.createElement("p");
    specialization.textContent = `Specialty: ${doctor.specialization}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    const availability = document.createElement("p");
    availability.textContent = `Availability: ${doctor.availability.join(", ")}`;

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // Button container
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // Role-based actions
    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.addEventListener("click", async () => {
            const confirmed = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
            if (!confirmed) return;

            const token = localStorage.getItem("token");
            try {
                const response = await fetch(`/api/doctors/${doctor.id}`, {
                    method: "DELETE",
                    headers: {
                        "Authorization": `Bearer ${token}`,
                    },
                });
                if (response.ok) {
                    alert("Doctor deleted successfully.");
                    card.remove();
                } else {
                    alert("Failed to delete doctor.");
                }
            } catch (err) {
                console.error(err);
                alert("An error occurred while deleting the doctor.");
            }
        });
        actionsDiv.appendChild(removeBtn);

    } else if (role === "patient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", () => {
            alert("Please log in first to book an appointment.");
        });
        actionsDiv.appendChild(bookNow);

    } else if (role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");
            try {
                const patientData = await getPatientData(token);
                showBookingOverlay(e, doctor, patientData);
            } catch (err) {
                console.error(err);
                alert("Failed to fetch patient data.");
            }
        });
        actionsDiv.appendChild(bookNow);
    }

    // Assemble card
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}
