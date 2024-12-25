<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM - Transaction History</title>
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
        <div class="card">
            <div class="card-header">
                <div class="report-header">
                    <h2>Recent Transactions</h2>
                    <button onclick="backToDashboard()" class="btn-secondary">Back to Dashboard</button>
                </div>
            </div>

            <div class="card-body">
                <!-- Balance Display -->
                <div class="balance-section">
                    <h3>Current Balance</h3>
                    <div id="balanceAmount" class="balance-amount">$0.00</div>
                </div>

                <!-- Transactions Table -->
                <div class="table-responsive">
                    <table id="transactionTable" class="report-table">
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
    let transactionTable;

    document.addEventListener('DOMContentLoaded', function() {
        fetchBalance();
        initializeTable();
    });

    function initializeTable() {
        transactionTable = $('#transactionTable').DataTable({
            columns: [
                { data: 'transactionDate', title: 'Date/Time' },
                { data: 'type', title: 'Type' },
                {
                    data: 'amount',
                    title: 'Amount',
                    render: function(data, type, row) {
                        const amount = Number(data).toFixed(2);
                        const prefix = row.type === 'WITHDRAW' ? '-' : '+';
                        const colorClass = row.type === 'WITHDRAW' ? 'text-red' : 'text-green';
                        return "<span class='" + colorClass + "'>" + prefix + "$" + amount + "</span>";
                    }
                },
                { data: 'description', title: 'Description' },
                {
                    data: 'balanceAfter',
                    title: 'Balance After',
                    render: data => '$' + Number(data).toFixed(2)
                }
            ],
            order: [[0, 'desc']],
            responsive: true,
            dom: 't', // Only show table
            searching: false,
            lengthChange: false,
            paging: false
        });

        fetchTransactions();
    }

    function fetchBalance() {
        fetch('${pageContext.request.contextPath}/user/balance')
            .then(response => response.json())
            .then(data => {
                document.getElementById('balanceAmount').textContent =
                    '$' + Number(data.balance).toFixed(2);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error fetching balance');
            });
    }

    function fetchTransactions() {
        fetch('${pageContext.request.contextPath}/user/history', {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) throw new Error('Failed to fetch transactions');
                return response.json();
            })
            .then(data => {
                transactionTable.clear();
                transactionTable.rows.add(data);
                transactionTable.draw();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error fetching transactions: ' + error.message);
            });
    }

    function backToDashboard() {
        window.location.href = '${pageContext.request.contextPath}/user/dashboard';
    }

    function logout() {
        fetch('${pageContext.request.contextPath}/logout')
            .then(() => {
                window.location.href = '${pageContext.request.contextPath}/';
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error during logout');
            });
    }
</script>
</body>
</html>