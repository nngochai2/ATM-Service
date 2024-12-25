package org.atm.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.atm.dao.impl.AdminDAOImpl;
import org.atm.dao.impl.TransactionDAOImpl;
import org.atm.dao.impl.UserDAOImpl;
import org.atm.service.AdminService;
import org.atm.service.impl.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;


@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardServlet.class);

    @Override
    public void init() throws ServletException {
        AdminService adminService = new AdminServiceImpl(new AdminDAOImpl(), new UserDAOImpl(), new TransactionDAOImpl());
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        logger.info("Accessing Dashboard. Session {}", session != null);

        // Check if user is logged in
        if (session == null) {
            logger.warn("Unauthorized access");
            resp.sendRedirect(req.getContextPath() + "/admin/auth");
            return;
        }

        req.setAttribute("currentDate", LocalDate.now());

        // Forward to dashboard page
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp")
                .forward(req, resp);
    }
}