package org.atm.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.atm.dao.impl.AdminDAOImpl;
import org.atm.dao.impl.TransactionDAOImpl;
import org.atm.dao.impl.UserDAOImpl;
import org.atm.exception.ATMException;
import org.atm.service.AdminService;
import org.atm.service.impl.AdminServiceImpl;

import java.io.IOException;

@WebServlet("/admin/auth")
public class AdminAuthServlet extends BaseServlet {
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = new AdminServiceImpl(new AdminDAOImpl(), new UserDAOImpl(), new TransactionDAOImpl());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Validate input
        if (username == null || password == null) {
            sendJsonResponse(resp, "{\"success\": false, \"message\": \"Username and password are required\"}");
            return;
        }

        try {
            if (adminService.authenticate(username, password)) {
                HttpSession session = req.getSession();
                session.setAttribute("username", username);
                sendJsonResponse(resp, "{\"success\": true, \"message\": \"Successfully authenticated\"}");
            } else {
                sendJsonResponse(resp, "{\"success\": false, \"message\": \"Invalid username or password\"}");
            }
        } catch (ATMException e) {
            throw new RuntimeException(e);
        }
    }
}
