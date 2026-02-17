// app/src/main/resources/static/js/services/index.js

// Import required modules
import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

// Define API endpoints
const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

// Wait for page to load
window.onload = function () {
    // Select buttons
    const adminBtn = document.getElementById('adminLogin');
    const doctorBtn = document.getElementById('doctorLogin');

    // Admin button click opens admin login modal
    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }

    // Doctor button click opens doctor login modal
    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
        });
    }
};

// Admin login handler
window.adminLoginHandler = async function () {
    try {
        // Read input values
        const username = document.getElementById('adminUsername').value;
        const password = document.getElementById('adminPassword').value;

        const admin = { username, password };

        // Send POST request
        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            selectRole('admin'); // Save selected role
        } else {
            alert('Invalid credentials!');
        }
    } catch (error) {
        console.error(error);
        alert('An error occurred during login.');
    }
};

// Doctor login handler
window.doctorLoginHandler = async function () {
    try {
        // Read input values
        const email = document.getElementById('doctorEmail').value;
        const password = document.getElementById('doctorPassword').value;

        const doctor = { email, password };

        // Send POST request
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            selectRole('doctor'); // Save selected role
        } else {
            alert('Invalid credentials!');
        }
    } catch (error) {
        console.error(error);
        alert('An error occurred during login.');
    }
};
