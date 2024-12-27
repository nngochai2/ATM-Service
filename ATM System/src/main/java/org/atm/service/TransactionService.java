package org.atm.service;

import org.atm.dto.TransactionReport;
import org.atm.model.Transaction;

import java.sql.Date;
import java.util.List;

public interface TransactionService {
    List<Transaction> getTransactionHistory(Long cardNumber);
    int getDailyTransactionCount(Long cardNumber, Transaction.TransactionType type);
    double getDailyTransactionTotal(Long cardNumber, Transaction.TransactionType type);
}
