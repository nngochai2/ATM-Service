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
        if (type == null || type.isEmpty() || date == null || date.isEmpty()) {
            sendErrorResponse(resp, "Both report type and date are required");
            return;
        }

        try {
            Transaction.TransactionType transactionType;
            switch (type.toLowerCase()) {
                case "withdraw":
                    transactionType = Transaction.TransactionType.WITHDRAW;
                    break;
                case "deposit":
                    transactionType = Transaction.TransactionType.DEPOSIT;
                    break;
                case "transfer":
                    transactionType = Transaction.TransactionType.TRANSFER;
                    break;
                default:
                    sendErrorResponse(resp, "Invalid report type");
                    return;
            }

            List<TransactionReport> reports = adminService.getTransactionReport(date, transactionType);

            // Convert the reports to JSON and send the response
            Gson gson = new Gson();
            sendJsonResponse(resp, gson.toJson(reports));
        } catch (Exception e) {
            sendErrorResponse(resp, e.getMessage());
        }
    }
}

//   @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("adminUsername") == null) {
//            sendErrorResponse(resp, "Not authenticated");
//            return;
//        }
//
//        String type = req.getParameter("type");
//        String date = req.getParameter("date");
//
//        if (type == null || type.isEmpty() || date == null || date.isEmpty()) {
//            sendErrorResponse(resp, "Both report type and date are required");
//            return;
//        }
//

//    }

