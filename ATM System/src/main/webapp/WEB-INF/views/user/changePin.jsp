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
    <h2 style="text-align:center; margin-bottom:1rem;">Change PIN</h2>

    <!-- Instruction or short text -->
    <p style="text-align:center; margin-bottom:2rem;">
        Please enter your current PIN and the new PIN you wish to set.
    </p>

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
    document.getElementById('changePinForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const oldPin = document.getElementById('oldPin').value;
        const newPin = document.getElementById('newPin').value;
        const confirmPin = document.getElementById('confirmPin').value;

        // Basic client-side check for matching newPin & confirmPin
        if (newPin !== confirmPin) {
            document.getElementById('errorMessage').textContent = 'New PINs do not match!';
            return;
        }

        // Build URL-encoded data
        const data = new URLSearchParams();
        data.append('oldPin', oldPin);
        data.append('newPin', newPin);

        fetch('${pageContext.request.contextPath}/user/changePin', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: data.toString()
        })
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    alert('PIN changed successfully. You will be logged out now.');
                    window.location.href = '${pageContext.request.contextPath}/logout';
                } else {
                    document.getElementById('errorMessage').textContent = result.message || 'Error changing PIN';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('errorMessage').textContent = 'An error occurred. Please try again.';
            });
    });
</script>
</body>
</html>
