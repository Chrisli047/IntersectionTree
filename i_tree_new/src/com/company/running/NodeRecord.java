package com.company.running;
import java.sql.*;

public class NodeRecord {
    DomainType d;
    Function f;
    int leftID;
    int rightID;
    int ID = 0;

    public NodeRecord (DomainType d, Function f, int leftID, int rightID) {
        this.d = d;
        this.f = f;
        this.leftID = leftID;
        this.rightID = rightID;
    }

    public static void createTable() {
        try {
            Connection connection = DriverManager.getConnection(Constants.MYSQL_URL, Constants.MYSQL_USER, Constants.MYSQL_PASSWORD);
            Statement stmt = connection.createStatement();
            String sql = "use i_tree";
            stmt.executeUpdate(sql);
            System.out.println("Create tables... ");
            String createTable = "CREATE TABLE IntersectionTree (" +
                    "    ID INT PRIMARY KEY AUTO_INCREMENT,  " +
                    "    Domain BLOB," +
                    "    LinearFunction BLOB," +
                    "    LeftID INT, " +
                    "    rightID INT)";
            stmt.executeUpdate(createTable);
            System.out.println("Table created successfully");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertToMySql() {
        int returnId = 0;
        try {
            Connection connection = DriverManager.getConnection(Constants.MYSQL_URL, Constants.MYSQL_USER, Constants.MYSQL_PASSWORD);
            Statement stmt = connection.createStatement();
            String sql = "use i_tree";
            stmt.executeUpdate(sql);
            System.out.println("Insert record... ");

            connection.setAutoCommit(false);
            // Create a PreparedStatement with the SQL statement for inserting a record
            String insertSql = "INSERT INTO IntersectionTree (Domain, LinearFunction, LeftID, rightID) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

            // Set the values for the parameters in the prepared statement
            pstmt.setBytes(1, d.toByte());
            pstmt.setBytes(2, f.toByte());
            pstmt.setInt(3, -1);
            pstmt.setInt(4, -1);

            // Execute the insert statement
            pstmt.executeUpdate();

            // Get the AUTO_INCREMENT value generated by the insert statement
            ResultSet rs = pstmt.getGeneratedKeys();
            int newId = -1;
            if (rs.next()) {
                newId = rs.getInt(1);
            }
            System.out.println("New record inserted with ID " + newId);

            // Commit the transaction
            connection.commit();

            // Clean up resources
            pstmt.close();

            System.out.println("Record inserted successfully");
            connection.close();
            returnId = newId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnId;
    }

    public static NodeRecord getRecordById(int id, boolean simplex) {
        NodeRecord nodeRecord = null;
        try {
            Connection connection = DriverManager.getConnection(Constants.MYSQL_URL, Constants.MYSQL_USER, Constants.MYSQL_PASSWORD);
            Statement stmt = connection.createStatement();
            String sql = "use i_tree";
            stmt.executeUpdate(sql);
            System.out.println("Select record...");

            // Create a PreparedStatement with the SQL statement for selecting a record based on its ID
            String selectSql = "SELECT * FROM IntersectionTree WHERE ID = ?";
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            pstmt.setInt(1, id);

            // Execute the select statement and retrieve the results
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                byte[] domainBytes = rs.getBytes("Domain");
                DomainType domain;
                if (!simplex) {
                    domain = Domain.toDomain(domainBytes);
                } else {
                    domain = DomainSignChangingSimplex.toDomain(domainBytes);
                }
                byte[] functionBytes = rs.getBytes("LinearFunction");
                Function function = Function.toFunction(functionBytes);
                int leftID = rs.getInt("LeftID");
                int rightID = rs.getInt("RightID");

                nodeRecord = new NodeRecord(domain, function, leftID, rightID);
                nodeRecord.ID = id;
            }

            // Clean up resources
            pstmt.close();

            System.out.println("Record selected successfully");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nodeRecord;
    }

    public static NodeRecord updateRecord(int recordId, int newLeftId, int newRightId, boolean simplex) {
        NodeRecord updatedRecord = null;

        try {
            Connection connection = DriverManager.getConnection(Constants.MYSQL_URL, Constants.MYSQL_USER, Constants.MYSQL_PASSWORD);
            Statement stmt = connection.createStatement();
            String sql = "use i_tree";
            stmt.executeUpdate(sql);

            String updateSql = "UPDATE IntersectionTree SET LeftID = ?, rightID = ? WHERE ID = ?";
            PreparedStatement pstmt = connection.prepareStatement(updateSql);
            pstmt.setInt(1, newLeftId);
            pstmt.setInt(2, newRightId);
            pstmt.setInt(3, recordId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Record with ID " + recordId + " updated successfully");
                updatedRecord = getRecordById(recordId, simplex);
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
