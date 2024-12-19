package org.atm.dao;

import org.atm.model.Admin;

import java.util.List;

public interface AdminDAO extends GenericDAO<Admin> {
    List<Admin> getAllActiveAdmins();
    Admin findByUsername(String username);
    boolean verifyPassword(String username, String password);
}
