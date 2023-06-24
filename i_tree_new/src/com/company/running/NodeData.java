package com.company.running;

/**
 * Data stored in TreeNode used for its construction, but not needed after that.
 */
public interface NodeData {
    int getDimension();
    byte[] toByte();

    NodeData toData(byte[] bytes);
}
