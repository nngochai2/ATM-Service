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

            <!-- Create Customer Modal -->
            <div id="createCustomerModal" class="modal">
                <div class="modal-content">
                    <span class="close" onclick="closeCreateCustomerModal()">&times;</span>
                    <h2>Create New Customer</h2>

                    <form id="createCustomerForm" onsubmit="handleCreateCustomer(event)">
                        <div class="form-group">
                            <label for="name">Full Name: *</label>
                            <input type="text" id="name" name="name" required>
                        </div>

                        <div class="form-group">
                            <label for="pin">PIN: *</label>
                            <input type="password" id="pin" name="pin" pattern="\d{4}"
                                   title="PIN must be 4 digits" required>
                            <small>4-digit number</small>
                        </div>

                        <div class="form-group">
                            <label for="gender">Gender: *</label>
                            <select id="gender" name="gender" required>
                                <option value="">Select gender</option>
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                                <option value="Other">Other</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="address">Address: *</label>
                            <textarea id="address" name="address" rows="3" required></textarea>
                        </div>

                        <div class="form-group">
                            <label for="contactNumber">Contact Number: *</label>
                            <input type="tel" id="contactNumber" name="contactNumber" required>
                        </div>

                        <button type="submit" class="btn btn-primary">Create Customer</button>
                    </form>
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
        const modal = document.getElementById('createCustomerModal');

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

            // Show create customer button only for account report
            if (reportType === 'account') {
                const reportHeader = document.querySelector('.report-header');
                const backButton = document.querySelector('.btn-secondary');

                // Create and insert the button before the back button
                const createButton = document.createElement('button');
                createButton.onclick = showCreateCustomerModal;
                createButton.className = 'btn btn-primary';
                createButton.style.marginRight = '10px';  // Add some spacing
                createButton.textContent = 'Create Customer';

                backButton.parentNode.insertBefore(createButton, backButton);
            }

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
                { data: 'balanceAfter', title: 'Balance After', render: data => '$' + Number(data).toFixed(2) },
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
                        closeCreateCustomerModal()
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error generating report: ' + error.message);
                });
        }

        function showCreateCustomerModal() {
            document.getElementById('createCustomerModal').style.display = 'block';
        }

        function closeCreateCustomerModal() {
            document.getElementById('createCustomerModal').style.display = 'none';
            document.getElementById('createCustomerForm').reset();
        }

        function handleCreateCustomer(event) {
            event.preventDefault();

            const formData = {
                name: document.getElementById('name').value.trim(),
                pin: document.getElementById('pin').value.trim(),
                gender: document.getElementById('gender').value,
                address: document.getElementById('address').value.trim(),
                contactNumber: document.getElementById('contactNumber').value.trim()
            };

            // Basic validation
            if (!formData.pin.match(/^\d{4}$/)) {
                alert('PIN must be exactly 4 digits');
                return;
            }

            fetch(contextPath + '/admin/report', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: JSON.stringify(formData)
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Customer created successfully');
                        closeCreateCustomerModal();
                        generateReport(); // Refresh the table
                    } else {
                        alert(data.message || 'Failed to create customer');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error creating customer: ' + error.message);
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

        // Add window click event to close modal
        window.onclick = function(event) {
            const modal = document.getElementById('createCustomerModal');
            if (event.target === modal) {
                closeCreateCustomerModal();
            }
        }
    </script>
</body>
</html>