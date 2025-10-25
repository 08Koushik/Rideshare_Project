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

        // Clear the existing content before populating or adding the message
        usersTableBody.innerHTML = '';

        if (Array.isArray(users) && users.length > 0) {
            // Case 1: Users exist, populate the table
            users.forEach(user => {
                const row = usersTableBody.insertRow();

                // Populate standard cells (Update this section if you fully implemented 8 columns in the HTML/previous steps)
                row.insertCell().textContent = user.id;
                row.insertCell().textContent = user.name;
                row.insertCell().textContent = user.email;
                row.insertCell().textContent = user.contactNumber;
                row.insertCell().textContent = user.roleType;

                // The original file only showed 6 cells, but assuming you implemented the full 8-column table:
                row.insertCell().textContent = user.roleType === 'DRIVER' ? (user.vehicleDetails || 'N/A') : 'N/A';
                row.insertCell().textContent = user.roleType === 'DRIVER' ? (user.driverLicenseNumber || 'N/A') : 'N/A';
                row.insertCell().textContent = user.roleType === 'PASSENGER' ? (user.aadharNumber || 'N/A') : 'N/A';
            });

        } else if (Array.isArray(users) && users.length === 0) {
            // Case 2: No users, display the "No data available" message
            const row = usersTableBody.insertRow();
            const cell = row.insertCell(0);

            // Set colSpan to the total number of columns in your table (8 columns: ID, Name, Email, Contact, Role, Vehicle, License, Aadhar)
            // If you only have 6 columns, change this to 6.
            cell.colSpan = 8;

            cell.textContent = "No data available.";
            cell.style.textAlign = 'center'; // Optional: Center the text for a better look
        }

    } catch (error) {
        console.error("Failed to load users:", error);
        const row = usersTableBody.insertRow();
        // Use the same colSpan here for error messages
        row.insertCell(0).textContent = "Error loading user data.";
        row.cells[0].colSpan = 8;
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


// In src/main/resources/static/js/admin-dashboard.js

// ... (Existing code before the deletion logic)

// =================== LOGIC FOR DELETING ALL USERS ===================
const deleteAllUsersBtn = document.getElementById("deleteAllUsersBtn");
if (deleteAllUsersBtn) {
    deleteAllUsersBtn.addEventListener("click", async () => {

        // Use SweetAlert for confirmation
        const result = await Swal.fire({
            title: 'WARNING: Delete All Users?',
            text: 'Are you sure you want to delete ALL users? This action cannot be undone.',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Yes, delete all!'
        });

        if (!result.isConfirmed) {
            return;
        }

        try {
            const resultText = await deleteData("/delete-all-users");

            // SweetAlert for SUCCESS
            Swal.fire({
                icon: 'success',
                title: 'Deleted!',
                text: resultText
            });

            // Reload the user list to show an empty table
            loadUsers();
        } catch (error) {
            // Note: The error alert is now handled by common.js, which should use SweetAlert.
            // If you still need a specific local message, add it here:
            console.error("Error deleting users:", error);
        }
    });
}