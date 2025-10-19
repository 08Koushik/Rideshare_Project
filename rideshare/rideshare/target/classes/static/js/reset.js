// BASE URL for backend API
const BASE_URL = "http://localhost:8080/api/auth";

// =================== RESET PASSWORD ===================
const resetForm = document.getElementById("resetForm");
if (resetForm) {
    resetForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        if (newPassword !== confirmPassword) {
            alert("Passwords do not match!");
            return;
        }

        // Get email from localStorage (stored during first login)
        const email = localStorage.getItem("resetEmail");
        if (!email) {
            alert("No user found for password reset!");
            return;
        }

        try {
            const response = await fetch(`${BASE_URL}/reset-password?email=${email}&newPassword=${newPassword}`, {
                method: "POST"
            });

            const result = await response.text();

            if (result.includes("successfully")) {
                alert("Password reset successfully! Please login again.");
                localStorage.removeItem("resetEmail");
                window.location.href = "user-login.html";
            } else {
                alert(result);
            }
        } catch (err) {
            console.error(err);
            alert("Error resetting password!");
        }
    });
}
