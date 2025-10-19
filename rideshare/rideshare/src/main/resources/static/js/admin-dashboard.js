// In src/main/resources/static/js/admin-dashboard.js

// Navigate to onboard page when "Onboard New User" button is clicked
const onboardBtn = document.getElementById("onboardBtn");
if (onboardBtn) {
  onboardBtn.addEventListener("click", () => {
    window.location.href = "onboard-user.html";
  });
}

// =================== LOGIC TO FETCH AND DISPLAY USERS ===================

async function loadUsers() {
    // Check if BASE_URL exists just in case (optional, but good practice)
    if (typeof BASE_URL === 'undefined') {
        console.error("Critical: BASE_URL is still undefined.");
        alert("Error loading dependencies. Please clear cache and refresh.");
        return;
    }

    const usersTableBody = document.querySelector("#usersTable tbody");
    if (!usersTableBody) return;

    try {
        // Use the reusable getData function from common.js
        const users = await getData("/admin/users");

        usersTableBody.innerHTML = '';

        if (Array.isArray(users)) {
            users.forEach(user => {
                const row = usersTableBody.insertRow();

                // Populate table cells
                row.insertCell().textContent = user.id;
                row.insertCell().textContent = user.name;
                row.insertCell().textContent = user.email;
                row.insertCell().textContent = user.contactNumber;
                row.insertCell().textContent = user.roleType;

                row.insertCell().textContent = user.roleType === 'DRIVER' ? (user.vehicleDetails || 'N/A') : 'N/A';
            });
        }

    } catch (error) {
        console.error("Failed to load users:", error);
        const row = usersTableBody.insertRow();
        row.insertCell(0).textContent = "Error loading user data.";
        row.cells[0].colSpan = 6;
    }
}

// *** CRITICAL FIX: Ensure loadUsers runs only AFTER all scripts are ready ***
document.addEventListener('DOMContentLoaded', loadUsers);
window.addEventListener('load', loadUsers); // Double check for maximum compatibility

// In src/main/resources/static/js/admin-dashboard.js

// ... (Existing code for onboardBtn and loadUsers function)

// =================== LOGIC FOR LOGOUT ===================
const logoutAdminBtn = document.getElementById("logoutAdminBtn");
if (logoutAdminBtn) {
    logoutAdminBtn.addEventListener("click", () => {
        // Since there's no official admin token, we just redirect to the admin login page
        window.location.href = "admin-login.html";
    });
}


// =================== LOGIC FOR DELETING ALL USERS ===================
const deleteAllUsersBtn = document.getElementById("deleteAllUsersBtn");
if (deleteAllUsersBtn) {
    deleteAllUsersBtn.addEventListener("click", async () => {
        if (!confirm("WARNING: Are you sure you want to delete ALL users? This action cannot be undone.")) {
            return;
        }

        try {
            // Use the deleteData function from common.js
            // The endpoint is /api/auth/delete-all-users (handled by UserController.java)
            const result = await deleteData("/delete-all-users");

            alert(result);

            // Reload the user list to show an empty table
            loadUsers();
        } catch (error) {
            console.error("Error deleting users:", error);
            alert("Failed to delete all users.");
        }
    });
}