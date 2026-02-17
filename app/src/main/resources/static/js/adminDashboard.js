// app/src/main/resources/static/js/adminDashboard.js

// Imports
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

// Event: Open Add Doctor Modal
document.getElementById('addDocBtn').addEventListener('click', () => openModal('addDoctor'));

// Load doctor cards on page load
window.addEventListener('DOMContentLoaded', loadDoctorCards);

async function loadDoctorCards() {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = ""; // Clear previous cards

    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        contentDiv.innerHTML = "<p>Error loading doctors. Please try again.</p>";
        console.error(error);
    }
}

// Filter/Search logic
document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar").value;
    const time = document.getElementById("filterTime").value;
    const specialty = document.getElementById("filterSpecialty").value;
    const contentDiv = document.getElementById("content");

    const filteredDoctors = await filterDoctors(name, time, specialty);
    renderDoctorCards(filteredDoctors.length ? filteredDoctors : []);
}

// Utility to render doctor cards
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    if (doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found.</p>";
        return;
    }

    doctors.forEach(doc => {
        const card = createDoctorCard(doc);
        contentDiv.appendChild(card);
    });
}

// Handle Add Doctor Form Submission
window.adminAddDoctor = async function() {
    const token = localStorage.getItem('token');
    if (!token) return alert('Admin not authenticated.');

    const doctor = {
        name: document.getElementById('docName').value,
        specialty: document.getElementById('docSpecialty').value,
        email: document.getElementById('docEmail').value,
        password: document.getElementById('docPassword').value,
        mobile: document.getElementById('docMobile').value,
        availability: Array.from(document.querySelectorAll('input[name="availability"]:checked')).map(cb => cb.value)
    };

    const result = await saveDoctor(doctor, token);
    if (result.success) {
        alert('Doctor added successfully!');
        loadDoctorCards();
    } else {
        alert('Error: ' + result.message);
    }
};
