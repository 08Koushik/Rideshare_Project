// In src/main/resources/static/js/admin-dashboard.js

let allUsers = [];
let userRoleChartInstance = null; // Variable to hold the chart instance

// =================== BUTTON AND NAVIGATION LOGIC ===================

const onboardBtn = document.getElementById("onboardBtn");
if (onboardBtn) {
  onboardBtn.addEventListener("click", () => {
    window.location.href = "onboard-user.html";
  });
}

document.addEventListener('DOMContentLoaded', () => {
    loadUsers();

    const searchInput = document.getElementById('searchUserInput');
    const roleFilter = document.getElementById('roleFilter');

    if (searchInput) {
        searchInput.addEventListener('input', updateDashboard);
    }
    if (roleFilter) {
        roleFilter.addEventListener('change', updateDashboard);
    }

    // --- DYNAMIC NAVIGATION LOGIC ---
    const navUsers = document.getElementById('navUsers');
    const navSettings = document.getElementById('navSettings');

    if (navUsers) {
        navUsers.addEventListener('click', () => {
            Swal.fire({
                icon: 'info',
                title: 'Users Page',
                text: 'This page would display the full user list table (Pending: Implement user-list.html).'
            });
        });
    }

    if (navSettings) {
        navSettings.addEventListener('click', () => {
            Swal.fire({
                icon: 'info',
                title: 'Settings Page',
                text: 'This page is for administrative settings (Pending: Implement settings.html).'
            });
        });
    }
    // ------------------------------------
});

// =================== LOGIC: CHART RENDERING ===================

function renderChart(driverCount, passengerCount, adminCount) {
    const ctx = document.getElementById('userRoleChart');

    // Destroy existing chart if it exists
    if (userRoleChartInstance) {
        userRoleChartInstance.destroy();
    }

    // Create a new Pie Chart instance
    userRoleChartInstance = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['Drivers', 'Passengers', 'Admins'],
            datasets: [{
                label: 'User Role Distribution',
                data: [driverCount, passengerCount, adminCount],
                backgroundColor: [
                    '#43e97b', // Green for Drivers (DR)
                    '#f76b1c', // Orange for Passengers (PS)
                    '#6f54ff'  // Purple for Admins (US)
                ],
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#333' // Chart.js legend text color for light dashboard
                    }
                },
                title: {
                    display: false,
                }
            }
        }
    });
}

// =================== LOGIC: CALCULATE AND DISPLAY ROLE COUNTS ===================

function updateCounts(users, isFiltered = false) {
    // 1. Calculate counts for all roles (based on the provided 'users' list)
    const driverCount = users.filter(user => user.roleType === 'DRIVER').length;
    const passengerCount = users.filter(user => user.roleType === 'PASSENGER').length;
    const adminCount = users.filter(user => user.roleType === 'ADMIN').length;
    const totalMatchingUsers = users.length;

    // 2. Update the HTML badges with FILTERED/MATCHING counts
    document.getElementById('usBadgeCount').textContent = totalMatchingUsers;
    document.getElementById('driverCount').textContent = driverCount;
    document.getElementById('passengerCount').textContent = passengerCount;

    // 3. The chart and overall header count always reflect the total system state.
    if (!isFiltered) {
        document.getElementById('totalUsersCountDisplay').textContent = allUsers.length;
        renderChart(driverCount, passengerCount, adminCount);
    }
}

const getCurrentFilteredUsers = () => {
    const searchTerm = document.getElementById('searchUserInput').value.toLowerCase();
    const role = document.getElementById('roleFilter').value;

    return allUsers.filter(user => {
        const matchesSearch = user.name.toLowerCase().includes(searchTerm) || user.email.toLowerCase().includes(searchTerm);
        const matchesRole = !role || user.roleType === role;
        return matchesSearch && matchesRole;
    });
};

function updateDashboard() {
    const filteredUsers = getCurrentFilteredUsers();
    updateCounts(filteredUsers, true);
}


async function loadUsers() {
    if (typeof BASE_URL === 'undefined') {
        console.error("Critical: BASE_URL is still undefined.");
        alert("Error loading dependencies. Please clear cache and refresh.");
        return;
    }

    try {
        const users = await getData("/admin/users");
        allUsers = Array.isArray(users) ? users : [];

        updateCounts(allUsers, false);

    } catch (error) {
        console.error("Failed to load users:", error);
    }
}

// =================== LOGIC FOR LOGOUT/DELETE ===================

const logoutAdminBtn = document.getElementById("logoutAdminBtn");
if (logoutAdminBtn) {
    logoutAdminBtn.addEventListener("click", () => {
        window.location.href = "user-login.html";
    });
}

const deleteAllUsersBtn = document.getElementById("deleteAllUsersBtn");
if (deleteAllUsersBtn) {
    deleteAllUsersBtn.addEventListener("click", async () => {

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

            Swal.fire({
                icon: 'success',
                title: 'Deleted!',
                text: resultText
            });

            loadUsers(); // Reload to update counts and chart
        } catch (error) {
            console.error("Error deleting users:", error);
        }
    });
}