// ================================
// ✅ BASE URL
// ================================
const BASE_URL = "http://localhost:8080/api/auth";


// ================================
// ✅ LOGIN HANDLER
// ================================
const userLoginForm = document.getElementById("userLoginForm");

if (userLoginForm) {

    userLoginForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();
        const role = document.getElementById("role").value;

        if (!email || !password || !role) {
            Swal.fire({
                icon: "warning",
                title: "Missing Fields",
                text: "Please enter email, password, and select login type."
            });
            return;
        }

        let url;

        // ✅ Admin login (string response)
        if (role === "ADMIN") {
            url = `${BASE_URL}/admin/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;
        }
        // ✅ Driver OR Passenger login
        else {
            url = `${BASE_URL}/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;
        }

        try {
            const response = await fetch(url, { method: "POST" });
            const text = await response.text();

            // ✅ ADMIN LOGIN
            if (role === "ADMIN") {
                if (text.includes("successfully")) {
                    window.location.href = "admin-dashboard.html";
                } else {
                    Swal.fire({
                        icon: "error",
                        title: "Login Failed",
                        text: "Invalid Admin Credentials."
                    });
                }
                return;
            }

            // ✅ USER LOGIN (Driver / Passenger)
            if (text.includes("FIRST_LOGIN")) {
                localStorage.setItem("userEmail", email);
                window.location.href = "reset-password.html";
                return;
            }

            // ✅ Successful User JSON
            if (text.trim().startsWith("{")) {
                const user = JSON.parse(text);

                // ✅ Save user
                localStorage.setItem("loggedInUser", JSON.stringify(user));

                console.log("✅ Logged In:", user);

                // ✅ CORRECT ROLE-BASED REDIRECT
                if (user.roleType === "DRIVER") {
                    window.location.href = "driver-dashboard.html";
                }
                else if (user.roleType === "PASSENGER") {
                    window.location.href = "passenger-dashboard.html";
                }
                else if (user.roleType === "ADMIN") {
                    window.location.href = "admin-dashboard.html";
                }
                else {
                    window.location.href = "user-home.html";
                }

                return;
            }

            // ❌ Unknown error
            Swal.fire({
                icon: "error",
                title: "Login Failed",
                text: text
            });

        } catch (err) {
            console.error(err);
            Swal.fire({
                icon: "error",
                title: "Server Error",
                text: "Unable to connect to server"
            });
        }

    });
}
