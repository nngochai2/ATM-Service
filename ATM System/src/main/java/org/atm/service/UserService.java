package org.atm.service;

import org.atm.exception.ATMException;

public interface UserService {
    boolean authenticate(Long cardNumber, String pin) throws ATMException;
    boolean deposit(Long cardNumber, double amount) throws ATMException;
    boolean withdraw(Long cardNumber, double amount) throws ATMException;
    double getBalance(Long cardNumber) throws ATMException;
    boolean transferMoney(Long fromCard, Long toCard, double amount, String description) throws ATMException;
    boolean changePin(Long cardNumber, String oldPin, String newPin) throws ATMException;
}
