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
                    <div id="filter" class="report-controls">
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
        const contextPath = '/ATM-System';

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

            if (reportType === 'account') {
                document.getElementById("filter").style.display = "none"
            }

            // Destroy any existing DataTable instance
            if (reportTable) {
                reportTable.destroy();
            }

            // Initialize a new DataTable instance
            reportTable = $('#reportTable').DataTable({
                columns: columns,
                order: [[0, 'desc']],
                // pageLength: 10,
                responsive: true,
                dom: 't<"bottom">', // This shows only table and pagination
                searching: false,     // Removes the search box
                lengthChange: false  // Removes the entries per page dropdown
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
            // Validate inputs
            if (!reportType) {
                alert('Report type is not set');
                return;
            }

            let url = '${pageContext.request.contextPath}' + '/admin/report?type=' + reportType;

            // Add date parameter only for non-account reports
            if (reportType !== 'account') {
                const date = document.getElementById('reportDate').value;
                url += '&date=' + date;
            }

            // Make the API call
            fetch(url, {
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
</body>
</html>