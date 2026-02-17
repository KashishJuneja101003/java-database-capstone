// header.js

/**
 * Renders the dynamic header based on user role and login state
 */
function renderHeader() {
    // Remove role and token if on homepage
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Check for invalid session
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    const headerDiv = document.getElementById("header");
    let headerContent = "";

    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="adminBtn">Add Doctor</button>
            <a href="#" id="logoutBtn">Logout</a>
        `;
    } else if (role === "doctor") {
        headerContent += `
            <a href="/doctorDashboard.html">Home</a>
            <a href="#" id="logoutBtn">Logout</a>
        `;
    } else if (role === "patient") {
        headerContent += `
            <a href="/login.html">Login</a>
            <a href="/signup.html">Sign Up</a>
        `;
    } else if (role === "loggedPatient") {
        headerContent += `
            <a href="/patientDashboard.html">Home</a>
            <a href="/appointments.html">Appointments</a>
            <a href="#" id="logoutBtn">Logout</a>
        `;
    }

    // Inject header content into the page
    headerDiv.innerHTML = headerContent;

    // Attach event listeners
    attachHeaderButtonListeners();
}

/**
 * Attaches click listeners for dynamic buttons
 */
function attachHeaderButtonListeners() {
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => openModal("addDoctor"));
    }

    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", logout);
    }
}

/**
 * Clears session for admin, doctor, or loggedPatient and redirects to homepage
 */
function logout() {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    window.location.href = "/";
}

/**
 * Clears token but retains "patient" role for patient users
 */
function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/patientDashboard.html";
}

// Initialize the header on page load
document.addEventListener("DOMContentLoaded", renderHeader);
