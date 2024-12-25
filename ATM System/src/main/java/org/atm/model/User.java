package org.atm.model;

import java.util.List;

public class User {
    private String userId;      // Primary key, unique identification number
    private String name;
    private Long cardNumber;    // 10-digit number, auto-generated
    private String pin;
    private String gender;
    private String address;
    private String contactNumber;
    private double balance;
    private List<Transaction> transactions;

    public User(){}

    public User(String userId, String name, Long cardNumber, String pin, String gender, String address, String contactNumber, double balance) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.userId = userId;
        this.pin = pin;
        this.gender = gender;
        this.address = address;
        this.contactNumber = contactNumber;
        this.balance = balance;
    }

    public Long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
