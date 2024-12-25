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
        <!-- Initial View - Report Buttons -->
        <div class="card balance-section">
            <h3>Welcome Admin</h3>
            <div id="adminName" class="balance-amount">Admin</div>
        </div>

        <!-- Grid of action cards -->
        <div id="reportButtons" class="actions-grid">
            <div class="action-card" onclick="showReport('withdraw')">
                <h3>Withdraw Report</h3>
                <p>Get withdraw report</p>
            </div>

            <div class="action-card" onclick="showReport('deposit')">
                <h3>Deposit Report</h3>
                <p>Get deposit report</p>
            </div>

            <div class="action-card" onclick="showReport('transfer')">
                <h3>Transfer Report</h3>
                <p>Get transfer report</p>
            </div>

            <div class="action-card" onclick="showReport('account')">
                <h3>Account Report</h3>
                <p>View list of all register user</p>
            </div>
        </div>

        <!-- Report View (Initially Hidden) -->
        <div id="reportView" class="card">
            <div class="card-header">
                <div class="report-header">
                    <h2 id="reportTitle"></h2>
                    <button onclick="showDashboard()" class="btn-secondary">Back to Dashboard</button>
                </div>
            </div>
            <div class="card-body">
                <div class="report-controls">
                    <div class="date-filter">
                        <label for="reportDate">Select Date:</label>
                        <input type="date" id="reportDate" class="form-control" value="${currentDate}">
                        <button onclick="generateReport()" class="btn btn-primary">Generate</button>
                    </div>
                </div>

                <div class="table-responsive">
                    <table id="reportTable" class="report-table">
                        <thead>
                        <!-- Headers will be set dynamically -->
                        </thead>
                        <tbody>
                        <!-- Data will be populated dynamically -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.datatables.net/2.0.0/js/dataTables.min.js"></script>
<script>
    let currentReportType = '';
    let reportTable;

    function showReport(type) {
        currentReportType = type;
        document.getElementById('reportButtons').style.display = 'none';
        document.getElementById('reportView').style.display = 'block';

        const titles = {
            'withdraw': 'Withdrawal Report',
            'deposit': 'Deposit Report',
            'transfer': 'Transfer Report',
            'account': 'Account Report'
        };
        document.getElementById('reportTitle').textContent = titles[type];

        // Initialize the DataTable for the selected report
        initializeTable(type);
        generateReport();
    }

    function showDashboard() {
        document.getElementById('reportButtons').style.display = 'block';
        document.getElementById('reportView').style.display = 'none';

        // Destroy the existing DataTable instance to reset it
        if (reportTable) {
            reportTable.destroy();
            $('#reportTable').empty(); // Clear the table HTML
        }
    }

    function initializeTable(type) {
        const columns = getColumnsForType(type);

        // Destroy any existing DataTable instance
        if (reportTable) {
            reportTable.destroy();
        }

        // Initialize a new DataTable instance
        reportTable = $('#reportTable').DataTable({
            columns: columns,
            order: [[0, 'desc']],
            pageLength: 10,
            responsive: true
        });
    }

    function getColumnsForType(type) {
        const commonColumns = [
            { data: 'transactionDate', title: 'Date/Time' },
            { data: 'cardNumber', title: 'Card Number' },
            { data: 'amount', title: 'Amount', render: data => '$' + Number(data).toFixed(2) },
            { data: 'balanceAfter', title: 'Balance After', render: data => '$' + Number(data).toFixed(2) }
        ];

        if (type === 'account') {
            return [
                { data: 'userId', title: 'User ID' },
                { data: 'cardNumber', title: 'Card Number' },
                { data: 'name', title: 'Name' },
                { data: 'contactNumber', title: 'Contact' },
                { data: 'balance', title: 'Balance', render: data => '$' + Number(data).toFixed(2) }
            ];
        } else if (type === 'transfer') {
            return [
                ...commonColumns,
                { data: 'toCardNumber', title: 'To Card Number' },
                { data: 'description', title: 'Description' }
            ];
        }
        return commonColumns;
    }

    function generateReport() {
        const date = document.getElementById('reportDate').value

        // Validate the current report type
        if (!currentReportType) {
            alert('Report type is not set');
            return;
        }

        fetch(`${pageContext.request.contextPath}/admin/report?type=` + currentReportType + `&date=` + date)
            .then(response => response.json())
            .then(data => {
                if (reportTable) {
                    reportTable.clear(); // Clear existing data
                    reportTable.rows.add(data); // Add new data
                    reportTable.draw();// Redraw the table
                } else {
                    console.error('Report table is not initialized.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error generating report');
            });
    }

    function logout() {
        fetch('${pageContext.request.contextPath}/logout')
            .then(() => {
                window.location.href = '${pageContext.request.contextPath}/';
            })
            .catch(error => {
                console.error('Error during logout:', error);
                alert('Error during logout.');
            });
    }
</script>
</body>
</html>