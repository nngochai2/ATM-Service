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
    const modal = document.getElementById('transactionModal');
    const modalTitle = document.getElementById('modalTitle');
    const transferFields = document.getElementById('transferFields');
    const transactionForm = document.getElementById('transactionForm');
    const closeButton = document.querySelector('.close');
    let currentHandler = null;

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
            .catch(error => {
                console.error('Error:', error);
                alert('Error fetching balance');
            });
    }

    function showTransactionForm(type) {
        modalTitle.textContent = type.charAt(0).toUpperCase() + type.slice(1);
        transferFields.style.display = type === 'transfer' ? 'block' : 'none';
        modal.style.display = 'block';

        // Remove previous handler if exists
        if (currentHandler) {
            transactionForm.removeEventListener('submit', currentHandler);
        }

        // Create new handler
        currentHandler = function (e) {
            e.preventDefault();
            handleTransaction(type);
        }

        // Add new event listener
        transactionForm.addEventListener('submit', currentHandler);
    }

    function handleTransaction(type) {
        const amount = document.getElementById('amount').value;
        let data = `action=${type}&amount=${amount}`;

        if (type === 'transfer') {
            const toCard = document.getElementById('toCard').value;
            const description = document.getElementById('description').value;

            if (!toCard || !description) {
                alert('Please fill in all transfer details');
                return;
            }
            data += `&toCard=${toCard}&description=${description}`;
        }

        fetch('${pageContext.request.contextPath}/user/transaction', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: data
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    fetchBalance();
                    closeModal();
                } else {
                    alert(data.message || 'Transaction failed');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred during the transaction');
            });
    }

    function showChangePin() {
        window.location.href = '${pageContext.request.contextPath}/user/changePin';
    }

    function closeModal() {
        modal.style.display = 'none';
        transactionForm.reset();
    }

    // Event Listeners
    closeButton.onclick = closeModal;

    // Close modal when clicking outside
    window.onclick = function(event) {
        if (event.target === modal) {
            closeModal();
        }
    }

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