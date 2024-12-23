<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM - Change PIN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600&display=swap" rel="stylesheet">
</head>

<body class="centered-page">

<div class="login-container"><!-- or .landing-container if you prefer -->
    <div style="display: flex; margin: auto">
        <h2 class="">Change PIN</h2>
        <p>
            Please enter your current PIN and the new PIN you wish to set.
        </p>
    </div>

    <!-- PIN Change Form -->
    <form id="changePinForm">
        <div class="form-group">
            <label for="oldPin">Old PIN</label>
            <input type="password"
                   id="oldPin"
                   name="oldPin"
                   class="form-control"
                   required
                   pattern="^[0-9]{4}$"
                   title="PIN must be exactly 4 digits"
                   maxlength="4"
                   placeholder="Current 4-digit PIN">
        </div>

        <div class="form-group">
            <label for="newPin">New PIN</label>
            <input type="password"
                   id="newPin"
                   name="newPin"
                   class="form-control"
                   required
                   pattern="^[0-9]{4}$"
                   title="PIN must be exactly 4 digits"
                   maxlength="4"
                   placeholder="New 4-digit PIN">
        </div>

        <div class="form-group">
            <label for="confirmPin">Confirm New PIN</label>
            <input type="password"
                   id="confirmPin"
                   name="confirmPin"
                   class="form-control"
                   required
                   pattern="^[0-9]{4}$"
                   title="PIN must be exactly 4 digits"
                   maxlength="4"
                   placeholder="Re-enter new 4-digit PIN">
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Change PIN</button>
            <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-secondary">
                Cancel
            </a>
        </div>
    </form>

    <div id="errorMessage" class="error-message"></div>
</div>

<script>
    document.getElementById('changePinForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const oldPin = document.getElementById('oldPin').value;
        const newPin = document.getElementById('newPin').value;
        const confirmPin = document.getElementById('confirmPin').value;

        if (newPin !== confirmPin) {
            document.getElementById('errorMessage').textContent = 'New PINs do not match';
            return;
        }

        fetch(pageContext.request.contextPath + '/user/changePin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'oldPin=' + oldPin + '&newPin=' + newPin
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('PIN changed successfully. Please login again.');
                    window.location.href = pageContext.request.contextPath + '/user/auth';
                } else {
                    document.getElementById('errorMessage').textContent = data.message;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('errorMessage').textContent = 'An error occurred while changing PIN';
            });
    });
</script>
</body>
</html>
