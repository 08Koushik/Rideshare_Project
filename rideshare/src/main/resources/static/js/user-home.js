// =================== USER HOME ===================

// Check if user is logged in
const user = JSON.parse(localStorage.getItem("loggedInUser"));

if (!user) {
    // If no user info, redirect to login
    window.location.href = "user-login.html";
} else {
    // Display welcome message
    const welcomeEl = document.getElementById("welcomeMessage");
    if (welcomeEl) {
        welcomeEl.innerText = `Welcome, ${user.name}!`;
    }

    // Display user details
    const userDetailsEl = document.getElementById("userDetails");
    if (userDetailsEl) {
        let html = `<p><strong>Email:</strong> ${user.email}</p>`;
        html += `<p><strong>Role:</strong> ${user.roleType}</p>`;
        if (user.roleType === "DRIVER") {
            html += `<p><strong>Vehicle:</strong> ${user.vehicleDetails}</p>`;
        }
        userDetailsEl.innerHTML = html;
    }
}

// Logout functionality
const logoutBtn = document.getElementById("logoutBtn");
if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem("loggedInUser");
        window.location.href = "user-login.html";
    });
}
