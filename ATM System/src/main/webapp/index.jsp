<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM System - Login</title>
    <!-- Google Font -->
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body class="centered-page">
<div class="login-container">
    <!-- Left Section: Image or Logo -->
    <div class="login-image">

    </div>

    <!-- Right Section: Login Options -->
    <div class="login-form">
        <h2>Welcome to the ATM System</h2>
        <p>Please select your login type below. </p>

        <div class="login-buttons">
            <a href="${pageContext.request.contextPath}/login/user" class="btn btn-primary">User Login</a>
            <a href="${pageContext.request.contextPath}/login/admin" class="btn btn-primary">Admin Login</a>
        </div>

<%--        <div class="note">--%>
<%--            <p><a href="${pageContext.request.contextPath}/help.jsp">Need help?</a> |--%>
<%--                <a href="${pageContext.request.contextPath}/about.jsp">About Us</a></p>--%>
<%--        </div>--%>
    </div>
</div>
</body>
</html>
