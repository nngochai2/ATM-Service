package org.atm.dao.impl;

import org.atm.dao.TransactionDAO;
import org.atm.model.Transaction;
import org.atm.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {
    private final Logger logger = LoggerFactory.getLogger(TransactionDAOImpl.class);

    @Override
    public List<Transaction> findByCardNumber(Long cardNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE card_number = ? ORDER BY transaction_date DESC LIMIT 5";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cardNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(this.mapResultSetToTransactions(rs));
                }
                return transactions;
            }
        } catch (SQLException e) {
            logger.error("Error finding transactions for card number: {}", cardNumber, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Transaction> findByDate(Date date) {
        String sql = "SELECT * FROM transactions WHERE DATE(transaction_date) = ?";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(this.mapResultSetToTransactions(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding transactions for date {}", date, e);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByCardNumberAndDate(Long cardNumber, Date date) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE card_number = ? AND transaction_date = ?";

        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cardNumber);
            pstmt.setDate(2, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(this.mapResultSetToTransactions(rs));
                }
                return transactions;
            }
        } catch (SQLException e) {
            logger.error("Error finding transactions for card number: {} on date: {}", cardNumber, date, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Transaction> findByCardNumberAndType(Long cardNumber, Transaction.TransactionType type) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE card_number = ? AND type = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cardNumber);
            pstmt.setString(2, String.valueOf(type));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(this.mapResultSetToTransactions(rs));
                }
                return transactions;
            }
        } catch (SQLException e) {
            logger.error("Error finding transactions for card number: {} with type: {}", cardNumber, type, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Transaction> findByType(Transaction.TransactionType type) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE type = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(type));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(this.mapResultSetToTransactions(rs));
                }
                return transactions;
            }
        } catch (SQLException e) {
            logger.error("Error finding transactions by type: {}", type, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Transaction> findByTypeAndDate(Transaction.TransactionType type, Date date) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE type = ? AND DATE(transaction_date) = ?";

        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type.toString());
            pstmt.setDate(2, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(this.mapResultSetToTransactions(rs));
                }
                return transactions;
            }
        } catch (SQLException e) {
            logger.error("Error finding transactions by type: {} and date: {}", type, date, e);
            return new ArrayList<>();
        }
    }

    @Override
    public int getDailyTransactionCount(Long cardNumber, Transaction.TransactionType type) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE card_number = ? AND type = ? " +
                "AND DATE(transaction_date) = CURRENT_DATE";

        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cardNumber);
            pstmt.setString(2, type.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting daily transactions for card: {}", cardNumber, e);
        }
        return 0;
    }

    @Override
    public double getDailyTransactionTotal(Long cardNumber, Transaction.TransactionType type) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE card_number = ? " +
                    "AND type = ? AND DATE(transaction_date) = CURRENT_DATE";
        try (Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cardNumber);
            pstmt.setString(2, String.valueOf(type));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting daily transaction total for card: {}", cardNumber, e);
        }
        return 0.0;
    }

    @Override
    public Transaction findById(String id) {
        return null;
    }

    @Override
    public boolean save(Transaction transaction) {
        String sql = "INSERT INTO transactions (card_number, type, amount, balance_after, description, transaction_date, to_card) " +
                    "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?) RETURNING transaction_id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, transaction.getCardNumber());
            pstmt.setString(2, transaction.getType().toString());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setDouble(4, transaction.getBalanceAfter());
            pstmt.setString(5, transaction.getDescription());

            if (transaction.getType() == Transaction.TransactionType.TRANSFER) {
                pstmt.setLong(6, transaction.getToCardNumber());
            } else {
                pstmt.setNull(6, Types.BIGINT);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    transaction.setTransactionId(rs.getLong("transaction_id"));
                    logger.info("Created transaction with ID: {} for card: {}",
                            transaction.getTransactionId(), transaction.getCardNumber());
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving transaction for card: {}", transaction.getCardNumber(), e);
        }
        return false;
    }

    @Override
    public boolean update(Transaction entity) {
        return false;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    private Transaction mapResultSetToTransactions(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getLong("transaction_id"));
        transaction.setCardNumber(rs.getLong("card_number"));
        transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
        transaction.setType(Transaction.TransactionType.valueOf(rs.getString("type")));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setDescription(rs.getString("description"));
        transaction.setBalanceAfter(rs.getDouble("balance_after"));

        if (transaction.getType() == Transaction.TransactionType.TRANSFER) {
            transaction.setToCard(rs.getLong("to_card"));
        }

        return transaction;
    }

}

