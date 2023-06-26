package com.company.running;

import java.io.IOException;
import java.sql.SQLException;

// TODO: TECH DEBT REFACTOR
//  * Parametric Equation Solution
//    * Point
//    * Segment
//    * Partition
//    * ParametricNodeData
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
