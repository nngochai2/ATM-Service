package org.atm.controller;

import com.google.gson.Gson;
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
import java.util.Collections;

@WebServlet("/user/balance")
public class BalanceServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(BalanceServlet.class);
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl(new UserDAOImpl(), new TransactionDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("cardNumber") == null) {
            sendErrorResponse(response, "Not authenticated");
            return;
        }

        try {
            Long cardNumber = Long.valueOf((String) session.getAttribute("cardNumber"));
            double balance = userService.getBalance(cardNumber);
            sendJsonResponse(response, new Gson().toJson(Collections.singletonMap("balance", balance)));
        } catch (ATMException e) {
            logger.error("Error retrieving balance", e);
            sendErrorResponse(response, e.getMessage());
        }
    }
}
