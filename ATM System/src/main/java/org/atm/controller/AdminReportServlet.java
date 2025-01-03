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
import org.atm.dto.TransactionReport;
import org.atm.exception.ATMException;
import org.atm.model.Transaction;
import org.atm.model.User;
import org.atm.service.AdminService;
import org.atm.service.impl.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/admin/report")
public class AdminReportServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminReportServlet.class);
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = new AdminServiceImpl(new AdminDAOImpl(), new UserDAOImpl(), new TransactionDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            sendErrorResponse(resp, "Not authenticated");
            return;
        }

        String type = req.getParameter("type");
        String date = req.getParameter("date");

        // Check if it's an AJAX request
        boolean isAjax = "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));

        if (isAjax) {
            // Handle AJAX request - return JSON
            if (type == null || type.isEmpty()) {
                sendErrorResponse(resp, "Report type is required");
                return;
            }

            try {
                if (type.equalsIgnoreCase("account")) {
                    List<User> accounts = adminService.getAccountReport();
                    Gson gson = new Gson();
                    sendJsonResponse(resp, gson.toJson(accounts));
                    return;
                }

                // For other reports, we need date
                if (date == null || date.isEmpty()) {
                    sendErrorResponse(resp, "Report date is required");
                    return;
                }

                Transaction.TransactionType transactionType = getTransactionType(type);
                List<TransactionReport> reports = adminService.getTransactionReport(date, transactionType);
                Gson gson = new Gson();
                sendJsonResponse(resp, gson.toJson(reports));
            } catch (Exception e) {
                logger.error("Error generating report", e);
                sendErrorResponse(resp, e.getMessage());
            }
        } else {
            // Handle page load - forward to JSP
            req.setAttribute("currentDate", LocalDate.now());
            req.getRequestDispatcher("/WEB-INF/views/admin/report.jsp")
                    .forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            sendErrorResponse(resp, "Not authenticated");
            return;
        }

        // Set response type BEFORE reading the request body
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Read JSON from request body
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            // Parse the JSON into User object
            Gson gson = new Gson();
            User newUser = gson.fromJson(buffer.toString(), User.class);

            // Create the user
            adminService.createUser(newUser);
            sendJsonResponse(resp, "{\"success\": true, \"message\": \"Customer created successfully\"}");

        } catch (Exception e) {
            logger.error("Error creating customer", e);
            sendErrorResponse(resp, "Error creating customer: " + e.getMessage());
        }
    }

    private Transaction.TransactionType getTransactionType(String type) {
        return switch (type.toLowerCase()) {
            case "withdraw" -> Transaction.TransactionType.WITHDRAW;
            case "deposit" -> Transaction.TransactionType.DEPOSIT;
            case "transfer" -> Transaction.TransactionType.TRANSFER;
            default -> throw new IllegalArgumentException("Invalid report type: " + type);
        };
    }
}

