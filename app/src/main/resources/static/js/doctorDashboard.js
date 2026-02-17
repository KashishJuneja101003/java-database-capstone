// app/src/main/resources/static/js/doctorDashboard.js

import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

const tableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split('T')[0];
const token = localStorage.getItem('token');
let patientName = null;

// Search bar event
document.getElementById('searchBar').addEventListener('input', e => {
    patientName = e.target.value || null;
    loadAppointments();
});

// Today button
document.getElementById('todayButton').addEventListener('click', () => {
    selectedDate = new Date().toISOString().split('T')[0];
    document.getElementById('datePicker').value = selectedDate;
    loadAppointments();
});

// Date picker change
document.getElementById('datePicker').addEventListener('change', e => {
    selectedDate = e.target.value;
    loadAppointments();
});

// Load appointments
async function loadAppointments() {
    tableBody.innerHTML = "";

    try {
        const appointments = await getAllAppointments(selectedDate, patientName, token);

        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML = "<tr><td colspan='6'>No Appointments found for today.</td></tr>";
            return;
        }

        appointments.forEach(appt => {
            const row = createPatientRow(appt);
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error(error);
        tableBody.innerHTML = "<tr><td colspan='6'>Error loading appointments.</td></tr>";
    }
}

// Initial render
window.addEventListener('DOMContentLoaded', loadAppointments);
