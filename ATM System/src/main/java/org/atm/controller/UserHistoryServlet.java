package org.atm.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.atm.dao.impl.TransactionDAOImpl;
import org.atm.model.Transaction;
import org.atm.service.TransactionService;
import org.atm.service.impl.TransactionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet("/user/history")
public class UserHistoryServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserHistoryServlet.class);
    private TransactionService transactionService;

    @Override
    public void init() throws ServletException {
        transactionService = new TransactionServiceImpl(new TransactionDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            sendErrorResponse(resp, "Not authenticated");
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String cardNumber = (String) session.getAttribute("cardNumber");

        // Handle AJAX request for transaction data
        if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
            try {
                if (cardNumber == null) {
                    sendErrorResponse(resp, "Card number not set");
                    return;
                }
                List<Transaction> transactions = transactionService.getTransactionHistory(Long.parseLong(cardNumber));
                Gson gson = new Gson();
                sendJsonResponse(resp, gson.toJson(transactions));
            } catch (Exception e) {
                logger.error("Error fetching transactions", e);
                sendErrorResponse(resp, "Failed to fetch transactions");
            }
            return;
        }

        // Forward to JSP for normal page load
        req.getRequestDispatcher("/WEB-INF/views/user/history.jsp").forward(req, resp);
    }
}
