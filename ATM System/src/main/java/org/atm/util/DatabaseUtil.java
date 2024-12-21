package org.atm.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DatabaseUtil {
    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            assert URL != null;
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL Driver not found", e);
            throw new SQLException("PostgreSQL Driver not found", e);
        }
    }

    public static void main(String[] args) {
        // Simple test to verify connection and run a test query.
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                logger.info("Database connection successful!");

                // Test a simple query
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    if (rs.next()) {
                        logger.info("Query test successful!");
                    } else {
                        logger.warn("Query test returned no results.");
                    }
                } catch (SQLException e) {
                    logger.error("Failed to run test query: ", e);
                }
            } else {
                logger.error("Connection is null or closed.");
            }
        } catch (SQLException e) {
            logger.error("Database connection failed: ", e);
        }
    }
}