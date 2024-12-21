<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM - User Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="login-container">
    <div class="card">
        <div class="card-header">
            <h2>ATM Login</h2>
        </div>
        <div class="card-body">
            <form id="loginForm">
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
    document.getElementById('loginForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const formData = new FormData(this);
        const urlEncodedData = new URLSearchParams(formData).toString();

        fetch(`${pageContext.request.contextPath}/user/auth`, {
            method: `POST`,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: urlEncodedData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    window.location.href = '${pageContext.request.contextPath}/user/dashboard';
                } else {
                    document.getElementById('errorMessage').textContent = data.message
                }
            })
            .catch(error => {
                document.getElementById('errorMessage').textContent = 'An error occurred';
                console.log('Error:', error);
            })
    })
</script>
</body>
</html>