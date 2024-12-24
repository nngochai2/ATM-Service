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
    public boolean createUser(String pin, String name, String contactNumber, String gender, String address)
            throws ATMException {
        try {
            // Generate user ID
            String userId = userDAO.getLatestUserId();

            // Generate card user
            Long cardNumber = userDAO.getLatestCardNumber() + 1;

            // Create new user with generated ID
            User newUser = new User();
            newUser.setUserId(userId);
            newUser.setCardNumber(cardNumber);
            //
            return userDAO.save(newUser);
        } catch (Exception e) {
            logger.error("Error creating user", e);
            throw new ATMException("Error creating user: " + e.getMessage());
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
    @Override
    public List<TransactionReport> getWithdrawalReport(String dateStr) throws ATMException {
        logger.info("Generating withdrawal report for date: {}", dateStr);
        try {
            Date date = Date.valueOf(dateStr);
            List<Transaction> withdrawals = transactionDAO.findByTypeAndDate(
                    Transaction.TransactionType.WITHDRAW, date
            );
            return withdrawals.stream()
                    .map(this::convertToReport)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid date format provided: {}", dateStr, e);
            throw new ATMException("Invalid date format. Use YYYY-MM-DD", e);
        } catch (Exception e) {
            logger.error("Error generating withdrawal report", e);
            throw new ATMException("Error generating withdrawal report", e);
        }
    }

    @Override
    public List<TransactionReport> getDepositReport(String dateStr) throws ATMException {
        logger.info("Generating deposit report for date: {}", dateStr);
        try {
            Date date = Date.valueOf(dateStr);
            List<Transaction> deposits = transactionDAO.findByTypeAndDate(
                    Transaction.TransactionType.DEPOSIT, date);

            return deposits.stream()
                    .map(this::convertToReport)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid date format provided: {}", dateStr, e);
            throw new ATMException("Invalid date format. Use YYYY-MM-DD", e);
        } catch (Exception e) {
            logger.error("Error generating deposit report", e);
            throw new ATMException("Error generating deposit report", e);
        }
    }

    @Override
    public List<TransactionReport> getTransferReport(String dateStr) throws ATMException {
        logger.info("Generating transfer report for date: {}", dateStr);
        try {
            Date date = Date.valueOf(dateStr);
            List<Transaction> transfers = transactionDAO.findByTypeAndDate(
                    Transaction.TransactionType.TRANSFER, date);

            return transfers.stream()
                    .map(this::convertToReport)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid date format provided: {}", dateStr, e);
            throw new ATMException("Invalid date format. Use YYYY-MM-DD", e);
        } catch (Exception e) {
            logger.error("Error generating transfer report", e);
            throw new ATMException("Error generating transfer report", e);
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
            report.setToCardNumber(transaction.getT);
        }
        return report;
    }
}
