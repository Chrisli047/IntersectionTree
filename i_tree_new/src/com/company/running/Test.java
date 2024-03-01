package com.company.running;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class Test {
    /**
     * Prepare and run tests.
     */
    public static void main(String[] args) throws SQLException {
        try {
            MySQL.setupMySQL();
            Test.runTests();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            MySQL.cleanupMySQL();
        }
    }

    /**
     * Run tests.
     */
    public static void runTests() throws SQLException {
        System.out.println("Running tests...");

        miniOriginLineTest1();
        miniOriginLineTest2();
        originLineTest();
        treePathTest();
    }

    // Full tree construction test for Simplex-based solutions:
    // Given n lines going through the origin with a positive finite slope there should be n+1 subdomains = 2n+1 nodes
    public static void miniOriginLineTest1() throws SQLException {
        System.out.println("Mini Origin Line Test 1:");

        final int EXPECTED_NUM_NODES = 3;

        final int DIMENSION = 2;

        ArrayList<double[]> domainBoundaryInequalities = generateDomainBoundary(DIMENSION, 1);
        Function[] functions = new Function[3];
        functions[0] = new Function(new double[]{0.5, -1, 0});
        functions[1] = new Function(new double[]{1, -1, 0});
        functions[2] = new Function(new double[]{2, -1, 0});

        // Modifications for Simplex:
        NodeData d = new NodeData(-1, null, null, null, DIMENSION);
        ArrayList<double[]> constraintCoefficients = new ArrayList<>();
        // Separate constraint constants
        ArrayList<Double> constraintConstants = new ArrayList<>();
        for (double[] inequality : domainBoundaryInequalities) {
            // ignore constant at the end
            double[] slackenedEquation = new double[inequality.length - 1];
            System.arraycopy(inequality, 0, slackenedEquation, 0, inequality.length - 1);
            constraintConstants.add(inequality[inequality.length - 1]);
            constraintCoefficients.add(slackenedEquation);
        }

        // Simplex
        int numNodesSimplex = Tree.constructTreeSimplex(functions, d, constraintCoefficients, constraintConstants,
                SimplexType.SIMPLEX, 2, "MiniOriginLineTest1Simplex");
        if (numNodesSimplex != EXPECTED_NUM_NODES) {
            System.out.println("FAILED");
            System.out.println("Num nodes Simplex should be " + EXPECTED_NUM_NODES + ", but is " + numNodesSimplex);
        }

        // Sign-Changing Simplex
        int numNodesSignChangingSimplex = Tree.constructTreeSimplex(functions, d, constraintCoefficients,
                constraintConstants, SimplexType.SIGN_CHANGING_SIMPLEX, 2,
                "MiniOriginLineTest1SignChangingSimplex");
        if (numNodesSignChangingSimplex != EXPECTED_NUM_NODES) {
            System.out.println("FAILED");
            System.out.println("Num nodes SignChangingSimplex should be " + EXPECTED_NUM_NODES + ", but is " +
                    numNodesSignChangingSimplex);
        }

//        // Sign-Changing Permanent Point Memorization Simplex
//        int numNodesPermanentPointMemorization = Tree.constructTreeSimplex(functions, d, constraintCoefficients,
//                constraintConstants, SimplexType.POINT_REMEMBERING_PERMANENT_SIGN_CHANGING_SIMPLEX, NUM_DIMENSION,
//                "OriginLineTestPermanentPointMemorization");
//        if (numNodesPermanentPointMemorization != EXPECTED_NUM_NODES) {
//            throw new IllegalStateException("Num nodes Permanent Point Memorization should be " + EXPECTED_NUM_NODES +
//                    ", but is " + numNodesPermanentPointMemorization);
//        }
//
//        // Sign-Changing Local Point Memorization Simplex
//        int numNodesLocalPointMemorization = Tree.constructTreeSimplex(functions, d, constraintCoefficients,
//                constraintConstants, SimplexType.POINT_REMEMBERING_LOCAL_SIGN_CHANGING_SIMPLEX, NUM_DIMENSION,
//                "OriginLineTestLocalPointMemorization");
//        if (numNodesLocalPointMemorization != EXPECTED_NUM_NODES) {
//            throw new IllegalStateException("Num nodes Local Point Memorization should be " + EXPECTED_NUM_NODES +
//                    ", but is " + numNodesLocalPointMemorization);
//        }
    }

    // Full tree construction test for Simplex-based solutions:
    // Given n lines going through the origin with a positive finite slope there should be n+1 subdomains = 2n+1 nodes
    public static void miniOriginLineTest2() throws SQLException {
        System.out.println("Mini Origin Line Test 2:");

        final int EXPECTED_NUM_NODES = 3;
        final int DIMENSION = 2;

        ArrayList<double[]> domainBoundaryInequalities = generateDomainBoundary(DIMENSION, 1);
        Function[] functions = new Function[3];
        functions[0] = new Function(new double[]{1, -1, 0});
        functions[1] = new Function(new double[]{0.5, -1, 0});
        functions[2] = new Function(new double[]{2, -1, 0});

        // Modifications for Simplex:
        NodeData d = new NodeData(-1, null, null, null, DIMENSION);
        ArrayList<double[]> constraintCoefficients = new ArrayList<>();
        // Separate constraint constants
        ArrayList<Double> constraintConstants = new ArrayList<>();
        for (double[] inequality : domainBoundaryInequalities) {
            // ignore constant at the end
            double[] slackenedEquation = new double[inequality.length - 1];
            System.arraycopy(inequality, 0, slackenedEquation, 0, inequality.length - 1);
            constraintConstants.add(inequality[inequality.length - 1]);
            constraintCoefficients.add(slackenedEquation);
        }

        // Simplex
        int numNodesSimplex = Tree.constructTreeSimplex(functions, d, constraintCoefficients, constraintConstants,
                SimplexType.SIMPLEX, 2, "MiniOriginLineTest2Simplex");
        if (numNodesSimplex != EXPECTED_NUM_NODES) {
            System.out.println("FAILED");
            System.out.println("Num nodes Simplex should be " + EXPECTED_NUM_NODES + ", but is " + numNodesSimplex);
        }

        // Sign-Changing Simplex
        int numNodesSignChangingSimplex = Tree.constructTreeSimplex(functions, d, constraintCoefficients,
                constraintConstants, SimplexType.SIGN_CHANGING_SIMPLEX, 2,
                "MiniOriginLineTest2SignChangingSimplex");
        if (numNodesSignChangingSimplex != EXPECTED_NUM_NODES) {
            System.out.println("FAILED");
            System.out.println("Num nodes SignChangingSimplex should be " + EXPECTED_NUM_NODES + ", but is " +
                    numNodesSignChangingSimplex);
        }

//        // Sign-Changing Permanent Point Memorization Simplex
//        int numNodesPermanentPointMemorization = Tree.constructTreeSimplex(functions, d, constraintCoefficients,
//                constraintConstants, SimplexType.POINT_REMEMBERING_PERMANENT_SIGN_CHANGING_SIMPLEX, NUM_DIMENSION,
//                "OriginLineTestPermanentPointMemorization");
//        if (numNodesPermanentPointMemorization != EXPECTED_NUM_NODES) {
//            throw new IllegalStateException("Num nodes Permanent Point Memorization should be " + EXPECTED_NUM_NODES +
//                    ", but is " + numNodesPermanentPointMemorization);
//        }
//
//        // Sign-Changing Local Point Memorization Simplex
//        int numNodesLocalPointMemorization = Tree.constructTreeSimplex(functions, d, constraintCoefficients,
//                constraintConstants, SimplexType.POINT_REMEMBERING_LOCAL_SIGN_CHANGING_SIMPLEX, NUM_DIMENSION,
//                "OriginLineTestLocalPointMemorization");
//        if (numNodesLocalPointMemorization != EXPECTED_NUM_NODES) {
//            throw new IllegalStateException("Num nodes Local Point Memorization should be " + EXPECTED_NUM_NODES +
//                    ", but is " + numNodesLocalPointMemorization);
//        }
    }

    // Full tree construction test for Simplex-based solutions:
    // Given n lines going through the origin with a positive finite slope there should be n+1 subdomains = 2n+1 nodes
    public static void originLineTest() throws SQLException {
        System.out.println("Origin Line Test:");

        final int NUM_INEQUALITY = 5;
        final int NUM_DIMENSION = 2;
        final int DOMAIN_BOUNDARY_LENGTH = 1;

        final int EXPECTED_NUM_NODES = NUM_INEQUALITY;

        ArrayList<double[]> domainBoundaryInequalities = generateDomainBoundary(NUM_DIMENSION, DOMAIN_BOUNDARY_LENGTH);
        Function[] functions = new Function[NUM_INEQUALITY];
        for (int j = 0; j < functions.length; j++) {
            functions[j] = new Function(generate_equation_origin(NUM_DIMENSION));
        }

        // Modifications for Simplex:
        NodeData d = new NodeData(-1, null, null, null, NUM_DIMENSION);
        ArrayList<double[]> constraintCoefficients = new ArrayList<>();
        // Separate constraint constants
        ArrayList<Double> constraintConstants = new ArrayList<>();
        for (double[] inequality : domainBoundaryInequalities) {
            // ignore constant at the end
            double[] slackenedEquation = new double[inequality.length - 1];
            System.arraycopy(inequality, 0, slackenedEquation, 0, inequality.length - 1);
            constraintConstants.add(inequality[inequality.length - 1]);
            constraintCoefficients.add(slackenedEquation);
        }

        // Simplex
        int numNodesSimplex = Tree.constructTreeSimplex(functions, d, constraintCoefficients, constraintConstants,
                SimplexType.SIMPLEX, NUM_DIMENSION, "OriginLineTestSimplex");
        if (numNodesSimplex != EXPECTED_NUM_NODES) {
            System.out.println("FAILED");
            System.out.println("Num nodes Simplex should be " + EXPECTED_NUM_NODES + ", but is " + numNodesSimplex);
        }

        // Sign-Changing Simplex
        int numNodesSignChangingSimplex = Tree.constructTreeSimplex(functions, d, constraintCoefficients,
                constraintConstants, SimplexType.SIGN_CHANGING_SIMPLEX, NUM_DIMENSION,
                "OriginLineTestSignChangingSimplex");
        if (numNodesSignChangingSimplex != EXPECTED_NUM_NODES) {
            System.out.println("FAILED");
            System.out.println("Num nodes SignChangingSimplex should be " + EXPECTED_NUM_NODES + ", but is " +
                    numNodesSignChangingSimplex);
        }

//        // Sign-Changing Permanent Point Memorization Simplex
//        int numNodesPermanentPointMemorization = Tree.constructTreeSimplex(functions, d, constraintCoefficients,
//                constraintConstants, SimplexType.POINT_REMEMBERING_PERMANENT_SIGN_CHANGING_SIMPLEX, NUM_DIMENSION,
//                "OriginLineTestPermanentPointMemorization");
//        if (numNodesPermanentPointMemorization != EXPECTED_NUM_NODES) {
//            throw new IllegalStateException("Num nodes Permanent Point Memorization should be " + EXPECTED_NUM_NODES +
//                    ", but is " + numNodesPermanentPointMemorization);
//        }
//
//        // Sign-Changing Local Point Memorization Simplex
//        int numNodesLocalPointMemorization = Tree.constructTreeSimplex(functions, d, constraintCoefficients,
//                constraintConstants, SimplexType.POINT_REMEMBERING_LOCAL_SIGN_CHANGING_SIMPLEX, NUM_DIMENSION,
//                "OriginLineTestLocalPointMemorization");
//        if (numNodesLocalPointMemorization != EXPECTED_NUM_NODES) {
//            throw new IllegalStateException("Num nodes Local Point Memorization should be " + EXPECTED_NUM_NODES +
//                    ", but is " + numNodesLocalPointMemorization);
//        }
    }

    // Positive finite slope going through origin
    public static ArrayList<double[]> generateDomainBoundary(int num_dimension, int domain_boundary_length) {
        int num_inequality = 0;

        ArrayList<double[]> inequalities = new ArrayList<>();

        // Generate boundaries
        for (int i = 0; i < num_dimension; i++) {
            // 1x ≥ 0 --> -1x ≤ 0
            double[] lower_bound = new double[num_dimension + 1];
            lower_bound[i] = -1;
            // 1x ≤ boundary_length
            double[] upper_bound = new double[num_dimension + 1];
            upper_bound[i] = 1;
            upper_bound[upper_bound.length - 1] = domain_boundary_length;
            inequalities.add(lower_bound);
            inequalities.add(upper_bound);
        }

        for (int i = 0; i < num_inequality; i++) {
            double[] inequality = new double[num_dimension+1];
            // Set a, b, c, ...
            for (int j = 0; j < inequality.length - 1; j++) {
                inequality[j] = Math.random();
            }
            // Set d
            inequality[inequality.length - 1] = 0;
            // Set b = -1
            inequality[1] = -1;

            inequalities.add(inequality);
        }

        return inequalities;
    }

    private static void treePathTest() {
        System.out.println("Tree Path Test:");

        int num_inequality = 5;
        int num_dimension = 2;
        int domain_boundary_length = 1;

        ArrayList<double[]> inequalities = generate_inequalities(0, num_dimension,
                domain_boundary_length);
        Function[] functions = new Function[num_inequality];
        for (int j = 0; j < functions.length; j++) {
            functions[j] = new Function(generate_equation(num_dimension));
        }

        // Modifications for Simplex:
        ArrayList<double[]> constraintCoefficients = new ArrayList<>();
        // Separate constraint constants
        ArrayList<Double> constraintConstants = new ArrayList<>();
        for (double[] inequality : inequalities) {
            // ignore constant at the end
            double[] slackenedEquation = new double[inequality.length - 1];
            System.arraycopy(inequality, 0, slackenedEquation, 0, inequality.length - 1);
            constraintConstants.add(inequality[inequality.length - 1]);
            constraintCoefficients.add(slackenedEquation);
        }

        int numPartitionsSimplex = Tree.constructTreeSegmentSimplex(functions, new ArrayList<>(constraintCoefficients),
                new ArrayList<>(constraintConstants), SimplexType.SIMPLEX, num_dimension, domain_boundary_length);
        int numPartitionsSignChangingSimplex = Tree.constructTreeSegmentSimplex(functions,
                new ArrayList<>(constraintCoefficients), new ArrayList<>(constraintConstants),
                SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension, domain_boundary_length);
//        int numPartitionsLocalPointMemorization = numPartitionsSimplex;
//        int numPartitionsLocalPointMemorization = Tree.constructTreeSegmentSimplex(functions,
//                new ArrayList<>(constraintCoefficients), new ArrayList<>(constraintConstants),
//                SimplexType.POINT_REMEMBERING_LOCAL_SIGN_CHANGING_SIMPLEX, num_dimension, domain_boundary_length);
        if (numPartitionsSimplex != numPartitionsSignChangingSimplex) {
            System.out.println("FAILED");
            System.out.println("Num nodes should be the same, but it is " +
                    numPartitionsSimplex + " for Simplex," +
                    numPartitionsSignChangingSimplex + " for SignChangingSimplex");
        }
    }

    // Positive finite slope going through origin
    private static double[] generate_equation_origin(int num_dimension) {
        double[] equation = new double[num_dimension + 1];
        for (int i = 0; i < equation.length - 1; i++) {
            equation[i] = Math.random();
        }
        equation[equation.length - 1] = 0;
        // Set b = -1
        equation[1] = -1;
        return equation;
    }

    // Generated equation is in the form: ax1 + bx2 + cx3 + ... = d where double[] = {a, b, c, d}.
    static double[] generate_equation(int num_dimension) {
        double[] equation = new double[num_dimension + 1];
        for (int i = 0; i < equation.length - 1; i++) {
            equation[i] = Math.random() * 2 - 1;
        }
        equation[equation.length - 1] = Math.random() - 0.5;
        return equation;
    }

    // Generated equations are in the form: ax1 + bx2 + cx3 + ... = d where double[] = {a, b, c, d}.
    static ArrayList<double[]> generate_inequalities(int num_inequality, int num_dimension,
                                                     int domain_boundary_length) {
        ArrayList<double[]> inequalities = new ArrayList<>();

        // Generate boundaries
        for (int i = 0; i < num_dimension; i++) {
            // 1x ≥ 0 --> -1x ≤ 0
            double[] lower_bound = new double[num_dimension + 1];
            lower_bound[i] = -1;
            // 1x ≤ boundary_length
            double[] upper_bound = new double[num_dimension + 1];
            upper_bound[i] = 1;
            upper_bound[upper_bound.length - 1] = domain_boundary_length;
            inequalities.add(lower_bound);
            inequalities.add(upper_bound);
        }

        // Given the equation ax1 + bx2 + cx3 + ... = d, with positive coefficients and constant, we can prove that the
        // defined line will partition a square (or higher dimensional figure) of a given side length s whose bottom
        // left corner is at the origin if and only if a + b + c + ... > d. We thus randomly generate each coefficient
        // value between -1 and 1 and constant d between -0.5 and 0.5. It becomes extremely likely that the line
        // partitions the domain.
        for (int i = 0; i < num_inequality; i++) {
            double[] inequality = new double[num_dimension+1];
            // Set a, b, c, ...
            for (int j = 0; j < inequality.length - 1; j++) {
                inequality[j] = Math.random() * 2 - 1;
            }
            // Set d
            inequality[inequality.length - 1] = Math.random() - 0.5;

            // The inequalities are very likely to be infeasible (contradictory), so we can force them to all accept one
            // point to ensure there is no contradiction. The center-most point in the domain boundary is likely to
            // result in the most complex subdomain, so we will choose that point.
            double point_value = 0;
            for (int j = 0; j < num_dimension; j++) {
                // point value = boundary_length/2
                point_value += inequality[j] * domain_boundary_length/2;
            }

            // if not legal (legal is ≤), invert inequality
            if (point_value > inequality[num_dimension]) {
                for (int j = 0; j < inequality.length; j++) {
                    inequality[j] *= -1;
                }
            }

            inequalities.add(inequality);
        }

        return inequalities;
    }
}
