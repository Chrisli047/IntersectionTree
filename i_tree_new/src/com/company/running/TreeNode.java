package com.company.running;
import java.sql.*;

import static com.company.running.MYSQLConstants.*;

/**
 * I-Tree node stored in MySQL
 */
public class TreeNode {
    public int ID;
    public int parentID;
    public int leftID;
    public int rightID;
    public DomainType domainType;
    public Function function;
    // TODO: intersection index should be a quality of simplex domain
    public int intersectionIndex;

    public TreeNode(DomainType domainType, Function function) {
        this.domainType = domainType;
        this.function = function;
    }

    public TreeNode(int parentID, DomainType domainType, Function function, int intersectionIndex) {
        this.parentID = parentID;
        this.domainType = domainType;
        this.function = function;
        this.intersectionIndex = intersectionIndex;
    }

    private TreeNode(int ID, int parentID, int leftID, int rightID, DomainType domainType, Function function, int intersectionIndex) {
        this.ID = ID;
        this.parentID = parentID;
        this.leftID = leftID;
        this.rightID = rightID;
        this.domainType = domainType;
        this.function = function;
        this.intersectionIndex = intersectionIndex;
    }

    public static void createTable(String table_name) throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        Statement statement = connection.createStatement();
        String sql = "use i_tree";
        statement.executeUpdate(sql);

        String createTable = "CREATE TABLE " + table_name + " (" +
                "ID INT PRIMARY KEY AUTO_INCREMENT," +
                "ParentID INT," +
                "LeftID INT," +
                "RightID INT," +
                "Domain BLOB," +
                "LinearFunction BLOB," +
                "IntersectionIndex INT)";
        statement.executeUpdate(createTable);

        connection.close();
    }

    // TODO: storePoints should be a feature of domainType (same for getRecordByID and getRecordByID and updateRecord)
    public int insertToMySql(int dimension, String table_name, boolean storePoints) throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        String sql = "use i_tree";
        statement.executeUpdate(sql);

        String insertSql = "INSERT INTO " + table_name +
                " (ParentID, LeftID, RightID, Domain, LinearFunction, IntersectionIndex) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, -1);
        preparedStatement.setInt(2, -1);
        preparedStatement.setInt(3, -1);
        preparedStatement.setBytes(4, domainType.toByte(dimension, storePoints));
        preparedStatement.setBytes(5, function.toByte(dimension));
        preparedStatement.setInt(6, intersectionIndex);

        preparedStatement.executeUpdate();

        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next();
        int newID = resultSet.getInt(1);

        connection.commit();
        preparedStatement.close();
        connection.close();

        return newID;
    }

    // TODO: don't be static
    // TODO: don't pass simplex boolean
    public static TreeNode getRecordByID(int ID, boolean simplex, int dimension, String table_name, boolean storedPoints)
            throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        Statement statement = connection.createStatement();
        String sql = "use i_tree";
        statement.executeUpdate(sql);

        String selectSql = "SELECT * FROM " + table_name + " WHERE ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
        preparedStatement.setInt(1, ID);

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        int parentID = resultSet.getInt("ParentID");
        int leftID = resultSet.getInt("LeftID");
        int rightID = resultSet.getInt("RightID");

        byte[] domainBytes = resultSet.getBytes("Domain");
        // TODO: use domainType so all cases are the same
        DomainType domain = simplex ? DomainSimplex.toDomain(domainBytes, dimension, storedPoints) :
                Domain.toDomain(domainBytes, dimension, storedPoints);

        byte[] functionBytes = resultSet.getBytes("LinearFunction");
        Function function = Function.toFunction(functionBytes);

        int intersectionIndex = resultSet.getInt("IntersectionIndex");

        TreeNode treeNode = new TreeNode(ID, parentID, leftID, rightID, domain, function, intersectionIndex);

        preparedStatement.close();
        connection.close();

        return treeNode;
    }

    // TODO: don't be static
    public static void updateRecord(int recordID, int newLeftID, int newRightID, DomainType newDomain,
                                    int dimension, String table_name, boolean storedPoints)
            throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        Statement statement = connection.createStatement();
        String sql = "use i_tree";
        statement.executeUpdate(sql);

        String updateSql = "UPDATE " + table_name + " SET LeftID = ?, RightID = ?, DOMAIN = ? WHERE ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateSql);

        preparedStatement.setInt(1, newLeftID);
        preparedStatement.setInt(2, newRightID);
        preparedStatement.setBytes(3, newDomain.toByte(dimension, storedPoints));
        preparedStatement.setInt(4, recordID);

        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }
}
