package com.company.running;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {
    public static final String MYSQL_URL = "jdbc:mysql://localhost:3306/";
    public static final String MYSQL_USER = "root";
    public static final String MYSQL_PASSWORD = "musor111";
    public static String TABLE_NAME;

    /**
     * Sets up MySQL table. Must be called prior to creating or using TreeNodes.
     */
    public static void setupMySQL(String tableName) throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        Statement statement = connection.createStatement();
        String sql = "use i_tree";
        statement.executeUpdate(sql);

        String createTable = "CREATE TABLE " + tableName + " (" +
                "ID INT PRIMARY KEY AUTO_INCREMENT," +
                "ParentID INT," +
                "LeftID INT," +
                "RightID INT," +
                "Domain BLOB," +
                "LinearFunction BLOB," +
                "IntersectionIndex INT)";
        statement.executeUpdate(createTable);

        TABLE_NAME = tableName;

        connection.close();
    }
}
