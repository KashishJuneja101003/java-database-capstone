// footer.js

/**
 * Renders the footer across all pages
 */
function renderFooter() {
    const footer = document.getElementById("footer");

    if (!footer) return; // Exit if footer container not found

    footer.innerHTML = `
        <footer class="footer">
            <!-- Branding Section -->
            <div class="footer-logo">
                <img src="../assets/images/logo.png" alt="Logo" />
                <p>© ${new Date().getFullYear()} HealthCare System. All rights reserved.</p>
            </div>

            <!-- Footer Links -->
            <div class="footer-links">
                <!-- Company Column -->
                <div class="footer-column">
                    <h4>Company</h4>
                    <a href="/about.html">About</a>
                    <a href="/careers.html">Careers</a>
                    <a href="/press.html">Press</a>
                </div>

                <!-- Support Column -->
                <div class="footer-column">
                    <h4>Support</h4>
                    <a href="/account.html">Account</a>
                    <a href="/help.html">Help Center</a>
                    <a href="/contact.html">Contact</a>
                </div>

                <!-- Legal Column -->
                <div class="footer-column">
                    <h4>Legal</h4>
                    <a href="/terms.html">Terms</a>
                    <a href="/privacy.html">Privacy Policy</a>
                    <a href="/licensing.html">Licensing</a>
                </div>
            </div>
        </footer>
    `;
}

// Automatically render the footer when the script loads
document.addEventListener("DOMContentLoaded", renderFooter);
