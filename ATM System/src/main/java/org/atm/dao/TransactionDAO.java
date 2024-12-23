package org.atm.dao;

import org.atm.model.Transaction;

import java.sql.Date;
import java.util.List;

public interface TransactionDAO extends GenericDAO<Transaction> {

    List<Transaction> findByCardNumber(Long cardNumber);
    List<Transaction> findByDate(Date date);
    List<Transaction> findByCardNumberAndDate(Long cardNumber, Date date);
    List<Transaction> findByCardNumberAndType(Long cardNumber, Transaction.TransactionType type);
    List<Transaction> findByType(Transaction.TransactionType type);
    List<Transaction> findByTypeAndDate(Transaction.TransactionType type, Date date);
    int getDailyTransactionCount(Long cardNumber, Transaction.TransactionType type);
    double getDailyTransactionTotal(Long cardNumber, Transaction.TransactionType transactionType);
}
