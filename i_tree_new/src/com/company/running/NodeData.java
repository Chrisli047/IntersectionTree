package com.company.running;

// TODO: TECH DEBT REFACTOR
/**
 * Data stored in TreeNode used for its construction, but not needed after that.
 */
public interface NodeData {
    // TODO: dimension is tree property, not a node property
    int getDimension();
    byte[] toByte();

    NodeData toData(byte[] bytes);
}
