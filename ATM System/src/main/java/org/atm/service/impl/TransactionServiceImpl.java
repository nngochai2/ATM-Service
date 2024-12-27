package org.atm.service.impl;

import org.atm.dao.TransactionDAO;
import org.atm.model.Transaction;
import org.atm.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.ArrayList;
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
    public int getDailyTransactionCount(Long cardNumber, Transaction.TransactionType type) {
        return transactionDAO.getDailyTransactionCount(cardNumber, type);
    }

    @Override
    public double getDailyTransactionTotal(Long cardNumber, Transaction.TransactionType type) {
        return transactionDAO.getDailyTransactionTotal(cardNumber, type);
    }
}
