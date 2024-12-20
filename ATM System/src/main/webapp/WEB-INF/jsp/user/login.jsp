<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM - User Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="login-container">
    <div class="card">
        <div class="card-header">
            <h2>ATM Login</h2>
        </div>
        <div class="card-body">
            <form id="loginForm" onsubmit="return handleLogin(event)">
                <div class="form-group">
                    <label for="cardNumber">Card Number</label>
                    <input type="text" id="cardNumber" name="cardNumber" class="form-control" required
                           pattern="\d{10}" title="Card number must be 10 digits">
                </div>

                <div class="form-group">
                    <label for="pin">PIN</label>
                    <input type="password" id="pin" name="pin" class="form-control" required
                           pattern="\d{4}" title="PIN must be 4 digits" maxlength="4">
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-primary">Login</button>
                    <a href="${pageContext.request.contextPath}/" class="btn-secondary">Back to Home</a>
                </div>
            </form>
            <div id="errorMessage" class="error-message"></div>
        </div>
    </div>
</div>

<script>
    function handleLogin(event) {
        event.preventDefault();

        const cardNumber = document.getElementById('cardNumber').value;
        const pin = document.getElementById('pin').value;
        const errorMessage = document.getElementById('errorMessage');

        fetch('${pageContext.request.contextPath}/user/auth', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `cardNumber=${cardNumber}&pin=${pin}`
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    window.location.href = '${pageContext.request.contextPath}/user/dashboard';
                } else {
                    errorMessage.textContent = data.message || 'Login failed. Please check your credentials.';
                }
            })
            .catch(error => {
                errorMessage.textContent = 'An error occurred. Please try again.';
                console.error('Error:', error);
            });

        return false;
    }
</script>
</body>
</html>