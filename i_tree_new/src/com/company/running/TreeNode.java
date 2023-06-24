package com.company.running;

import java.sql.*;

import static com.company.running.Constants.*;

// TODO: review file
// TODO: add doc comments

/**
 * I-Tree node stored in MySQL.
 */
public class TreeNode {
    private final int ID;
    public int getID() {return ID;}
    private final int parentID;
    private int leftID;
    private int rightID;
    public int getRightID() {return rightID;}
    private final NodeData nodeData;
    public NodeData getNodeData() {return nodeData;}
    private final Function function;
    public Function getFunction() {return function;}

    // Always couple this with updating parent's left/right unless this is the root
    public TreeNode(int parentID, NodeData nodeData, Function function) throws SQLException {
        this.parentID = parentID;
        this.nodeData = nodeData;
        this.function = function;

        this.ID = insertToMySql();
    }

    /**
     * Create existing TreeNode from MySQL.
     */
    private TreeNode(int ID, int parentID, int leftID, int rightID, NodeData nodeData, Function function) {
        this.ID = ID;
        this.parentID = parentID;
        this.leftID = leftID;
        this.rightID = rightID;
        this.nodeData = nodeData;
        this.function = function;
    }

    private static String tableName;

    // TODO: move to separate MySQL file alongside MySQL constants
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

        TreeNode.tableName = tableName;

        connection.close();
    }

    private int insertToMySql() throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        String sql = "use i_tree";
        statement.executeUpdate(sql);

        String insertSql = "INSERT INTO " + tableName +
                " (ParentID, LeftID, RightID, Domain, LinearFunction) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, parentID);
        preparedStatement.setInt(2, leftID);
        preparedStatement.setInt(3, rightID);
        preparedStatement.setBytes(4, nodeData.toByte());
        preparedStatement.setBytes(5, function.toByte(nodeData.getDimension()));

        preparedStatement.executeUpdate();

        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next();
        int newID = resultSet.getInt(1);

        connection.commit();
        preparedStatement.close();
        connection.close();

        return newID;
    }

    public TreeNode getParentNode(boolean simplex) throws SQLException {
        return getRecordByID(parentID);
    }

    private TreeNode getRecordByID(int ID)
            throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        Statement statement = connection.createStatement();
        String sql = "use i_tree";
        statement.executeUpdate(sql);

        String selectSql = "SELECT * FROM " + tableName + " WHERE ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
        preparedStatement.setInt(1, ID);

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        int parentID = resultSet.getInt("ParentID");
        int leftID = resultSet.getInt("LeftID");
        int rightID = resultSet.getInt("RightID");
        byte[] domainBytes = resultSet.getBytes("Domain");
        NodeData domain = nodeData.toData(domainBytes);
        byte[] functionBytes = resultSet.getBytes("LinearFunction");
        Function function = Function.toFunction(functionBytes);

        preparedStatement.close();
        connection.close();

        return new TreeNode(ID, parentID, leftID, rightID, domain, function);
    }

    public void addLeftChild(int leftID) throws SQLException {
        this.leftID = leftID;

        updateMySQLNode();
    }

    public void addRightChild(int rightID) throws SQLException {
        this.rightID = rightID;

        updateMySQLNode();
    }

    private void updateMySQLNode() throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        Statement statement = connection.createStatement();
        String sql = "use i_tree";
        statement.executeUpdate(sql);

        String updateSql = "UPDATE " + tableName + " SET LeftID = ?, RightID = ?, DOMAIN = ? WHERE ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateSql);

        preparedStatement.setInt(1, leftID);
        preparedStatement.setInt(2, rightID);
        preparedStatement.setBytes(3, nodeData.toByte());
        preparedStatement.setInt(4, ID);

        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }
}
