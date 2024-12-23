<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM - User Login</title>\
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="centered-page">

<!-- Container with glass morphism effect and two columns -->
<div class="login-container">
    <!-- Left Column: Image -->
    <div class="login-image">
        <!-- By default, uses background image from style.css -->
    </div>
    <!-- Right Column: Form -->
    <div class="login-form">
        <h2>User Login</h2>
        <p>Please enter your card number and 4-digit PIN.</p>

        <%-- User fills out the login form on the page--%>
        <form id="loginForm">
            <div class="form-group">
                <label for="cardNumber">Card Number</label>
                <!-- Pattern ensures exactly 10 digits -->
                <input type="text"
                       id="cardNumber"
                       name="cardNumber"
                       class="form-control"
                       required
                       pattern="^[0-9]{10}$"
                       title="Card number must be exactly 10 digits (e.g., 1234567890)"
                       placeholder="Enter your 10-digit card number">
            </div>

            <div class="form-group">
                <label for="pin">PIN</label>
                <!-- Pattern ensures exactly 4 digits -->
                <input type="password"
                       id="pin"
                       name="pin"
                       class="form-control"
                       required
                       pattern="^[0-9]{4}$"
                       title="PIN must be exactly 4 digits"
                       maxlength="4"
                       placeholder="4-digit PIN">
            </div>

            <div class="form-actions">
                <!-- Primary button triggers AJAX login -->
                <button type="submit" class="btn btn-primary">Login</button>
                <!-- Secondary link returns to home/landing page -->
                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Back to Home</a>
            </div>
        </form>

        <!-- Error display -->
        <div id="errorMessage" class="error-message"></div>
    </div>
</div>

<!-- AJAX login logic -->
<script>
    document.getElementById('loginForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const formData = new FormData(this);
        const urlEncodedData = new URLSearchParams(formData).toString();

        // Sends a POST request to the servlet URL
        fetch(`${pageContext.request.contextPath}/user/auth`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded' // Encode format
            },
            body: urlEncodedData
        })
            .then(response => response.json())
            // Processes the Server's response
            .then(data => {
                if (data.success) {
                    // On success, redirect to user dashboard
                    window.location.href = '${pageContext.request.contextPath}/user/dashboard';
                } else {
                    // Show server error message
                    document.getElementById('errorMessage').textContent = data.message;
                }
            })
            .catch(error => {
                document.getElementById('errorMessage').textContent = 'An error occurred';
                console.error('Error:', error);
            });
    });
</script>

</body>
</html>
