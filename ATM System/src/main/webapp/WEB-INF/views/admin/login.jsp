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

</body>
</html>
