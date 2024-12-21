<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>ATM - Admin Login</title>
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
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" class="form-control" required title="Username">
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="pin" class="form-control" required>
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
    document.getElementById('loginForm').addEventListener("submit", function (event) {
        event.preventDefault();

        const username = document.getElementById('username');
        const password = document.getElementById('password');

        fetch(`${pageContext.request.contextPath}/admin/auth`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'username=' + encodeURIComponent(username) + '&password=' + encodeURIComponent(password)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok')
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    window.location.href = '${pageContext.request.contextPath}/admin/dashboard';
                } else {
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
