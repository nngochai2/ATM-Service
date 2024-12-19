package org.atm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    private static final String URL =
            "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres?user=postgres.duvbnejsreiybzujqijt&password=Nguyenngochai135&sslmode=require";

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(URL);
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