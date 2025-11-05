// src/main/resources/static/js/user.js

// BASE URL for backend API
const BASE_URL = "http://localhost:8080/api/auth";

// =================== UNIFIED LOGIN ===================
const userLoginForm = document.getElementById("userLoginForm");
if (userLoginForm) {
    userLoginForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        // --- CLEAN RETRIEVAL ---
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const role = document.getElementById("role").value; // NEW: Get selected role

        // Check for empty values immediately
        if (!email || !password || !role) {
            Swal.fire({
                            icon: 'warning',
                            title: 'Required Fields',
                            text: "Please enter both email and password, and select a login role."
                        });
            return;
        }

        let fetchUrl;

        if (role === 'ADMIN') {
            // Admin Login Endpoint (Admin controller expects query params)
            fetchUrl = `${BASE_URL}/admin/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;
        } else {
            // User Login Endpoint (User controller expects query params)
            fetchUrl = `${BASE_URL}/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;
        }

        console.log("Attempting to fetch URL:", fetchUrl);

        try {
            const response = await fetch(fetchUrl, { method: "POST" });
            const resultText = await response.text();

            if (role === 'ADMIN') {
                // Admin login returns a plain success/fail string
                if (resultText.includes("successfully")) {
                    window.location.href = "admin-dashboard.html";
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Login Failed',
                        text: "Invalid Admin credentials."
                    });
                }
            } else {
                // User login returns "FIRST_LOGIN" or a JSON User object
                if (resultText.includes("FIRST_LOGIN")) {
                    localStorage.setItem("userEmail", email);
                    window.location.href = "reset-password.html";
                } else if (resultText.trim().startsWith("{")) {
                    const user = JSON.parse(resultText);

                    // *** CRITICAL FIX: SAVE THE FULL USER OBJECT ***
                    localStorage.setItem("loggedInUser", JSON.stringify(user));

                    // --- NEW ROLE-BASED REDIRECTION LOGIC ---
                    if (user.roleType === 'DRIVER') {
                         window.location.href = "driver-dashboard.html";
                    } else if (user.roleType === 'PASSENGER') {
                         window.location.href = "passenger-dashboard.html";
                    } else {
                         window.location.href = "user-home.html"; // Fallback for other roles
                    }
                    // ------------------------------------------
                } else {
                    // If it's another error message (e.g., "User not found!" or "Incorrect password!")
                    Swal.fire({
                        icon: 'error',
                        title: 'Login Failed',
                        text: "Invalid credentials. Please try again. Backend response: " + resultText
                    });
                }
            }
        } catch (err) {
            console.error("Fetch error:", err);
            Swal.fire({
                icon: 'error',
                title: 'Connection Error',
                text: "Error connecting to server. Check console for details."
            });
        }
    });
}