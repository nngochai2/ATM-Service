<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM - Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="dashboard-container">
    <!-- Navbar -->
    <nav class="navbar">
        <h2>ATM System</h2>
        <button onclick="logout()" class="btn-logout">Logout</button>
    </nav>

    <!-- Main content area -->
    <div class="main-content">
        <!-- Balance section in a "card" style container -->
        <div class="card balance-section">
            <h3>Current Balance</h3>
            <div id="balanceAmount" class="balance-amount">$0.00</div>
        </div>

        <!-- Grid of action cards -->
        <div class="actions-grid">
            <div class="action-card" onclick="showTransactionForm('withdraw')">
                <h3>Withdraw</h3>
                <p>Withdraw cash from your account</p>
            </div>

            <div class="action-card" onclick="showTransactionForm('deposit')">
                <h3>Deposit</h3>
                <p>Deposit money into your account</p>
            </div>

            <div class="action-card" onclick="showTransactionForm('transfer')">
                <h3>Transfer</h3>
                <p>Transfer money to another account</p>
            </div>

            <div class="action-card" onclick="showChangePin()">
                <h3>Change PIN</h3>
                <p>Update your 4-digit PIN</p>
            </div>
        </div>

        <!-- Modal for transaction form -->
        <div id="transactionModal" class="modal">
            <div class="modal-content">
                <!-- Close button -->
                <span class="close">&times;</span>
                <h2 id="modalTitle">Transaction</h2>

                <form id="transactionForm">
                    <div class="form-group">
                        <label for="amount">Amount</label>
                        <input type="number" id="amount" name="amount" min="0" step="0.01" required>
                    </div>

                    <!-- Extra fields shown only for transfer -->
                    <div id="transferFields" style="display:none">
                        <div class="form-group">
                            <label for="toCard">To Card Number</label>
                            <!-- Pattern for exactly 10 digits -->
                            <input type="text" id="toCard" name="toCard" pattern="\d{10}">
                        </div>
                        <div class="form-group">
                            <label for="description">Description</label>
                            <input type="text" id="description" name="description">
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary">Submit</button>
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
    const balance = document.getElementById('balanceAmount');
    let currentHandler = null;

    // Fetch the user's balance on page load
    window.onload = function() {
        fetchBalance();
    };

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

    // Show the transaction modal and configure the form
    function showTransactionForm(type) {
        const amount = document.getElementById('amount').value;
        console.log(`Action:`, type, `Amount:`, amount)

        // Update the modal title based on the action
        modalTitle.textContent = capitalize(type);

        // Show transfer-specific fields only if needed
        transferFields.style.display = (type === 'transfer') ? 'block' : 'none';
        modal.style.display = 'block';

        // Remove any previous submit event handler
        if (currentHandler) {
            transactionForm.removeEventListener('submit', currentHandler);
        }

        // Create a new handler for this transaction type
        currentHandler = function(e) {
            e.preventDefault();
            handleTransaction(type);
        };
        transactionForm.addEventListener('submit', currentHandler);
    }

    function handleTransaction(type) {
        console.log('Action type: ', type);

        // Retrieve and validate the amount
        const amountInput = document.getElementById('amount');
        const amount = amountInput.value.trim();

        // Validate the amount
        if (!amount || isNaN(amount) || parseFloat(amount) <= 0) {
            alert('Please enter a valid amount')
            return;
        }

        // Initialize the data string
        let data = "action=" + type + "&amount=" + amount;

        // Additional data for transfer
        if (type === 'transfer') {
            const toCard = document.getElementById('toCard').value;
            const description = document.getElementById('description').value;

            if (!toCard || !description) {
                alert('Please fill in all transfer details');
                return;
            }
            data += "&toCard=" + toCard + "&description=" + description;
        }

        console.log('Sending data:', data);

        // Sending the request to the server
        fetch('${pageContext.request.contextPath}/user/transaction', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: data
        })
            .then(response => {
                console.log('Response status:', response.status);
                if (!response.ok) {
                    throw new Error("Server responded with status" + response.status);
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    fetchBalance(); // Refresh the balance
                    closeModal(); // Close the modal
                } else {
                    alert(data.message || 'Transaction failed');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred during the transaction');
            });
    }

    // Navigate to change PIN page
    function showChangePin() {
        window.location.href = `${pageContext.request.contextPath}/user/changePin`;
    }

    // Close the modal and reset the form
    function closeModal() {
        modal.style.display = 'none';
        transactionForm.reset();
    }

    // Close when user clicks the 'X'
    closeButton.onclick = closeModal;

    // Also close the modal if user clicks outside it
    window.onclick = function(event) {
        if (event.target === modal) {
            closeModal();
        }
    };

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

    // Helper to capitalize first letter of a word
    function capitalize(word) {
        return word.charAt(0).toUpperCase() + word.slice(1);
    }
</script>
</body>
</html>
