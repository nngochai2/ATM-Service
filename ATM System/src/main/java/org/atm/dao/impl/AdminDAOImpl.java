package org.atm.dao.impl;

import org.atm.dao.AdminDAO;
import org.atm.model.Admin;
import org.atm.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAOImpl implements AdminDAO {
    private static final Logger logger = LoggerFactory.getLogger(AdminDAOImpl.class);

    @Override
    public Admin findByUsername(String username) {
        String sql = "SELECT * FROM admins WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Found admin with username: {}", username);
                    return this.mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding admin by username: {}", username, e);
        }
        return null;
    }

    @Override
    public boolean verifyPassword(String username, String password) {
        String sql = "SELECT password FROM admins WHERE username = ? AND is_active = true";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password").equals(password);
                }
            }
        } catch (SQLException e) {
            logger.error("Error verifying admin credentials for username: {}", username, e);
        }
        return false;
    }

    @Override
    public List<Admin> getAllActiveAdmins() {
        String sql = "SELECT * FROM admins WHERE is_active = true";
        List<Admin> admins = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                admins.add(mapResultSetToAdmin(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving active admins", e);
        }
        return admins;
    }

    @Override
    public Admin findById(String id) {
        return null;
    }

    @Override
    public boolean save(Admin entity) {
        return false;
    }

    @Override
    public boolean update(Admin entity) {
        return false;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getString("admin_id"));
        admin.setUsername(rs.getString("username"));
        admin.setPassword(rs.getString("password"));
        admin.setName(rs.getString("name"));
        admin.setEmail(rs.getString("email"));
        admin.setActive(rs.getBoolean("is_active"));
        return admin;
    }
}
