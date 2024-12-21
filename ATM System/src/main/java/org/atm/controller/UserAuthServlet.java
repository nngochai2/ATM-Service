package org.atm.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.atm.dao.impl.TransactionDAOImpl;
import org.atm.dao.impl.UserDAOImpl;
import org.atm.service.UserService;
import org.atm.exception.ATMException;
import org.atm.service.impl.UserServiceImpl;

import java.io.IOException;

@WebServlet("/user/auth")
public class UserAuthServlet extends BaseServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl(new UserDAOImpl(), new TransactionDAOImpl());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String cardNumber = request.getParameter("cardNumber");
        String pin = request.getParameter("pin");

        // Validate input
        if (cardNumber == null || pin == null || cardNumber.trim().isEmpty() || pin.trim().isEmpty()) {
            // Use from BaseServlet
            sendJsonResponse(response, "{\"success\": false, \"message\": \"Card number and PIN are required\"}");
            return;
        }

        try {
            Long cardNumberLong = Long.valueOf(cardNumber);
            if (userService.authenticate(cardNumberLong, pin)) {
                HttpSession session = request.getSession();
                session.setAttribute("cardNumber", cardNumber);

                // Return JSON response
                sendJsonResponse(response, "{\"success\": true, \"message\": \"Login successful\"}");
            } else {
                sendJsonResponse(response, "{\"success\": false, \"message\": \"Invalid credentials\"}");
            }
        } catch (NumberFormatException e) {
            sendJsonResponse(response, "{\"success\": false, \"message\": \"Invalid card number format\"}");
        } catch (ATMException e) {
            sendJsonResponse(response, "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}
