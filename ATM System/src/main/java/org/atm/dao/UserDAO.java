package org.atm.dao;

import org.atm.model.User;

import java.util.List;

public interface UserDAO extends GenericDAO<User> {
    User findById(String userId);
    String getLatestUserId();
    User findByCardNumber(Long cardNumber);
    Long getLatestCardNumber();
    List<User> getAllUsers();
    boolean verifyPin(Long cardNumber, String pin);
    boolean updateBalance(Long cardNumber, double newBalance);
    boolean updatePin(Long cardNumber, String newPin);
}
