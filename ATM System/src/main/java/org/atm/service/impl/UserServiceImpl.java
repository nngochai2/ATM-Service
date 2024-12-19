package org.atm.service.impl;

import org.atm.dao.UserDAO;
import org.atm.dao.TransactionDAO;
import org.atm.exception.ATMException;
import org.atm.model.User;
import org.atm.model.Transaction;
import org.atm.service.UserService;

public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;

    private static final double DAILY_LIMIT = 25000.0;
    private static final int MAX_DAILY_TRANSACTIONS = 5;

    public UserServiceImpl(UserDAO userDAO, TransactionDAO transactionDAO) {
        this.userDAO = userDAO;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public boolean authenticate(Long cardNumber, String pin) throws ATMException {
        try {
            return userDAO.verifyPin(cardNumber, pin);
        } catch (Exception e) {
            throw new ATMException("Error during authentication", e);
        }
    }

    @Override
    public boolean deposit(Long cardNumber, double amount) throws ATMException {
        try {
            // Check daily deposit limits
            int dailyCount = transactionDAO.getDailyTransactionCount(cardNumber, Transaction.TransactionType.DEPOSIT);
            double dailyTotal = transactionDAO.getDailyTransactionTotal(cardNumber, Transaction.TransactionType.DEPOSIT);

            if (dailyCount >= MAX_DAILY_TRANSACTIONS) {
                throw new ATMException("Daily transaction limit exceeded");
            }
            if (dailyTotal + amount > DAILY_LIMIT) {
                throw new ATMException("Daily deposit limit exceeded");
            }

            User user = userDAO.findByCardNumber(cardNumber);
            double newBalance = user.getBalance() + amount;

            if (userDAO.updateBalance(cardNumber, newBalance)) {
                // Create transaction without ID
                Transaction transaction = new Transaction(
                        cardNumber,
                        Transaction.TransactionType.DEPOSIT,
                        amount,
                        newBalance,
                        "Deposit"
                );

                return transactionDAO.save(transaction);
            }
        } catch (Exception e) {
            throw new ATMException("Error processing deposit", e);
        }
        return false;
    }

    @Override
    public boolean withdraw(Long cardNumber, double amount) throws ATMException {
        try {
            // Check daily withdrawal limits
            int dailyCount = transactionDAO.getDailyTransactionCount(cardNumber, Transaction.TransactionType.WITHDRAW);
            double dailyTotal = transactionDAO.getDailyTransactionTotal(cardNumber, Transaction.TransactionType.WITHDRAW);

            if (dailyCount >= MAX_DAILY_TRANSACTIONS) {
                throw new ATMException("Daily transaction limit exceeded");
            }
            if (dailyTotal + amount > DAILY_LIMIT) {
                throw new ATMException("Daily withdrawal limit exceeded");
            }

            User user = userDAO.findByCardNumber(cardNumber);
            if (user.getBalance() < amount) {
                throw new ATMException("Insufficient funds");
            }

            double newBalance = user.getBalance() - amount;
            return userDAO.updateBalance(cardNumber, newBalance);
        } catch (Exception e) {
            throw new ATMException("Error processing withdrawal", e);
        }
    }

    @Override
    public double getBalance(Long cardNumber) throws ATMException {
        try {
            User user = userDAO.findByCardNumber(cardNumber);
            return user.getBalance();
        } catch (Exception e) {
            throw new ATMException("Error retrieving balance", e);
        }
    }

    @Override
    public boolean transferMoney(Long fromCard, Long toCard, double amount, String description)
            throws ATMException {
        try {
            // Check if the recipient exists
            User recipient = userDAO.findByCardNumber(toCard);
            if (recipient == null) {
                throw new ATMException("Recipient account not found");
            }

            // If no description provided, use default
            String transferDescription = (description != null && !description.trim().isEmpty()
                ? description
                : "Transfer to " + toCard);

            // Withdraw from sender
            if (!withdraw(fromCard, amount)) {
                throw new ATMException("Error withdrawing from sender account");
            }

            // Deposit to receiver
            try {
                User user = userDAO.findByCardNumber(toCard);
                double newBalance = user.getBalance() + amount;
                if (userDAO.updateBalance(toCard, newBalance)) {
                    // Create transaction with custom description
                    Transaction transaction = new Transaction(
                            toCard,
                            Transaction.TransactionType.TRANSFER,
                            amount,
                            newBalance,
                            transferDescription
                    );
                    transactionDAO.save(transaction);
                    return true;
                }

                // If deposit fails, rollback withdrawal
                User sender = userDAO.findByCardNumber(fromCard);
                userDAO.updateBalance(fromCard, sender.getBalance() + amount);
                throw new ATMException("Error completing transfer");
            } catch (Exception e) {
                // Rollback withdrawal if deposit fails
                User sender = userDAO.findByCardNumber(fromCard);
                userDAO.updateBalance(fromCard, sender.getBalance() + amount);
                throw new ATMException("Error completing transfer");
            }
        } catch (Exception e) {
            throw new ATMException("Error processing transfer", e);
        }
    }
//
//            // Deposit to recipient
//            try {
//                User user = userDAO.findByCardNumber(toCard);
//                double newBalance = user.getBalance() + amount;
//                if (userDAO.updateBalance(toCard, newBalance)) {
//                    Transaction transaction = new Transaction(toCard, Transaction.TransactionType.TRANSFER,
//                                                           amount, newBalance, "Transfer from " + fromCard);
//                    transactionDAO.save(transaction);
//                    return true;
//                }
//                // If deposit fails, rollback withdrawal
//                User sender = userDAO.findByCardNumber(fromCard);
//                userDAO.updateBalance(fromCard, sender.getBalance() + amount);
//                throw new ATMException("Error completing transfer");
//            } catch (Exception e) {
//                // Rollback withdrawal if deposit fails
//                User sender = userDAO.findByCardNumber(fromCard);
//                userDAO.updateBalance(fromCard, sender.getBalance() + amount);
//                throw new ATMException("Error completing transfer", e);
//            }
//        } catch (Exception e) {
//            throw new ATMException("Error processing transfer", e);
//        }
//    }
//}

    @Override
    public boolean changePin(Long cardNumber, String oldPin, String newPin) throws ATMException {
        try {
            if (!userDAO.verifyPin(cardNumber, oldPin)) {
                throw new ATMException("Invalid old PIN");
            }
            return userDAO.updatePin(cardNumber, newPin);
        } catch (ATMException e) {
            throw new ATMException("Error changing PIN", e);
        }
    }
}
