package org.atm.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.atm.dao.impl.AdminDAOImpl;
import org.atm.dao.impl.TransactionDAOImpl;
import org.atm.dao.impl.UserDAOImpl;
import org.atm.service.AdminService;
import org.atm.exception.ATMException;
import org.atm.service.impl.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminServlet extends BaseServlet {
    private AdminService adminService;
    private static final Logger logger = LoggerFactory.getLogger(AdminServlet.class);

    @Override
    public void init() throws ServletException {
        adminService = new AdminServiceImpl(new AdminDAOImpl(), new UserDAOImpl(), new TransactionDAOImpl());
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

        // Forward to dashboard page
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);

        if (session == null) {
            sendErrorResponse(response, "Not authenticated");
            return;
        }

        switch (pathInfo) {
            case "/createUser":
                this.handleCreateUser(request, response);
                break;
            case "/generateReport":
                this.handleGenerateReport(request, response);
                break;
            case "/auth":
                this.handleAuthentication(request, response);
                break;
            default:
                sendErrorResponse(response, "Invalid endpoint");
        }
    }

    private void handleCreateUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pin = request.getParameter("pin");
        String name = request.getParameter("name");
        String contactNumber = request.getParameter("contactNumber");
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");

        try {
            boolean created = adminService.createUser(pin, name, contactNumber, gender, address);
            if (created) {
                sendJsonResponse(response, "{\"success\": true, \"message\": \"User created successfully\"}");
            } else {
                sendErrorResponse(response, "Failed to create user");
            }
        } catch (ATMException e) {
            sendErrorResponse(response, e.getMessage());
        }
    }

    private void handleGenerateReport(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        String reportType = request.getParameter("type");
        String date = request.getParameter("date");

        try {
            switch (reportType) {
                case "withdrawal":
                    sendJsonResponse(response, new Gson().toJson(adminService.getWithdrawalReport(date)));
                    break;
                case "deposit":
                    sendJsonResponse(response, new Gson().toJson(adminService.getDepositReport(date)));
                    break;
                case "transfer":
                    sendJsonResponse(response, new Gson().toJson(adminService.getTransferReport(date)));
                    break;
                case "account":
                    sendJsonResponse(response, new Gson().toJson(adminService.getAccountReport()));
                    break;
                default:
                    sendErrorResponse(response, "Invalid report type");
            }
        } catch (ATMException e) {
            sendErrorResponse(response, e.getMessage());
        }
    }

        private void handleAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            if (adminService.authenticate(username, password)) {
                HttpSession session = request.getSession(true);
                session.setAttribute("adminId", username);
                sendJsonResponse(response, "{\"success\": true}");
            } else {
                sendErrorResponse(response, "Invalid credentials");
            }
        } catch (ATMException e) {
            sendErrorResponse(response, e.getMessage());
        }
    }
}