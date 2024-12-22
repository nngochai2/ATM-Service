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

@WebServlet("/user/transaction")
public class TransactionServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServlet.class);
    private UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl(new UserDAOImpl(), new TransactionDAOImpl());
    }

    /**
     * Handle GET: Return the user's current balance as JSON
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("cardNumber") == null) {
            sendErrorResponse(response, "Not authenticated");
            return;
        }

        try {
            // Get cardNumber from session
            String cardNumberStr = (String) session.getAttribute("cardNumber");
            Long cardNumber = Long.valueOf(cardNumberStr);

            // Query balance
            double balance = userService.getBalance(cardNumber);
            sendJsonResponse(response, String.format("{\"balance\": %.2f", balance));
        } catch (ATMException e) {
            sendErrorResponse(response, e.getMessage());
        } catch (NumberFormatException e) {
            // IF cardNumber was not a valid Long
            logger.error("Invalid card number format in session", e);
            sendErrorResponse(response, "Invalid card number");
        } catch (Exception e) {
            // Any unexpected exceptions
            logger.error("Error fetching balance", e);
            sendErrorResponse(response, "Error fetching balance");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Log the incoming request for debugging
        String action = request.getParameter("action");
        String amountStr = request.getParameter("amount");

        logger.debug("Received action: {}, amount: {}", action, amountStr); // Debug log

        // Check session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("cardNumber") == null) {
            sendErrorResponse(response, "Not authenticated");
            return;
        }

        try {
            // Validate action
            if (action == null || action.isEmpty()) {
                logger.error("Action is missing or empty");
                sendErrorResponse(response, "Action is required");
                return;
            }

            // Validate amount
            if (amountStr == null || amountStr.isEmpty()) {
                logger.error("Amount is missing or empty");
                sendErrorResponse(response, "Amount is required");
                return;
            }

            // Get cardNumber from session as String and convert to Long
            String cardNumberStr = (String) session.getAttribute("cardNumber");
            Long cardNumber = Long.valueOf(cardNumberStr);
            logger.debug("Session cardNumber: {}", cardNumber);

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    sendErrorResponse(response, "Amount must be greater than zero");
                    return;
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(response, "Invalid amount format");
                return;
            }

            // Switch on the action
            switch (action) {
                case "withdraw":
                    userService.withdraw(cardNumber, amount);
                    sendJsonResponse(response, "{\"success\": true, \"message\": \"Withdrawal successful\"}");
                    break;

                case "deposit":
                    userService.deposit(cardNumber, amount);
                    sendJsonResponse(response, "{\"success\": true, \"message\": \"Deposit successful\"}");
                    break;

                case "transfer":
                    // Additional params
                    String toCardStr = request.getParameter("toCard");
                    if (toCardStr == null) {
                        sendErrorResponse(response, "Recipient card number is required");
                        return;
                    }
                    Long toCard = Long.valueOf(toCardStr);
                    String description = request.getParameter("description");

                    userService.transferMoney(cardNumber, toCard, amount, description);
                    sendJsonResponse(response, "{\"success\": true, \"message\": \"Transfer successful\"}");
                    break;

                default:
                    sendErrorResponse(response, "Invalid action");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid amount format in session", e);
            sendErrorResponse(response, "Invalid number format");
        } catch (ATMException e) {
            logger.error("Error fetching balance", e);
            sendErrorResponse(response, e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing transaction", e);
            sendErrorResponse(response, "An error occurred during the transaction");
        }
    }
}
