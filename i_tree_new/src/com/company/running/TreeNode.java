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
    // TODO: getters for these, single setter function
    public int ID;
    public int parentID;
    public int leftID;
    public int rightID;
    public NodeData nodeData;
    public Function function;

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

    // TODO: don't be static
    // TODO: changing any features of TreeNode should be done in group and call this function privately
    public static void updateRecord(int recordID, int newLeftID, int newRightID, NodeData newDomain,
                                    int dimension, String tableName, boolean storedPoints)
            throws SQLException {
        Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        Statement statement = connection.createStatement();
        String sql = "use i_tree";
        statement.executeUpdate(sql);

        String updateSql = "UPDATE " + tableName + " SET LeftID = ?, RightID = ?, DOMAIN = ? WHERE ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateSql);

        preparedStatement.setInt(1, newLeftID);
        preparedStatement.setInt(2, newRightID);
        preparedStatement.setBytes(3, newDomain.toByte());
        preparedStatement.setInt(4, recordID);

        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }
}
