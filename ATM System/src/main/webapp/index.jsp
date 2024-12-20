<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome to ATM System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="landing-container">
    <h1>Welcome to ATM System</h1>
    <div class="button-group">
        <a href="${pageContext.request.contextPath}/user/login.jsp" class="btn-primary">User Login</a>
        <a href="${pageContext.request.contextPath}/admin/login.jsp" class="btn-primary">Admin Login</a>
    </div>
</div>
</body>
</html>