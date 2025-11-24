// In src/main/resources/static/js/admin-dashboard.js

let allUsers = [];
let userRoleChartInstance = null; // Variable to hold the pie chart instance
const BASE_REPORT_URL = "http://localhost:8080/api/"; // Base URL for monitoring endpoints

// =================== BUTTON AND NAVIGATION LOGIC ===================

const onboardBtn = document.getElementById("onboardBtn");
if (onboardBtn) {
  onboardBtn.addEventListener("click", () => {
    window.location.href = "admin-onboard-user.html";
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

    // --- DYNAMIC NAVIGATION LOGIC (Stubs) ---
    const navUsers = document.getElementById('navUsers');
    const navSettings = document.getElementById('navSettings');

   if (navUsers) {
           navUsers.addEventListener('click', () => {

               window.location.href = "user-list.html";
           });
       }
    if (navSettings) {
            navSettings.addEventListener('click', () => {

                window.location.href = "settings.html";
            });
        }
    // Attach listener for the new "Monitor All Data" button
    const monitorDataBtn = document.getElementById("monitorDataBtn");
    if (monitorDataBtn) {
        monitorDataBtn.addEventListener("click", async () => {
            Swal.fire({
                title: 'Monitoring Dashboard',
                html:
                    'Select the data stream you want to monitor (first 10 records shown):<br>' +
                    '<button id="viewRidesBtn" class="swal2-styled" style="margin: 10px 5px; background-color: #4facfe;">View All Rides</button>' +
                    '<button id="viewBookingsBtn" class="swal2-styled" style="margin: 10px 5px; background-color: #4facfe;">View All Bookings</button>' +
                    '<button id="viewPaymentsBtn" class="swal2-styled" style="margin: 10px 5px; background-color: #4facfe;">View All Payments</button>',
                showCancelButton: true,
                showConfirmButton: false,
                didOpen: () => {
                    document.getElementById('viewRidesBtn').addEventListener('click', () => viewDataStream('Rides', BASE_REPORT_URL + 'rides/admin/rides'));
                    document.getElementById('viewBookingsBtn').addEventListener('click', () => viewDataStream('Bookings', BASE_REPORT_URL + 'booking/admin/bookings'));
                    document.getElementById('viewPaymentsBtn').addEventListener('click', () => viewDataStream('Payments', BASE_REPORT_URL + 'payments/admin/payments'));
                }
            });
        });
    }
});

// =================== LOGIC: CHART RENDERING ===================

// MODIFIED PIE CHART: Displays Drivers, Passengers, Total Rides, and Total Bookings
function renderChart(driverCount, passengerCount, ridesCount, bookingsCount) {
    const ctx = document.getElementById('userRoleChart');

    // Destroy existing chart if it exists
    if (userRoleChartInstance) {
        userRoleChartInstance.destroy();
    }

    userRoleChartInstance = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['Drivers', 'Passengers', 'Total Rides', 'Total Bookings'],
            datasets: [{
                label: 'System Distribution',
                data: [driverCount, passengerCount, ridesCount, bookingsCount],
                backgroundColor: [
                    '#43e97b', // Green (Drivers)
                    '#f76b1c', // Orange (Passengers)
                    '#ff9b6b', // Light Orange (Rides)
                    '#6f54ff'  // Purple (Bookings)
                ],
                hoverOffset: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#333'
                    }
                },
                title: {
                    display: false,
                }
            }
        }
    });
}


// =================== LOGIC: CALCULATE AND DISPLAY ROLE COUNTS AND REPORTS ===================

function updateCounts(users, isFiltered = false) {
    // 1. Calculate counts for all roles
    const driverCount = users.filter(user => user.roleType === 'DRIVER').length;
    const passengerCount = users.filter(user => user.roleType === 'PASSENGER').length;
    const adminCount = users.filter(user => user.roleType === 'ADMIN').length;
    const totalMatchingUsers = users.length;

    // 2. Update the HTML badges with FILTERED/MATCHING counts
    document.getElementById('usBadgeCount').textContent = totalMatchingUsers;
    document.getElementById('driverCount').textContent = driverCount;
    document.getElementById('passengerCount').textContent = passengerCount;

    // 3. The overall header count reflects the total system state.
    if (!isFiltered) {
        document.getElementById('totalUsersCountDisplay').textContent = allUsers.length;
        // NOTE: Chart rendering is now handled by loadReports()
    }
}

// MODIFIED: Load Aggregated Reports (Rides, Earnings, BKS) and Render Chart
async function loadReports() {
    try {
        // Calls the new endpoint: /api/auth/admin/report
        const report = await getData("/admin/report");

        if (report) {
            document.getElementById('totalRidesCount').textContent = report.totalRides || 0;
            document.getElementById('totalEarnings').textContent = `₹${(report.totalEarnings || 0).toFixed(2)}`;
            document.getElementById('totalBookingsCount').textContent = report.totalBookings || 0;

            // Update the large earnings overview card
            document.getElementById('overview-earnings-value').textContent = `₹${(report.totalEarnings || 0).toFixed(2)}`;

            // Get User Counts from the globally available list for the chart
            const driverCount = allUsers.filter(user => user.roleType === 'DRIVER').length;
            const passengerCount = allUsers.filter(user => user.roleType === 'PASSENGER').length;

            // RENDER PIE CHART with the four requested metrics
            renderChart(
                driverCount,
                passengerCount,
                report.totalRides,
                report.totalBookings
            );
        }

    } catch (error) {
        console.error("Failed to load aggregated report data:", error);
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
        return;
    }

    try {
        const users = await getData("/admin/users");
        allUsers = Array.isArray(users) ? users : [];

        updateCounts(allUsers, false);
        loadReports(); // Call new function to load report data AND render chart

    } catch (error) {
        console.error("Failed to load users:", error);
    }
}


// MODIFIED FUNCTION: Generic Data Viewer for Monitoring All Data with Pagination and Search
let monitorDataCache = [];
let monitorCurrentPage = 1;
const monitorItemsPerPage = 10;
let monitorSearchTerm = '';

async function viewDataStream(title, url) {
    Swal.fire({
        title: `Loading All ${title}`,
        text: 'Fetching full dataset from backend...',
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });

    // List of keys to exclude from display
    const EXCLUDED_KEYS = [
        'sourceLatitude',
        'sourceLongitude',
        'destinationLatitude',
        'destinationLongitude',
        'vehicleImageReference'
    ];

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error("Failed to fetch data.");
        const data = await response.json();
        
        // Store data in cache
        monitorDataCache = data;
        monitorCurrentPage = 1;
        monitorSearchTerm = '';

        displayMonitorData(title, EXCLUDED_KEYS);

    } catch (error) {
        Swal.fire('Error', `Failed to load ${title.toLowerCase()}: ${error.message}`, 'error');
    }
}

function displayMonitorData(title, excludedKeys) {
    // Filter data based on search term
    let filteredData = monitorDataCache;
    if (monitorSearchTerm) {
        filteredData = monitorDataCache.filter(item => {
            return Object.values(item).some(value => {
                if (value === null || value === undefined) return false;
                return String(value).toLowerCase().includes(monitorSearchTerm.toLowerCase());
            });
        });
    }

    const totalPages = Math.ceil(filteredData.length / monitorItemsPerPage);
    const startIndex = (monitorCurrentPage - 1) * monitorItemsPerPage;
    const endIndex = startIndex + monitorItemsPerPage;
    const pageData = filteredData.slice(startIndex, endIndex);

    let tableHtml = '<div style="margin-bottom: 15px;">';
    tableHtml += '<input type="text" id="monitorSearchInput" placeholder="Search..." style="padding: 8px; border: 1px solid #ccc; border-radius: 5px; width: 300px; margin-right: 10px;" value="' + monitorSearchTerm + '">';
    tableHtml += '<span style="color: #333;">Showing ' + (startIndex + 1) + '-' + Math.min(endIndex, filteredData.length) + ' of ' + filteredData.length + ' records</span>';
    tableHtml += '</div>';
    
    tableHtml += '<div style="max-height: 400px; overflow-y: auto;">';
    if (filteredData.length === 0) {
        tableHtml += `<p>No ${title.toLowerCase()} found.</p>`;
    } else {
        // Get all keys and filter out the excluded ones
        const allKeys = Object.keys(monitorDataCache[0]);
        const filteredKeys = allKeys.filter(key => !excludedKeys.includes(key));

        tableHtml += `<table style="width:100%; font-size: 0.8em; border-collapse: collapse;"><thead><tr>`;
        filteredKeys.forEach(key => tableHtml += `<th style="border: 1px solid #ccc; padding: 5px; background-color: #f0f0f0;">${key}</th>`);
        tableHtml += `</tr></thead><tbody>`;

        pageData.forEach(item => {
            tableHtml += `<tr>`;
            filteredKeys.forEach(key => {
                let value = item[key];
                if (typeof value === 'object' && value !== null) {
                    value = JSON.stringify(value);
                }
                if (typeof value === 'string' && value.length > 30) {
                    value = value.substring(0, 30) + '...'; // Truncate long strings
                }
                tableHtml += `<td style="border: 1px solid #ccc; padding: 5px; text-align: left;">${value}</td>`;
            });
            tableHtml += `</tr>`;
        });
        tableHtml += `</tbody></table>`;
    }
    tableHtml += '</div>';

    // Add pagination controls
    if (totalPages > 1) {
        tableHtml += '<div class="pagination" style="display: flex; justify-content: center; align-items: center; gap: 10px; margin-top: 20px;">';
        tableHtml += '<button id="monitorFirstPage" style="padding: 8px 12px; background-color: #4facfe; color: white; border: none; border-radius: 5px; cursor: pointer;" ' + (monitorCurrentPage === 1 ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : '') + '>First</button>';
        tableHtml += '<button id="monitorPrevPage" style="padding: 8px 12px; background-color: #4facfe; color: white; border: none; border-radius: 5px; cursor: pointer;" ' + (monitorCurrentPage === 1 ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : '') + '>Previous</button>';
        tableHtml += '<span style="color: #333;">Page ' + monitorCurrentPage + ' of ' + totalPages + '</span>';
        tableHtml += '<button id="monitorNextPage" style="padding: 8px 12px; background-color: #4facfe; color: white; border: none; border-radius: 5px; cursor: pointer;" ' + (monitorCurrentPage === totalPages ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : '') + '>Next</button>';
        tableHtml += '<button id="monitorLastPage" style="padding: 8px 12px; background-color: #4facfe; color: white; border: none; border-radius: 5px; cursor: pointer;" ' + (monitorCurrentPage === totalPages ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : '') + '>Last</button>';
        tableHtml += '</div>';
    }

    Swal.fire({
        title: `Monitoring: All ${title}`,
        html: tableHtml,
        width: '95%',
        showConfirmButton: true,
        confirmButtonText: 'Close',
        didOpen: () => {
            // Attach event listeners
            const searchInput = document.getElementById('monitorSearchInput');
            if (searchInput) {
                searchInput.addEventListener('input', (e) => {
                    monitorSearchTerm = e.target.value;
                    monitorCurrentPage = 1;
                    displayMonitorData(title, excludedKeys);
                });
            }

            const firstBtn = document.getElementById('monitorFirstPage');
            if (firstBtn) {
                firstBtn.addEventListener('click', () => {
                    monitorCurrentPage = 1;
                    displayMonitorData(title, excludedKeys);
                });
            }

            const prevBtn = document.getElementById('monitorPrevPage');
            if (prevBtn) {
                prevBtn.addEventListener('click', () => {
                    if (monitorCurrentPage > 1) {
                        monitorCurrentPage--;
                        displayMonitorData(title, excludedKeys);
                    }
                });
            }

            const nextBtn = document.getElementById('monitorNextPage');
            if (nextBtn) {
                nextBtn.addEventListener('click', () => {
                    const totalPages = Math.ceil(filteredData.length / monitorItemsPerPage);
                    if (monitorCurrentPage < totalPages) {
                        monitorCurrentPage++;
                        displayMonitorData(title, excludedKeys);
                    }
                });
            }

            const lastBtn = document.getElementById('monitorLastPage');
            if (lastBtn) {
                lastBtn.addEventListener('click', () => {
                    monitorCurrentPage = Math.ceil(filteredData.length / monitorItemsPerPage);
                    displayMonitorData(title, excludedKeys);
                });
            }
        }
    });
}

// =================== LOGIC FOR LOGOUT/DELETE (Existing) ===================

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
            // Note: BASE_URL points to /api/auth in common.js
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