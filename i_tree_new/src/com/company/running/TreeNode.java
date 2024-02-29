package com.company.running;

import java.sql.*;

import static com.company.running.MySQL.*;

/**
 * I-Tree node stored in MySQL.
 */
public class TreeNode {
    private final String tableName;
    private final int ID;
    private final int parentID;
    private int leftID;
    private int rightID;
    private final NodeData nodeData;
    private final Function function;

    public int getID() {return ID;}
    public int getRightID() {return rightID;}
    public NodeData getNodeData() {return nodeData;}
    public Function getFunction() {return function;}

    /**
     * Always couple this with updating the parent node's respective child ID unless this is the root node.
     */
    public TreeNode(String tableName, int parentID, NodeData nodeData, Function function) throws SQLException {
        this.tableName = tableName;

        this.parentID = parentID;
        this.nodeData = nodeData;
        this.function = function;

        this.ID = insertToMySql();
    }

    /**
     * Create existing TreeNode from MySQL.
     */
    private TreeNode(String tableName, int ID, int parentID, int leftID, int rightID, NodeData nodeData, Function function) {
        this.tableName = tableName;
        this.ID = ID;
        this.parentID = parentID;
        this.leftID = leftID;
        this.rightID = rightID;
        this.nodeData = nodeData;
        this.function = function;
    }

    public TreeNode getParentNode() throws SQLException {
        return getRecordByID(parentID);
    }

    public void addLeftChild(int leftID) throws SQLException {
        this.leftID = leftID;

        updateMySQLNode();
    }

    public void addRightChild(int rightID) throws SQLException {
        this.rightID = rightID;

        updateMySQLNode();
    }

    private int insertToMySql() throws SQLException {
        Connection connection = setupConnection();

        String insertSql = "INSERT INTO " + tableName +
                " (ParentID, LeftID, RightID, Domain, LinearFunction) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, parentID);
        preparedStatement.setInt(2, leftID);
        preparedStatement.setInt(3, rightID);
        preparedStatement.setBytes(4, nodeData.toByte());
        preparedStatement.setBytes(5, this.function.toByte());

        preparedStatement.executeUpdate();

        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next();
        int newID = resultSet.getInt(1);

        preparedStatement.close();
        connection.close();

        return newID;
    }

    private TreeNode getRecordByID(int ID) throws SQLException {
        Connection connection = setupConnection();

        String selectSql = "SELECT * FROM " + tableName + " WHERE ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
        preparedStatement.setInt(1, ID);

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        int parentID = resultSet.getInt("ParentID");
        int leftID = resultSet.getInt("LeftID");
        int rightID = resultSet.getInt("RightID");
        NodeData nodeData = this.nodeData.toData(resultSet.getBytes("Domain"));
        Function function = Function.toFunction(resultSet.getBytes("LinearFunction"));

        preparedStatement.close();
        connection.close();

        return new TreeNode(tableName, ID, parentID, leftID, rightID, nodeData, function);
    }

    private void updateMySQLNode() throws SQLException {
        Connection connection = setupConnection();

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
