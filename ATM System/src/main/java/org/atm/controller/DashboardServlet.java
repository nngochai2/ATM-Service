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

@WebServlet("/user/dashboard")
public class DashboardServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(DashboardServlet.class);

    @Override
    public void init() throws ServletException {
        UserService userService = new UserServiceImpl(new UserDAOImpl(), new TransactionDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        logger.info("Accessing Dashboard. Session {}", session != null);

        // Check if user is logged in
        if (session == null || session.getAttribute("cardNumber") == null) {
            logger.warn("Unauthorized access");
            resp.sendRedirect(req.getContextPath() + "/user/auth");
            return;
        }

        // Forward to dashboard page
        req.getRequestDispatcher("/WEB-INF/views/user/dashboard.jsp")
                .forward(req, resp);
    }
}
