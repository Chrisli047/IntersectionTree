package com.company.running;
import java.sql.*;

public class NodeRecord {
    DomainType d;
    Function f;
    int intersectionIndex;
    int parentID;
    int leftID;
    int rightID;
    int ID = 0;

    public NodeRecord (DomainType d, Function f, int parentID, int leftID, int rightID) {
        this.d = d;
        this.f = f;
        this.intersectionIndex = -1;
        this.parentID = parentID;
        this.leftID = leftID;
        this.rightID = rightID;
    }

    public NodeRecord (DomainType d, Function f, int intersectionIndex, int parentID, int leftID, int rightID) {
        this.d = d;
        this.f = f;
        this.intersectionIndex = intersectionIndex;
        this.parentID = parentID;
        this.leftID = leftID;
        this.rightID = rightID;
    }

    public static void createTable(String table_name) {
        try {
            Connection connection = DriverManager.getConnection(MYSQLConstants.MYSQL_URL, MYSQLConstants.MYSQL_USER, MYSQLConstants.MYSQL_PASSWORD);
            Statement stmt = connection.createStatement();
            String sql = "use i_tree";
            stmt.executeUpdate(sql);
//            System.out.println("Create tables... ");
            String createTable = "CREATE TABLE " + table_name + " (" +
                    "    ID INT PRIMARY KEY AUTO_INCREMENT,  " +
                    "    Domain BLOB," +
                    "    LinearFunction BLOB," +
                    "    IntersectionIndex INT, " +
                    "    ParentID INT, " +
                    "    LeftID INT, " +
                    "    rightID INT)";
            stmt.executeUpdate(createTable);
//            System.out.println("Table created successfully");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertToMySql(int dimension, String table_name, boolean storePoints) {
        int returnId = 0;
        try {
            Connection connection = DriverManager.getConnection(MYSQLConstants.MYSQL_URL, MYSQLConstants.MYSQL_USER, MYSQLConstants.MYSQL_PASSWORD);
            Statement stmt = connection.createStatement();
            String sql = "use i_tree";
            stmt.executeUpdate(sql);
//            System.out.println("Insert record... ");

            connection.setAutoCommit(false);
            // Create a PreparedStatement with the SQL statement for inserting a record
            String insertSql = "INSERT INTO " + table_name + " (Domain, LinearFunction, IntersectionIndex, ParentID, " +
                    "LeftID, rightID) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

            // Set the values for the parameters in the prepared statement
            pstmt.setBytes(1, d.toByte(dimension, storePoints));
            pstmt.setBytes(2, f.toByte(dimension));
            pstmt.setInt(3, intersectionIndex);
            pstmt.setInt(4, -1);
            pstmt.setInt(5, -1);
            pstmt.setInt(6, -1);

            // Execute the insert statement
            pstmt.executeUpdate();

            // Get the AUTO_INCREMENT value generated by the insert statement
            ResultSet rs = pstmt.getGeneratedKeys();
            int newId = -1;
            if (rs.next()) {
                newId = rs.getInt(1);
            }
//            System.out.println("New record inserted with ID " + newId);

            // Commit the transaction
            connection.commit();

            // Clean up resources
            pstmt.close();

//            System.out.println("Record inserted successfully");
            connection.close();
            returnId = newId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnId;
    }

    public static NodeRecord getRecordById(int id, boolean simplex, int dimension, String table_name,
                                           boolean storedPoints) {
        NodeRecord nodeRecord = null;
        try {
            Connection connection = DriverManager.getConnection(MYSQLConstants.MYSQL_URL, MYSQLConstants.MYSQL_USER, MYSQLConstants.MYSQL_PASSWORD);
            Statement stmt = connection.createStatement();
            String sql = "use i_tree";
            stmt.executeUpdate(sql);
//            System.out.println("Select record...");

            // Create a PreparedStatement with the SQL statement for selecting a record based on its ID
            String selectSql = "SELECT * FROM " + table_name + " WHERE ID = ?";
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            pstmt.setInt(1, id);

            // Execute the select statement and retrieve the results
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                byte[] domainBytes = rs.getBytes("Domain");
                DomainType domain;
                if (!simplex) {
                    domain = Domain.toDomain(domainBytes, dimension, storedPoints);
                } else {
                    domain = DomainSimplex.toDomain(domainBytes, dimension, storedPoints);
                }
                byte[] functionBytes = rs.getBytes("LinearFunction");
                Function function = Function.toFunction(functionBytes);
                int intersectionIndex = rs.getInt("IntersectionIndex");
                int parentID = rs.getInt("ParentID");
                int leftID = rs.getInt("LeftID");
                int rightID = rs.getInt("RightID");

                nodeRecord = new NodeRecord(domain, function, intersectionIndex, parentID, leftID, rightID);
                nodeRecord.ID = id;
            }

            // Clean up resources
            pstmt.close();

//            System.out.println("Record selected successfully");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nodeRecord;
    }

    public static NodeRecord updateRecord(int recordId, int newLeftId, int newRightId, DomainType newDomain,
                                          boolean simplex, int dimension, String table_name, boolean storedPoints) {
        NodeRecord updatedRecord = null;

        try {
            Connection connection = DriverManager.getConnection(MYSQLConstants.MYSQL_URL, MYSQLConstants.MYSQL_USER, MYSQLConstants.MYSQL_PASSWORD);
            Statement stmt = connection.createStatement();
            String sql = "use i_tree";
            stmt.executeUpdate(sql);

            String updateSql = "UPDATE " + table_name + " SET LeftID = ?, rightID = ? WHERE ID = ?, DOMAIN = ?";
            PreparedStatement pstmt = connection.prepareStatement(updateSql);
            pstmt.setInt(1, newLeftId);
            pstmt.setInt(2, newRightId);
            pstmt.setInt(3, recordId);;
            pstmt.setBytes(4, newDomain.toByte(dimension, storedPoints));

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
//                System.out.println("Record with ID " + recordId + " updated successfully");
                updatedRecord = getRecordById(recordId, simplex, dimension, table_name, storedPoints);
            } else {
                System.out.println("No record found with ID " + recordId);
            }

            pstmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return updatedRecord;
    }
}
