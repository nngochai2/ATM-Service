package org.atm.service;

import org.atm.dto.TransactionReport;
import org.atm.exception.ATMException;
import org.atm.model.Transaction;
import org.atm.model.User;

import java.util.List;

public interface AdminService {
    boolean authenticate(String username, String password) throws ATMException;
    boolean createUser(String pin, String name, String contactNumber, String gender, String address)
            throws ATMException;
    List<User> getAccountReport() throws ATMException;
    List<TransactionReport> getTransactionReport(String date, Transaction.TransactionType transactionType) throws ATMException;
}
