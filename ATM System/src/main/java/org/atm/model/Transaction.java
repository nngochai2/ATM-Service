package org.atm.model;

import java.sql.Timestamp;

public class Transaction {
    public enum TransactionType {
        DEPOSIT,
        WITHDRAW,
        TRANSFER
    };
    private Long transactionId;
    private Long cardNumber;
    private TransactionType type;
    private Long toCardNumber; // For transfers
    private double amount;
    private double balanceAfter;
    private String description;
    private Timestamp transactionDate;

    public Transaction() {}

    public Transaction(Long cardNumber, TransactionType type, Long toCardNumber, double amount, double balanceAfter, String description) {
        this.cardNumber = cardNumber;
        this.type = type;
        this.toCardNumber = toCardNumber;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getToCardNumber() {
        return toCardNumber;
    }

    public void setToCard(Long toCardNumber) {
        this.toCardNumber = toCardNumber;
    }
}
