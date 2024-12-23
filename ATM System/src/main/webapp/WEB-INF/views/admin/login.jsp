<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>ATM - Admin Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="centered-page">
    <div class="login-container">
        <!-- Left Column: Image -->
        <div class="login-image">
            <!-- By default, uses background image from style.css -->
        </div>
        <!-- Right Column: Form -->
        <div class="login-form">
            <h2>Admin Login</h2>
            <p>Please enter your username and password.</p>

            <%-- User fills out the login form on the page--%>
            <form id="loginForm">
                <div class="form-group">
                    <label for="username">Username</label>
                    <!-- Pattern ensures exactly 10 digits -->
                    <input type="text"
                           id="username"
                           name="username"
                           class="form-control"
                           required
                           placeholder="Enter your username">
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <!-- Pattern ensures exactly 4 digits -->
                    <input type="password"
                           id="password"
                           name="password"
                           class="form-control"
                           required
                           placeholder="Enter your password">
                </div>

                <div class="form-actions">
                    <!-- Primary button triggers AJAX login -->
                    <button type="submit" class="btn btn-primary">Login</button>
                    <!-- Secondary link returns to home/landing page -->
                    <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Back</a>
                </div>
            </form>

            <!-- Error display -->
            <div id="errorMessage" class="error-message"></div>
        </div>
    </div>

    <script>
        document.getElementById('loginForm').addEventListener("submit", function (event) {
            event.preventDefault();

            const formData = new FormData(this);
            const urlEncodedData = new URLSearchParams(formData).toString();

            // Sends a POST request to the servlet URL
            fetch(`${pageContext.request.contextPath}/admin/auth`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded' // Encoded format
                },
                body: urlEncodedData
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // On success, redirect to admin dashboard
                        window.location.href = `${pageContext.request.contextPath}/admin/dashboard`;
                    } else {
                        // Show server error message
                        document.getElementById('errorMessage').textContent = data.message;
                    }
                })
                .catch(error => {
                    document.getElementById('errorMessage').textContent = 'An error occurred. Please try again.';
                    console.error('Error:', error);
                });
        });
    </script>

</body>
</html>
