package com.company.running;

/**
 * Data stored in TreeNode used for its construction.
 */
public interface NodeData {
    byte[] toByte(int dimension, boolean storePoints);

    static NodeData toData(byte[] bytes, int dimension, boolean storedPoints) {
        return null;
    }
}
