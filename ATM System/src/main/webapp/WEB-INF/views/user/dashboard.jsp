<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM - Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="dashboard-container">
    <nav class="navbar">
        <h2>Welcome to ATM</h2>
        <button onclick="logout()" class="btn-logout">Logout</button>
    </nav>

    <div class="main-content">
        <div class="balance-section">
            <h3>Current Balance</h3>
            <div id="balanceAmount" class="balance-amount">$0.00</div>
        </div>

        <div class="actions-grid">
            <div class="action-card" onclick="showTransactionForm('withdraw')">
                <h3>Withdraw</h3>
                <p>Withdraw cash from your account</p>
            </div>

            <div class="action-card" onclick="showTransactionForm('deposit')">
                <h3>Deposit</h3>
                <p>Deposit money to your account</p>
            </div>

            <div class="action-card" onclick="showTransactionForm('transfer')">
                <h3>Transfer</h3>
                <p>Transfer money to another account</p>
            </div>

            <div class="action-card" onclick="showChangePin()">
                <h3>Change PIN</h3>
                <p>Update your PIN</p>
            </div>
        </div>

        <!-- Transaction Form Modal -->
        <div id="transactionModal" class="modal">
            <div class="modal-content">
                <span class="close">&times;</span>
                <h2 id="modalTitle">Transaction</h2>
                <form id="transactionForm">
                    <div class="form-group">
                        <label for="amount">Amount</label>
                        <input type="number" id="amount" name="amount" min="0" step="0.01" required>
                    </div>
                    <div id="transferFields" style="display:none">
                        <div class="form-group">
                            <label for="toCard">To Card Number</label>
                            <input type="text" id="toCard" name="toCard" pattern="\d{10}">
                        </div>
                        <div class="form-group">
                            <label for="description">Description</label>
                            <input type="text" id="description" name="description">
                        </div>
                    </div>
                    <button type="submit" class="btn-primary">Submit</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    // Load balance on page load
    window.onload = function() {
        fetchBalance();
    }

    function fetchBalance() {
        fetch('${pageContext.request.contextPath}/user/balance')
            .then(response => response.json())
            .then(data => {
                document.getElementById('balanceAmount').textContent =
                    '$' + Number(data.balance).toFixed(2);
            })
            .catch(error => console.error('Error:', error));
    }

    function showTransactionForm(type) {
        const modal = document.getElementById('transactionModal');
        const title = document.getElementById('modalTitle');
        const transferFields = document.getElementById('transferFields');

        title.textContent = type.charAt(0).toUpperCase() + type.slice(1);
        transferFields.style.display = type === 'transfer' ? 'block' : 'none';
        modal.style.display = 'block';

        // Update form submission handling
        document.getElementById('transactionForm').onsubmit = function(e) {
            e.preventDefault();
            handleTransaction(type);
        }
    }

    function handleTransaction(type) {
        const amount = document.getElementById('amount').value;
        let data = `amount=${amount}`;

        if (type === 'transfer') {
            const toCard = document.getElementById('toCard').value;
            const description = document.getElementById('description').value;
            data += `&toCard=${toCard}&description=${description}`;
        }

        fetch(`${pageContext.request.contextPath}/user/transaction`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `action=${type}&${data}`
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    fetchBalance();
                    closeModal();
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred');
            });
    }

    function closeModal() {
        document.getElementById('transactionModal').style.display = 'none';
        document.getElementById('transactionForm').reset();
    }

    document.querySelector('.close').onclick = closeModal;

    function logout() {
        fetch('${pageContext.request.contextPath}/logout')
            .then(() => {
                window.location.href = '${pageContext.request.contextPath}/';
            });
    }
</script>
</body>
</html>