package com.company.running;
import java.sql.*;

import static com.company.running.Constants.*;

// TODO: Change readme to instruct users to use files (add to project structure -> modules in IntelliJ) in setup_files to run code
// TODO: change set 2: do not expose anything other than necessary constructors (as few as possible) and single group setter
//  * doc comment public
// TODO: change set 3: misc refactoring changes

/**
 * I-Tree node stored in MySQL.
 */
public class TreeNode {
    private int ID;
    public int getID() {return ID;}
    private int parentID;
    public int getParentID() {return parentID;}
    private int leftID;
    public int getLeftID() {return leftID;}
    private int rightID;
    public int getRightID() {return rightID;}
    private NodeData nodeData;
    public NodeData getNodeData() {return nodeData;}
    private Function function;
    public Function getFunction() {return function;}

    // TODO: combine constructors: use parentID, keep intersectionIndex in domainType
    public TreeNode(NodeData nodeData, Function function) {
        this.nodeData = nodeData;
        this.function = function;
    }

    public TreeNode(int parentID, NodeData nodeData, Function function) {
        this.parentID = parentID;
        this.nodeData = nodeData;
        this.function = function;
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

    // TODO: move to separate MySQL file alongside MySQL constants
    private static String tableName;

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

    // TODO: storePoints should be a feature of domainType (same for getRecordByID and getRecordByID and updateRecord)
    // TODO: this should be done in constructor privately
    public int insertToMySql(int dimension, String tableName, boolean storePoints) throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        String sql = "use i_tree";
        statement.executeUpdate(sql);

        String insertSql = "INSERT INTO " + tableName +
                " (ParentID, LeftID, RightID, Domain, LinearFunction) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

        // TODO: should use our values for these not hardcoded -1, but ensure those values are indeed -1
        preparedStatement.setInt(1, -1);
        preparedStatement.setInt(2, -1);
        preparedStatement.setInt(3, -1);
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

    // TODO: don't be static
    // TODO: don't pass simplex boolean
    public static TreeNode getRecordByID(int ID, boolean simplex, int dimension, String tableName, boolean storedPoints)
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
        // TODO: use domainType so all cases are the same
        NodeData domain = simplex ? SimplexNodeData.toData(domainBytes) : ParametricNodeData.toData(domainBytes);
        byte[] functionBytes = resultSet.getBytes("LinearFunction");
        Function function = Function.toFunction(functionBytes);

        preparedStatement.close();
        connection.close();

        return new TreeNode(ID, parentID, leftID, rightID, domain, function);
    }

    public void updateNode(int newLeftID, int newRightID, NodeData newNodeData) throws SQLException {
        leftID = newLeftID;
        rightID = newRightID;
        nodeData = newNodeData;

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
