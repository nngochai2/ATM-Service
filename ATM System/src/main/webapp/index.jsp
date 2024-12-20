<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome to ATM System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="landing-container">
    <div class="card">
        <div class="card-header">
            <h1>Welcome to ATM System</h1>
        </div>
        <div class="card-body">
            <div class="button-group">
                <a href="${pageContext.request.contextPath}/login/user" class="btn-primary">User Login</a>
                <a href="${pageContext.request.contextPath}/login/admin" class="btn-primary">Admin Login</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>