<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM System - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="login-container">
        <h2>Welcome to ATM System</h2>
        <form id="loginForm" action="${pageContext.request.contextPath}/user/auth" method="post">
            <div class="form-group">
                <label for="cardNumber">Card Number:</label>
                <input type="text" id="cardNumber" name="cardNumber" required>
            </div>
            <div class="form-group">
                <label for="pin">PIN:</label>
                <input type="password" id="pin" name="pin" maxlength="4" required>
            </div>
            <button type="submit" class="btn-primary">Login</button>
        </form>
        <div id="errorMessage" class="error-message"></div>
    </div>

    <script>
        document.getElementById('loginForm').addEventListener('submit', function(e) {
            e.preventDefault();

            fetch('${pageContext.request.contextPath}/user/auth', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    'cardNumber': document.getElementById('cardNumber').value,
                    'pin': document.getElementById('pin').value
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    window.location.href = '${pageContext.request.contextPath}/user/dashboard.jsp';
                } else {
                    document.getElementById('errorMessage').textContent = data.message;
                }
            })
            .catch(error => {
                document.getElementById('errorMessage').textContent = 'An error occurred. Please try again.';
            });
        });
    </script>
</body>
</html>