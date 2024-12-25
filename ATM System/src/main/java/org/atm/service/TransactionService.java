package org.atm.service;

import org.atm.dto.TransactionReport;
import org.atm.model.Transaction;

import java.sql.Date;
import java.util.List;

public interface TransactionService {
    List<Transaction> getTransactionHistory(Long cardNumber);
    List<Transaction> getDailyTransactions(Long cardNumber, Transaction.TransactionType type, Date date);
    boolean isWithinDailyLimit(Long cardNumber, Transaction.TransactionType type, double amount);
    int getDailyTransactionCount(Long cardNumber, Transaction.TransactionType type);
    double getDailyTransactionTotal(Long cardNumber, Transaction.TransactionType type);
    boolean saveTransaction(Transaction transaction);
}
