<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>ATM - Change PIN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600&display=swap" rel="stylesheet">
</head>

<body class="centered-page">

<div class="form-group"><!-- or .landing-container if you prefer -->
    <div style="display: flex; margin: auto">
        <h2 class="card-header">Change PIN</h2>
        <p class="note" style="display: flex; text-align: start; padding-left: 10px">
            Please enter your current PIN and the new PIN you wish to set.
        </p>
    </div>

    <!-- PIN Change Form -->
    <form id="changePinForm" style="padding-top: 20px">
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

        // Add validation
        if (newPin !== confirmPin) {
            alert('New PINs do not match');
            return;
        }

        const formData = new URLSearchParams();
        formData.append('oldPin', oldPin);
        formData.append('newPin', newPin);

        console.log('Sending data:', formData.toString()); // Debug log

        fetch('${pageContext.request.contextPath}/user/changePin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData.toString()
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('PIN changed successfully. Please login again.');
                    window.location.href = '${pageContext.request.contextPath}/user/auth';
                } else {
                    alert(data.message || 'Failed to change PIN');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error changing PIN');
            });
    });
</script>
</body>
</html>
