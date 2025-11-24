// src/main/resources/static/js/user-list.js

const BASE_URL_AUTH = "http://localhost:8080/api/auth";

document.addEventListener('DOMContentLoaded', () => {
    loadUserList();
});

async function loadUserList() {
    const container = document.getElementById('userTableContainer');
    container.innerHTML = '<p>Fetching users...</p>';

    try {
        const response = await fetch(`${BASE_URL_AUTH}/admin/users`);
        if (!response.ok) throw new Error('Failed to fetch user data.');

        const users = await response.json();

        if (users.length === 0) {
            container.innerHTML = '<p>No users found in the system.</p>';
            return;
        }

        renderUserTable(users);

    } catch (error) {
        console.error("Error loading user list:", error);
        container.innerHTML = `<p style="color:red;">Error loading users: ${error.message}</p>`;
    }
}

function renderUserTable(users) {
    const container = document.getElementById('userTableContainer');
    let tableHtml = `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Verified</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>`;

    users.forEach(user => {
        const isVerified = user.verified || false;
        const isBlocked = user.blocked || false;

        const verifiedStatus = isVerified
            ? `<span class="status-VERIFIED">YES</span>`
            : `<span class="status-NOT-VERIFIED">NO</span>`;

        const blockedStatus = isBlocked
            ? `<span class="status-BLOCKED">BLOCKED</span>`
            : `Active`;

        // Action Buttons
        let actionButtons = '';

        // MODIFIED LOGIC: Show Verify button if the user is a DRIVER OR PASSENGER and is NOT verified.
        if ((user.roleType === 'DRIVER' || user.roleType === 'PASSENGER') && !isVerified) {
            actionButtons += `<button class="action-btn" onclick="updateUserStatus(${user.id}, 'verified', true)">Verify</button>`;
        }

        if (isBlocked) {
            actionButtons += `<button class="action-btn" style="background-color:#4CAF50; color:white;" onclick="updateUserStatus(${user.id}, 'blocked', false)">Unblock</button>`;
        } else {
            actionButtons += `<button class="action-btn" style="background-color:#f44336; color:white;" onclick="updateUserStatus(${user.id}, 'blocked', true)">Block</button>`;
        }


        tableHtml += `
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>${user.roleType}</td>
                <td>${verifiedStatus}</td>
                <td>${blockedStatus}</td>
                <td>${actionButtons}</td>
            </tr>`;
    });

    tableHtml += `</tbody></table>`;
    container.innerHTML = tableHtml;
}

async function updateUserStatus(userId, type, value) {
    const action = value ? type : `un${type}`;

    const result = await Swal.fire({
        title: `Confirm ${action.toUpperCase()}?`,
        text: `Are you sure you want to set user ${userId}'s ${type} status to ${value}?`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, proceed!'
    });

    if (!result.isConfirmed) {
        return;
    }

    // Calls POST /api/auth/admin/user/{userId}/status?type={type}&value={value}
    const url = `${BASE_URL_AUTH}/admin/user/${userId}/status?type=${type}&value=${value}`;

    try {
        const response = await fetch(url, { method: 'POST' });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `Failed to update status.`);
        }

        Swal.fire('Success', `User ${userId} status updated!`, 'success');
        loadUserList(); // Reload the table
    } catch (error) {
        Swal.fire('Error', `Operation failed: ${error.message}`, 'error');
    }
}