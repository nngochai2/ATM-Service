package org.atm;

import org.atm.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionTest {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionTest.class);

    public static void testConnection() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                logger.info("Database connection successful!");

                // Optional: test a simple query
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    if (rs.next()) {
                        logger.info("Query test successful!");
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Database connection failed: ", e);
        }
    }

    public static void main(String[] args) {
        testConnection();
    }
}


