package com.company.running;

import java.io.IOException;
import java.sql.SQLException;




// TODO: CURRENT:
//   * archiving parametric solution (done)
//   * SimplexNodeData -> NodeData, TreeNode only uses the one only type of NodeData







// TODO: TECH DEBT REFACTOR
//  * Simplex Solution
//    * SimplexMarker
//    * SimplexType
//    * TwoPhaseSimplex
//    * TwoPhaseSignChangingSimplex
//    * TwoPhaseSignChangingPointMemorizingSimplex
//    * SimplexNodeData
//  * Tree
//  * Test
//  * Main
//  * Misc Remaining
public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        // TODO: migrate files into folders
        Test.runTests();

//        Test.collectData();
//        Test.parseDataFiles();
    }
}
