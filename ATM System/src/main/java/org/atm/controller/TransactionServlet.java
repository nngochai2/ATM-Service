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

import java.io.IOException;

@WebServlet("/user/transaction")
public class TransactionServlet extends BaseServlet {
    private UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl(new UserDAOImpl(), new TransactionDAOImpl());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("card_number") == null) {
            sendErrorResponse(response, "Not authenticated");
            return;
        }

        Long cardNumber = (Long) session.getAttribute("card_number");
        String action = request.getParameter("action");

        try {
            if (action == null) {
                sendErrorResponse(response, "Action is required");
                return;
            }

            double amount = Double.parseDouble(request.getParameter("amount"));

            switch (action) {
                case "deposit":
                    userService.deposit(cardNumber, amount);
                    sendJsonResponse(response, "{\"success\": true, \"message\": \"Deposit successful\"}");
                    break;

                case "withdraw":
                    userService.withdraw(cardNumber, amount);
                    sendJsonResponse(response, "{\"success\": true, \"message\": \"Deposit successful\"}");
                    break;

                case "transfer":
                    Long toCard = Long.valueOf(request.getParameter("to_card"));
                    String description = request.getParameter("description");

                    if (toCard.equals(cardNumber)) {
                        sendErrorResponse(response, "Cannot transfer to the same account");
                    }

                    if (userService.transferMoney(cardNumber, toCard, amount, description)) {
                        sendJsonResponse(response, "{\"success\": true, \"message\": \"Transfer successful\"}");
                        break;
                    }
                    return;

                default:
                    sendErrorResponse(response, "Invalid action.");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Invalid amount format");
        } catch (ATMException e) {
            sendErrorResponse(response,e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("card_number") == null) {
            sendErrorResponse(response, "Not authenticated");
            return;
        }

        Long cardNumber = (Long) session.getAttribute("cardNumber");
        try {
            double balance = userService.getBalance(cardNumber);
            sendJsonResponse(response, String.format("{\"balance\": %.2f", balance));
        } catch (ATMException e) {
            sendErrorResponse(response, e.getMessage());
        }
    }
}
