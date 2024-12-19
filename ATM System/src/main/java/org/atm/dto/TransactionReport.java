package org.atm.dto;

import java.sql.Timestamp;

public class TransactionReport {
    private Long cardNumber;
    private Long toCardNumber;
    private double amount;
    private double balanceAfter;
    private Timestamp transactionDate;
    private String description;

    public Long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Long getToCardNumber() {
        return toCardNumber;
    }

    public void setToCardNumber(Long toCardNumber) {
        this.toCardNumber = toCardNumber;
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

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
