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
        // Initialize UserService
        userService = new UserServiceImpl(new UserDAOImpl(), new TransactionDAOImpl());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long cardNumber = Long.valueOf(request.getParameter("card_number"));
        String pin = request.getParameter("pin");

        try {
            if (userService.authenticate(cardNumber, pin)) {
                HttpSession session = request.getSession();
                session.setAttribute("card_number", cardNumber);
                response.sendRedirect("dashboard.jsp");
            } else {
                request.setAttribute("error", "Invalid credentials");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (ATMException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
