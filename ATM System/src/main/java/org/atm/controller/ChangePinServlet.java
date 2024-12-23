package org.atm.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.atm.dao.impl.TransactionDAOImpl;
import org.atm.dao.impl.UserDAOImpl;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward or redirect to changePin.jsp
        request.getRequestDispatcher("/WEB-INF/views/user/changePin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("Received PIN change request"); // Add logging

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("cardNumber") == null) {
            sendErrorResponse(response, "Not authenticated");
            return;
        }

        try {
            String cardNumberStr = (String) session.getAttribute("cardNumber");
            Long cardNumber = Long.valueOf(cardNumberStr);
            String oldPin = request.getParameter("oldPin");
            String newPin = request.getParameter("newPin");

            logger.debug("Received PIN change request - Card: {}, Old PIN length: {}, New PIN length: {}",
                    cardNumber,
                    oldPin != null ? oldPin.length() : 0,
                    newPin != null ? newPin.length() : 0);

            if (oldPin == null || newPin == null ||
                    !oldPin.matches("\\d{4}") || !newPin.matches("\\d{4}")) {
                sendErrorResponse(response, "PIN must be 4 digits");
                return;
            }

            if (userService.changePin(cardNumber, oldPin, newPin)) {
                session.invalidate();
                sendJsonResponse(response, "{\"success\": true, \"message\": \"PIN changed successfully\"}");
            } else {
                sendErrorResponse(response, "Failed to change PIN");
            }
        } catch (Exception e) {
            logger.error("Error changing PIN", e);
            sendErrorResponse(response, "Error changing PIN");
        }
    }
}

