package org.atm.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.atm.dao.impl.TransactionDAOImpl;
import org.atm.dao.impl.UserDAOImpl;
import org.atm.exception.ATMException;
import org.atm.service.UserService;
import org.atm.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/user/changePin")
public class ChangePinServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(ChangePinServlet.class);
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl(new UserDAOImpl(), new TransactionDAOImpl());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("cardNumber") == null) {
            sendErrorResponse(response, "Not authenticated");
            return;
        }

        Long cardNumber = Long.valueOf((String) session.getAttribute("cardNumber"));
        String oldPin = request.getParameter("oldPin");
        String newPin = request.getParameter("newPin");
        String confirmPin = request.getParameter("confirmPin");

        try {
            if (oldPin == null || newPin == null || confirmPin == null) {
                sendErrorResponse(response, "All PIN fields are required");
                return;
            }

            if (!newPin.equals(confirmPin)) {
                sendErrorResponse(response, "New PINs do not match");
                return;
            }

            if (!newPin.matches("\\d{4}")) {
                sendErrorResponse(response, "PIN must be exactly 4 digits");
                return;
            }

            if (userService.changePin(cardNumber, oldPin, newPin)) {
                session.invalidate(); // Force re-login with new PIN
                sendJsonResponse(response, "{\"success\": true, \"message\": \"PIN changed successfully. Please login again.\"}");
            } else {
                sendErrorResponse(response, "Failed to change PIN");
            }
        } catch (ATMException e) {
            logger.error("Error changing PIN for card: {}", cardNumber, e);
            sendErrorResponse(response, e.getMessage());
        }
    }
}

