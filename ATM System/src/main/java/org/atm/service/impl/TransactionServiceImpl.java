package org.atm.service.impl;

import org.atm.dao.TransactionDAO;
import org.atm.model.Transaction;
import org.atm.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {
    private final TransactionDAO transactionDAO;
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    public TransactionServiceImpl(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    @Override
    public List<Transaction> getTransactionHistory(Long cardNumber) {
        logger.debug("Retrieving transaction history for card: {}", cardNumber);
        return transactionDAO.findByCardNumber(cardNumber);
    }

    @Override
    public List<Transaction> getDailyTransactions(Long cardNumber, Transaction.TransactionType type, Date date) {
        logger.debug("Retrieving daily transactions for card: {} and type: {}", cardNumber, type);
        return transactionDAO.findByTypeAndDate(type, new Date(System.currentTimeMillis()));
    }

    @Override
    public boolean isWithinDailyLimit(Long cardNumber, Transaction.TransactionType type, double amount) {
        int count = getDailyTransactionCount(cardNumber, type);
        double total = getDailyTransactionTotal(cardNumber, type);

        if (count >= 5) {
            logger.warn("Daily transaction count limit exceeded for card: {}", cardNumber);
            return false;
        }

        if (total + amount > 25000) {
            logger.warn("Daily transaction amount limit exceeded for card: {}", cardNumber);
            return false;
        }

        return true;
    }

    @Override
    public int getDailyTransactionCount(Long cardNumber, Transaction.TransactionType type) {
        return transactionDAO.getDailyTransactionCount(cardNumber, type);
    }

    @Override
    public double getDailyTransactionTotal(Long cardNumber, Transaction.TransactionType type) {
        return transactionDAO.getDailyTransactionTotal(cardNumber, type);
    }

    @Override
    public boolean saveTransaction(Transaction transaction) {
        logger.debug("Saving new transaction for card: {}", transaction.getCardNumber());
        return transactionDAO.save(transaction);
    }
}
