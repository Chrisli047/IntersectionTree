package com.company.running;

import java.sql.*;

public class MySQL {
    public static final String MYSQL_URL = "jdbc:mysql://localhost:3306/";
    public static final String MYSQL_USER = "root";
    public static final String MYSQL_PASSWORD = "musor111";
    public static final String DATABASE_NAME = "i_tree";

    /**
     * Creates MySQL database. Must be called prior to tree construction or other MySQL commands.
     */
    public static void setupMySQL() throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);

        String sql = "CREATE DATABASE " + DATABASE_NAME;
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);

        statement.close();
        connection.close();
    }

    /**
     * Setup database connection.
     */
    public static Connection setupConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);

        String sql = "use " + DATABASE_NAME;
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);

        statement.close();

        return connection;
    }

    /**
     * Creates MySQL table. Must be called prior to creating or using TreeNodes.
     */
    public static void createMySQLTable(String tableName) throws SQLException {
        Connection connection = setupConnection();

        String createTable = "CREATE TABLE " + tableName + " (" +
                "ID INT PRIMARY KEY AUTO_INCREMENT," +
                "ParentID INT," +
                "LeftID INT," +
                "RightID INT," +
                "Domain BLOB," +
                "LinearFunction BLOB," +
                "IntersectionIndex INT)";
        Statement statement = connection.createStatement();
        statement.executeUpdate(createTable);

        statement.close();
        connection.close();
    }

    /**
     * Cleans up MySQL.
     */
    public static void cleanupMySQL() throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);

        String sql = "DROP DATABASE " + DATABASE_NAME;
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);

        statement.close();
        connection.close();
    }
}
