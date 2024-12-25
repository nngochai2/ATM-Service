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
import org.atm.model.Transaction;
import org.atm.service.AdminService;
import org.atm.service.impl.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            if (date == null || date.isEmpty()) {
                sendErrorResponse(resp, "Report date is required");
                return;
            }

            try {
                Transaction.TransactionType transactionType = getTransactionType(type);
                List<TransactionReport> reports = adminService.getTransactionReport(date, transactionType);
                Gson gson = new Gson();
                sendJsonResponse(resp, gson.toJson(reports));
            } catch (Exception e) {
                sendErrorResponse(resp, e.getMessage());
            }
        } else {
            // Handle page load - forward to JSP
            req.setAttribute("currentDate", LocalDate.now());
            req.getRequestDispatcher("/WEB-INF/views/admin/report.jsp")
                    .forward(req, resp);
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

