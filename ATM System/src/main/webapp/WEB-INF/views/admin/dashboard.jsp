<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM - Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="dashboard-container">
    <!-- Navbar -->
    <nav class="navbar">
        <h2>ATM System</h2>
        <button onclick="logout()" class="btn-logout">Logout</button>
    </nav>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Welcome Section -->
        <div class="card balance-section">
            <h2>Welcome Admin</h2>
<%--            <div id="adminName" class="admin-title">Admin</div>--%>
        </div>

        <!-- Report Options -->
        <div id="reportButtons" class="actions-grid">
            <div class="action-card" onclick="redirectToReport('withdraw')">
                <h3>Withdraw Report</h3>
                <p>View withdraw transactions</p>
            </div>

            <div class="action-card" onclick="redirectToReport('deposit')">
                <h3>Deposit Report</h3>
                <p>View deposit transactions</p>
            </div>

            <div class="action-card" onclick="redirectToReport('transfer')">
                <h3>Transfer Report</h3>
                <p>View transfer transactions</p>
            </div>

            <div class="action-card" onclick="redirectToReport('account')">
                <h3>Account Management</h3>
                <p>Manage customer accounts</p>
            </div>
        </div>
    </div>
</div>

<script>
    <%--// Fetch the username on page load--%>
    <%--window.onload = function () {--%>
    <%--    fetchUsername();--%>
    <%--}--%>

    <%--function fetchUsername() {--%>
    <%--    fetch('${pageContext.request.contextPath}/admin/username')--%>
    <%--        .then(response => response.json())--%>
    <%--        .then(data => {--%>
    <%--             // Store username--%>
    <%--            document.getElementById('username').textContent = data.username;--%>
    <%--        })--%>
    <%--        .catch(error => {--%>
    <%--            console.error('Error:', error);--%>
    <%--            alert('Error fetching username')--%>
    <%--        })--%>
    <%--}--%>

    // Redirect to report page based on type
    function redirectToReport(type) {
        window.location.href = `${pageContext.request.contextPath}/admin/report?type=` + type;
    }

    // Logout function
    function logout() {
        fetch(`${pageContext.request.contextPath}/logout`)
            .then(() => {
                window.location.href = `${pageContext.request.contextPath}/`;
            });
    }
</script>
</body>
</html>
