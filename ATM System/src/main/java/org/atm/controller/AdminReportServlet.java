package org.atm.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.atm.dao.TransactionDAO;
import org.atm.dao.impl.TransactionDAOImpl;
import org.atm.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/admin/report")
public class AdminReportServlet extends BaseServlet {
    private static Logger logger = LoggerFactory.getLogger(AdminReportServlet.class);
    private TransactionDAO transactionDAO;

    @Override
    public void init() throws ServletException {
        transactionDAO = new TransactionDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session == null) {
            sendErrorResponse(resp, "Not authenticated");
            return;
        }

        try {
            String dateStr = req.getParameter("date");
            Date date = dateStr != null ? Date.valueOf(dateStr) : new Date(System.currentTimeMillis());

            List<Transaction> transactions = transactionDAO.findByDate(date);

            // Convert to JSON and response
            Gson gson = new Gson();
            sendJsonResponse(resp, gson.toJson(transactions));
        } catch (Exception e) {
            logger.error("Error generating report", e);
            sendErrorResponse(resp, e.getMessage());
        }
    }
}
