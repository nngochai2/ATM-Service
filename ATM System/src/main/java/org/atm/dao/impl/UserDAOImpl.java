package org.atm.dao.impl;

import org.atm.dao.UserDAO;
import org.atm.model.User;
import org.atm.util.DatabaseUtil;
import org.atm.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    @Override
    public User findByCardNumber(Long cardNumber) {
        String sql = "SELECT * FROM users WHERE card_number = ?";
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cardNumber);

            logger.debug("Attempting to find user with card number: {}", cardNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                logger.debug("Successfully found user with card number: {}", cardNumber);
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by card number: {}", cardNumber, e);
        }
        return null;
    }

    @Override
    public Long getLatestCardNumber() {
        String sql = "SELECT card_number FROM users ORDER BY card_number DESC LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong("card_number");
            }
            return 1000000000L; // First card number if no users exist
        } catch (SQLException e) {
            logger.error("Error getting latest card number", e);
            return 1000000000L;
        }
    }

    @Override
    public User findById(String id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                this.mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            logger.error("Failed to find user with ID: {}", id, e);
        }
        return null;
    }

    @Override
    public String getLatestUserId() {
        String sql = "SELECT user_id FROM users ORDER BY user_id DESC LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int latestId = rs.getInt("user_id");
                return String.valueOf(latestId + 1);
            }
            // First user
            return "1";

        } catch (SQLException e) {
            logger.error("Error getting latest user ID", e);
            return "1";
        }
    }

    @Override
    public boolean verifyPin(Long cardNumber, String pin) {
        String sql = "SELECT pin FROM users WHERE card_number = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cardNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPin = rs.getString("pin");
                return PasswordUtil.verifyPassword(pin, hashedPin);
            }
        } catch (SQLException e) {
            logger.error("Error: Wrong PIN number");
        }
        return false;
    }

    @Override
    public boolean updateBalance(Long cardNumber, double newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE card_number = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newBalance);
            pstmt.setLong(2, cardNumber);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating balance", e);
        }
        return false;
    }

    // @Override
    //public boolean updatePin(Long cardNumber, String newPin) {
    //    String sql = "UPDATE users SET pin = ? WHERE card_number = ?";
    //
    //    try (Connection conn = DatabaseUtil.getConnection();
    //         PreparedStatement pstmt = conn.prepareStatement(sql)) {
    //
    //        String hashedPin = PasswordUtil.hashPin(newPin);  // Hash the new PIN
    //        pstmt.setString(1, hashedPin);
    //        pstmt.setLong(2, cardNumber);
    //
    //        return pstmt.executeUpdate() > 0;
    //    } catch (SQLException e) {
    //        logger.error("Error while updating pin", e);
    //    }
    //    return false;
    //}

    @Override
    public boolean updatePin(Long cardNumber, String newPin) {
        String sql = "UPDATE users SET pin = ? WHERE card_number = ?";

        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPin = PasswordUtil.hashPassword(newPin); // Hash the new PIN
            pstmt.setString(1, hashedPin);
            pstmt.setLong(2, cardNumber);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error while updating pin", e);
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(this.mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        return users;
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO users VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Get new user ID and card number
            String userId = getLatestUserId();
            long cardNumber = getLatestCardNumber() + 1;
            String hashedPin = PasswordUtil.hashPassword(user.getPin());

            pstmt.setInt(1, Integer.parseInt(userId));
            pstmt.setString(2, user.getName());
            pstmt.setLong(3, cardNumber);    // Use the generated card number
            pstmt.setString(4, hashedPin);  // Use hashed password
            pstmt.setString(5, user.getGender());
            pstmt.setString(6, user.getAddress());
            pstmt.setLong(7, Long.parseLong(user.getContactNumber().replaceAll("[^0-9]", ""))); // Convert to number
            pstmt.setDouble(8, 0.0);  // Initial balance

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error("Error saving new user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(User entity) {
        return false;
    }

    @Override
    public boolean delete(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, userId);
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            logger.error("Error deleting account", e);
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getString("user_id"));
        user.setCardNumber(rs.getLong("card_number"));
        user.setPin(rs.getString("pin"));
        user.setName(rs.getString("name"));
        user.setContactNumber(rs.getString("contact_number"));
        user.setGender(rs.getString("gender"));
        user.setAddress(rs.getString("address"));
        user.setBalance(rs.getDouble("balance"));
        return user;
    }
}