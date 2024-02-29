package com.company.running;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        try {
            setup();
            Test.runTests();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Setup MySQL.
     */
    private static void setup() throws SQLException {
        MySQL.setupMySQL();
    }

    /**
     * Cleanup MySQL.
     */
    private static void cleanup() throws SQLException {
        MySQL.cleanupMySQL();
    }
}
