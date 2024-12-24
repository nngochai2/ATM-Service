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
        <div class="actions-grid">
            <div class="action-card" onclick="showTransactionForm('withdraw')">
                <h3>Withdraw Report</h3>
                <p>Get withdraw report</p>
            </div>

            <div class="action-card" onclick="showTransactionForm('deposit')">
                <h3>Deposit Report</h3>
                <p>Get deposit report</p>
            </div>

            <div class="action-card" onclick="showTransactionForm('transfer')">
                <h3>Transfer Report</h3>
                <p>Get transfer report</p>
            </div>

            <div class="action-card" onclick="showChangePin()">
                <h3>Account Report</h3>
                <p>View list of all register user</p>
            </div>
        </div>

        <!-- Modal for transaction form -->
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

        // Set report title
        const titles = {
            'withdraw': 'Withdrawal Report',
            'deposit': 'Deposit Report',
            'transfer': 'Transfer Report',
            'account': 'Account Report'
        };
        document.getElementById('reportTitle').textContent = titles[type];

        // Initialize the table based on report type
        initalizeTable(type);
        generateReport();
    }

    function showDashboard() {
        document.getElementById('reportButtons').style.display = 'block';
        document.getElementById('reportView').style.display = 'none';

        if (reportTable) {
            reportTable.destroy();
        }
    }

    function initializeTable(type) {
        const columns = getColumnsForType(type);

        if (reportTable) {
            reportTable.destroy();
        }

        reportTable = ${'#reportTable'}.DataTable({
            columns: columns,
            order: [[0, 'desc']],
            pageLength: 10,
            responsive: true
        })
    }

    function getColumnsForType(type) {
        const commonColumns = [
            {
                data: 'transactionDate',
                title: 'Date/Time'
            },
            {
                data: 'cardNumber',
                title: 'Card Number'
            },
            {
                data: 'amount',
                title: 'Amount',
                render: data => '$' + Number(data).toFixed(2)
            },
            {
                data: 'balanceAfter',
                title: 'Balance After',
                render: data => '$' + Number(data).toFixed(2)
            }
        ];

        if (type === 'account') {
            return [
                { data: 'userId', title: 'User ID' },
                { data: 'cardNumber', title: 'Card Number' },
                { data: 'name', title: 'Name' },
                { data: 'contactNumber', title: 'Contact' },
                { data: 'balance', title: 'Balance',
                    render: data => '$' + Number(data).toFixed(2) }
            ];
        } else if (type === 'transfer') {
            return [
                ...commonColumns,
                { data: 'description', title: 'Description' }
            ];
        }

        return commonColumns;
    }

    function generateReport() {
        const date = document.getElementById('reportDate').value;

        fetch(`${pageContext.request.contextPath}/admin/report?` +
            'type=' + currentReportType + '&date=' + date)
            .then(response => response.json())
            .then(data => {
                reportTable.clear();
                reportTable.rows.add(data);
                reportTable.draw();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error generating report')
            })
    }

    // Logout function
    function logout() {
        fetch('${pageContext.request.contextPath}/logout')
            .then(response => {
                if (response.ok) {
                    window.location.href = '${pageContext.request.contextPath}/';
                } else {
                    throw new Error('Logout failed');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error during logout');
            });
    }
</script>
</body>
</html>
