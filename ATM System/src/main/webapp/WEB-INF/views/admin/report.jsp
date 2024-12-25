<%--
  Created by IntelliJ IDEA.
  User: Nguyen Ngoc Hai
  Date: 12/25/2024
  Time: 5:32 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM - Report</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="dashboard-container">
    <!-- Navbar -->
    <nav class="navbar">
        <h2>ATM System</h2>
        <button onclick="logout()" class="btn-logout">Logout</button>
    </nav>

    <!-- Report Content -->
    <div class="main-content">
        <div class="card">
            <div class="card-header">
                <div class="report-header">
                    <h2 id="reportTitle"></h2>
                    <button onclick="backToDashboard()" class="btn-secondary">Back to Dashboard</button>
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
                        </thead>
                        <tbody>
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
    // Fetch report type from URL query parameters
    let reportType = new URLSearchParams(window.location.search).get('type');
    let reportTable;

    document.addEventListener('DOMContentLoaded', function () {
        initializeReport();
    });

    function initializeReport() {
        if (!reportType) {
            alert('Report type not specified');
            return;
        }

        // Set the title based on the report type
        const titles = {
            'withdraw': 'Withdrawal Report',
            'deposit': 'Deposit Report',
            'transfer': 'Transfer Report',
            'account': 'Account Report'
        };

        document.getElementById('reportTitle').textContent = titles[reportType] || 'Report';

        // Initialize the table and generate the initial report
        initializeTable(reportType);
        generateReport();
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
        let date = document.getElementById('reportDate').value;

        // Validate inputs
        if (!reportType) {
            alert('Report type is not set');
            return;
        }

        // Add headers to indicate this is an AJAX request
        fetch('${pageContext.request.contextPath}/admin/report?type=' + reportType + '&date=' + date, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch report');
                }
                return response.json();
            })
            .then(data => {
                if (reportTable) {
                    reportTable.clear();
                    reportTable.rows.add(data);
                    reportTable.draw();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error generating report: ' + error.message);
            });
    }

    function backToDashboard() {
        window.location.href = '/ATM-System/admin/dashboard';
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


<%--<script>--%>
<%--    let reportType = new URLSearchParams(window.location.search).get('type'); // Get report type from query param--%>
<%--    let reportTable;--%>

<%--    document.addEventListener('DOMContentLoaded', function () {--%>
<%--        initializeReport();--%>
<%--    });--%>

<%--    function initializeReport() {--%>
<%--        if (!reportType) {--%>
<%--            alert('Report type not specified');--%>
<%--            return;--%>
<%--        }--%>

<%--        const titles = {--%>
<%--            'withdraw': 'Withdrawal Report',--%>
<%--            'deposit': 'Deposit Report',--%>
<%--            'transfer': 'Transfer Report',--%>
<%--            'account': 'Account Report'--%>
<%--        };--%>
<%--        document.getElementById('reportTitle').textContent = titles[reportType] || 'Report';--%>

<%--        // Set today's date as default--%>
<%--        const today = new Date().toISOString().split('T')[0];--%>
<%--        const dateInput = document.getElementById('reportDate');--%>
<%--        dateInput.value = today;--%>

<%--        // Initialize the DataTable for the selected report--%>
<%--        initializeTable(reportType);--%>
<%--        generateReport(); // Generate report for the current day on page load--%>
<%--    }--%>

<%--    function initializeTable(type) {--%>
<%--        const columns = getColumnsForType(type);--%>

<%--        // Destroy any existing DataTable instance--%>
<%--        if (reportTable) {--%>
<%--            reportTable.destroy();--%>
<%--        }--%>

<%--        reportTable = $('#reportTable').DataTable({--%>
<%--            columns: columns,--%>
<%--            order: [[0, 'desc']],--%>
<%--            pageLength: 10,--%>
<%--            responsive: true--%>
<%--        });--%>
<%--    }--%>

<%--    function getColumnsForType(type) {--%>
<%--        const commonColumns = [--%>
<%--            { data: 'transactionDate', title: 'Date/Time' },--%>
<%--            { data: 'cardNumber', title: 'Card Number' },--%>
<%--            { data: 'amount', title: 'Amount', render: data => '$' + Number(data).toFixed(2) },--%>
<%--            { data: 'balanceAfter', title: 'Balance After', render: data => '$' + Number(data).toFixed(2) }--%>
<%--        ];--%>

<%--        if (type === 'account') {--%>
<%--            return [--%>
<%--                { data: 'userId', title: 'User ID' },--%>
<%--                { data: 'cardNumber', title: 'Card Number' },--%>
<%--                { data: 'name', title: 'Name' },--%>
<%--                { data: 'contactNumber', title: 'Contact' },--%>
<%--                { data: 'balance', title: 'Balance', render: data => '$' + Number(data).toFixed(2) }--%>
<%--            ];--%>
<%--        } else if (type === 'transfer') {--%>
<%--            return [--%>
<%--                ...commonColumns,--%>
<%--                { data: 'toCardNumber', title: 'To Card Number' },--%>
<%--                { data: 'description', title: 'Description' }--%>
<%--            ];--%>
<%--        }--%>
<%--        return commonColumns;--%>
<%--    }--%>

<%--    function generateReport(date = document.getElementById('reportDate').value) {--%>
<%--        // Validate the current report type--%>
<%--        if (!reportType) {--%>
<%--            alert('Report type is not set');--%>
<%--            return;--%>
<%--        }--%>

<%--        if (!date) {--%>
<%--            alert('Date is not set');--%>
<%--            return;--%>
<%--        }--%>

<%--        fetch(`${pageContext.request.contextPath}/admin/report?type=` + reportType + `&date=` + date)--%>
<%--            .then(response => {--%>
<%--                if (!response.ok) {--%>
<%--                    throw new Error('Failed to fetch report');--%>
<%--                }--%>
<%--                return response.json();--%>
<%--            })--%>
<%--            .then(data => {--%>
<%--                reportTable.clear();--%>
<%--                reportTable.rows.add(data);--%>
<%--                reportTable.draw();--%>
<%--            })--%>
<%--            .catch(error => {--%>
<%--                console.error('Error:', error);--%>
<%--                alert('Error generating report');--%>
<%--            });--%>
<%--    }--%>

<%--    function backToDashboard() {--%>
<%--        window.location.href = '${pageContext.request.contextPath}/admin/dashboard.jsp';--%>
<%--    }--%>

<%--    function logout() {--%>
<%--        fetch('${pageContext.request.contextPath}/logout')--%>
<%--            .then(() => {--%>
<%--                window.location.href = '${pageContext.request.contextPath}/';--%>
<%--            });--%>
<%--    }--%>
<%--</script>--%>
</body>
</html>

<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
        <div id="reportView" class="card" style="display: none;">
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
--%>

<%--<script>
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
</script>--%>


