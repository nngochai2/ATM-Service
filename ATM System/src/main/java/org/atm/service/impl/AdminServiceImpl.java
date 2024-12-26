package org.atm.service.impl;

import org.atm.dao.AdminDAO;
import org.atm.dao.TransactionDAO;
import org.atm.dao.UserDAO;
import org.atm.dto.TransactionReport;
import org.atm.exception.ATMException;
import org.atm.model.Transaction;
import org.atm.model.User;
import org.atm.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AdminServiceImpl implements AdminService {
    private final AdminDAO adminDAO;
    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);


    public AdminServiceImpl(AdminDAO adminDAO, UserDAO userDAO, TransactionDAO transactionDAO) {
        this.adminDAO = adminDAO;
        this.userDAO = userDAO;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public boolean authenticate(String username, String password) throws ATMException {
        try {
            return adminDAO.verifyPassword(username, password);
        } catch (Exception e) {
            throw new ATMException("Error during admin authentication", e);
        }
    }

    @Override
    public void createUser(User user) throws ATMException {
        try {
            // Validate user input
            if (user.getPin() == null || !user.getPin().matches("\\d{4}")) {
                throw new ATMException("PIN must be 4 digits");
            }
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                throw new ATMException("Name is required");
            }
            if (user.getContactNumber() == null || user.getContactNumber().trim().isEmpty()) {
                throw new ATMException("Contact number is required");
            }
            if (user.getGender() == null || user.getGender().trim().isEmpty()) {
                throw new ATMException("Gender is required");
            }
            if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
                throw new ATMException("Address is required");
            }

            // Save the user
            if (!userDAO.save(user)) {
                throw new ATMException("Failed to create customer");
            }

            logger.info("Successfully created new user {} with card number {}", user.getName(), user.getCardNumber());

        } catch (Exception e) {
            logger.error("Error creating customer", e);
            throw new ATMException("Failed to create customer: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAccountReport() throws ATMException {
        logger.info("Generating account report");
        try {
            return userDAO.getAllUsers();
        } catch (Exception e) {
            logger.error("Error generating account report", e);
            throw new ATMException("Error generating account report", e);
        }
    }

    private TransactionReport convertToReport(Transaction transaction) {
        TransactionReport report = new TransactionReport();
        report.setCardNumber(transaction.getCardNumber());
        report.setAmount(transaction.getAmount());
        report.setBalanceAfter(transaction.getBalanceAfter());
        report.setTransactionDate(transaction.getTransactionDate());
        report.setDescription(transaction.getDescription());

        if (transaction.getType() == Transaction.TransactionType.TRANSFER) {
            report.setToCardNumber(transaction.getToCardNumber());
        }
        return report;
    }

    @Override
    public List<TransactionReport> getTransactionReport(String dateStr, Transaction.TransactionType type) throws ATMException {
        logger.info("Generating {} report for date: {}", type, dateStr);
        try {
            Date date = Date.valueOf(dateStr);
            List<Transaction> transactions = transactionDAO.findByTypeAndDate(type, date);

            return transactions.stream()
                    .map(this::convertToReport)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid date format provided: {}", dateStr, e);
            throw new ATMException("Invalid date format. Use YYYY-MM-DD", e);
        } catch (Exception e) {
            logger.error("Error generating {} report", type, e);
            throw new ATMException("Error generating report: " + type, e);
        }
    }
}
