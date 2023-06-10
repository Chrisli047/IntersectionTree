package com.company.running;

import java.io.*;
import java.util.ArrayList;

public class Test {
    // Only includes broad Simplex tests.
    // For smaller (unfinished) tests go to ArchivedTests.java.
    public static void runTests() {
        System.out.println("Running tests. Will print test name and if fails will print failure and problem.");

        miniOriginLineTest();
        originLineTest();
        treePathTest();
    }

    // Full tree construction test for Simplex-based solutions:
    // Given n lines going through the origin with a positive finite slope there should be n+1 subdomains = 2n+1 nodes
    public static void miniOriginLineTest() {
        System.out.println("Mini Origin Line Test:");

        final int EXPECTED_NUM_NODES = 3;

        ArrayList<double[]> domainBoundaryInequalities = generateDomainBoundary(2, 1);
        Function[] functions = new Function[3];
        functions[0] = new Function(new double[]{0.5, -1, 0});
        functions[1] = new Function(new double[]{1, -1, 0});
        functions[2] = new Function(new double[]{2, -1, 0});

        // Modifications for Simplex:
        double[] function_values = new double[3];
        function_values[function_values.length - 1] = 1;
        Function function = new Function(function_values);
        DomainSimplex d = new DomainSimplex(function, true, null, null, null);
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
                SimplexType.SIMPLEX, 2, "MiniOriginLineTestSimplex");
        if (numNodesSimplex != EXPECTED_NUM_NODES) {
            System.out.println("FAILED");
            System.out.println("Num nodes Simplex should be " + EXPECTED_NUM_NODES + ", but is " + numNodesSimplex);
        }

        // Sign-Changing Simplex
        int numNodesSignChangingSimplex = Tree.constructTreeSimplex(functions, d, constraintCoefficients,
                constraintConstants, SimplexType.SIGN_CHANGING_SIMPLEX, 2,
                "MiniOriginLineTestSignChangingSimplex");
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
    public static void originLineTest() {
        System.out.println("Origin Line Test:");

        final int NUM_INEQUALITY = 5;
        final int NUM_DIMENSION = 2;
        final int DOMAIN_BOUNDARY_LENGTH = 1;

        final int EXPECTED_NUM_NODES = 2 * NUM_INEQUALITY + 1;

        ArrayList<double[]> domainBoundaryInequalities = generateDomainBoundary(NUM_DIMENSION, DOMAIN_BOUNDARY_LENGTH);
        Function[] functions = new Function[NUM_INEQUALITY];
        for (int j = 0; j < functions.length; j++) {
            functions[j] = new Function(generate_equation_origin(NUM_DIMENSION));
        }

        // Modifications for Simplex:
        double[] function_values = new double[NUM_DIMENSION + 1];
        function_values[function_values.length - 1] = 1;
        Function function = new Function(function_values);
        DomainSimplex d = new DomainSimplex(function, true, null, null, null);
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

    // ***************
    // DATA COLLECTION
    // ***************

    // Does not include boundary inequalities
    final static int num_inequality_default = 50;
    final static int num_dimension_default = 5;
    final static int domain_boundary_length_default = 1;

    // Number of runs per variable
    final static int unique_runs = 10;
    final static int repeat_runs = 10;

    public static void collectData() throws IOException {
        //  TODO: Data Gathering for 2 Point Memorization Versions
        int[] table_counter = new int[]{0};
        collect_data_individual_feasibility_checks();
        collect_data_tree_path(table_counter);
        collect_data_tree_construction(table_counter);
    }

    public static void collect_data_individual_feasibility_checks() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("individual_feasibility_checks.txt"));

        writer.write("Variable #Inequalities\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_inequality = 3; num_inequality <= 100; num_inequality++) {
            writer.write(num_inequality + ",");
            time_individual_feasibility_checks(num_dimension_default, num_inequality, domain_boundary_length_default,
                    writer);
        }

        writer.write("\nVariable #Dimensions\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_dimension = 2; num_dimension <= 10; num_dimension++) {
            writer.write(num_dimension + ",");
            time_individual_feasibility_checks(num_dimension, num_inequality_default, domain_boundary_length_default,
                    writer);
        }

        writer.write("\nVariable Domain Boundary Length\n");
        writer.write("Domain Boundary Length,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int domain_boundary_length = 1; domain_boundary_length <= 10; domain_boundary_length++) {
            writer.write(domain_boundary_length + ",");
            time_individual_feasibility_checks(num_dimension_default, num_inequality_default, domain_boundary_length,
                    writer);
        }

        writer.close();
    }

    public static void collect_data_tree_path(int[] table_counter) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("tree_path.txt"));

        writer.write("Variable #Inequalities\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_inequality = 1; num_inequality <= 100; num_inequality++) {
            writer.write(num_inequality + ",");
            time_tree_path(num_dimension_default, num_inequality, domain_boundary_length_default, writer, table_counter);
        }

        writer.write("\nVariable #Dimensions\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_dimension = 2; num_dimension <= 10; num_dimension++) {
            writer.write(num_dimension + ",");
            time_tree_path(num_dimension, num_inequality_default, domain_boundary_length_default, writer, table_counter);
        }

        writer.write("\nVariable Domain Boundary Length\n");
        writer.write("Domain Boundary Length,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int domain_boundary_length = 1; domain_boundary_length <= 10; domain_boundary_length++) {
            writer.write(domain_boundary_length + ",");
            time_tree_path(num_dimension_default, num_inequality_default, domain_boundary_length, writer, table_counter);
        }

        writer.close();
    }

    public static void collect_data_tree_construction(int[] table_counter)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("tree_construction.txt"));

        writer.write("Variable #Inequalities\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_inequality = 1; num_inequality <= 100; num_inequality++) {
            writer.write(num_inequality + ",");
            time_tree_construction(num_dimension_default, num_inequality, domain_boundary_length_default, writer,
                    table_counter);
        }

        writer.write("\nVariable #Dimensions\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_dimension = 2; num_dimension <= 10; num_dimension++) {
            writer.write(num_dimension + ",");
            time_tree_construction(num_dimension, num_inequality_default, domain_boundary_length_default, writer,
                    table_counter);
        }

        writer.write("\nVariable Domain Boundary Length\n");
        writer.write("Domain Boundary Length,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int domain_boundary_length = 1; domain_boundary_length <= 10; domain_boundary_length++) {
            writer.write(domain_boundary_length + ",");
            time_tree_construction(num_dimension_default, num_inequality_default, domain_boundary_length, writer,
                    table_counter);
        }

        writer.close();
    }

    private static void time_individual_feasibility_checks(int num_dimension, int num_inequality,
                                                           int domain_boundary_length, BufferedWriter writer)
            throws IOException {
        long average_time_simplex = 0;
        long average_time_sign_changing_simplex = 0;
        long average_time_parametric_equation = 0;
        long average_time_repeat, start_time, stop_time;

        for (int i = 0; i < unique_runs; i++) {
            // Equations defining subdomain
            ArrayList<double[]> inequalities = generate_inequalities(num_inequality, num_dimension, domain_boundary_length);
            // Equation of line for feasibility checking. Hijacks generate_inequalities().
            Function function = new Function(generate_equation(num_dimension));

            // Uncomment if slack variables required
//            // Modifications for Simplex:
//            // Introduce slack variables
//            ArrayList<double[]> constraintCoefficients = new ArrayList<>();
//            // Separate constraint constants
//            ArrayList<Double> constraintConstants = new ArrayList<>();
//            for (double[] inequality : inequalities) {
//                // ignore constant at the end
//                double[] slackenedEquation = new double[inequality.length * 2 - 2];
//                for (int j = 0; j < inequality.length - 1; j++) {
//                    slackenedEquation[j * 2] = inequality[j];
//                    slackenedEquation[j * 2 + 1] = -inequality[j];
//                }
//                constraintConstants.add(inequality[inequality.length - 1]);
//                constraintCoefficients.add(slackenedEquation);
//            }

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

            // Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                DomainSimplex.ifPartitionsDomain(new ArrayList<>(constraintCoefficients),
                        new ArrayList<>(constraintConstants), function,
                        SimplexType.SIMPLEX, num_dimension, null, null, false, false);
                stop_time = System.nanoTime();
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_simplex += average_time_repeat / unique_runs;

            // Sign-Changing Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                DomainSimplex.ifPartitionsDomain(new ArrayList<>(constraintCoefficients),
                        new ArrayList<>(constraintConstants), function,
                        SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension, null, null, false, false);
                stop_time = System.nanoTime();
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_sign_changing_simplex += average_time_repeat / unique_runs;
        }

        writer.write(average_time_simplex + ",");
        writer.write(average_time_sign_changing_simplex + ",");
        writer.write(average_time_parametric_equation + "\n");
    }

    private static void time_tree_path(int num_dimension, int num_inequality, int domain_boundary_length,
                                       BufferedWriter writer, int[] table_counter)
            throws IOException {
        long average_time_simplex = 0;
        long average_time_sign_changing_simplex = 0;
        long average_time_parametric_equation = 0;
        long average_time_repeat, start_time, stop_time;

        for (int i = 0; i < unique_runs; i++) {
            // Equations defining subdomain
            ArrayList<double[]> inequalities = generate_inequalities(0, num_dimension,
                    domain_boundary_length);
            Function[] functions = new Function[num_inequality];
            for (int j = 0; j < functions.length; j++) {
                functions[j] = new Function(generate_equation(num_dimension));
            }

            // Uncomment if slack variables required
//            // Modifications for Simplex:
//            // Introduce slack variables
//            ArrayList<double[]> constraintCoefficients = new ArrayList<>();
//            // Separate constraint constants
//            ArrayList<Double> constraintConstants = new ArrayList<>();
//            for (double[] inequality : inequalities) {
//                // ignore constant at the end
//                double[] slackenedEquation = new double[inequality.length * 2 - 2];
//                for (int j = 0; j < inequality.length - 1; j++) {
//                    slackenedEquation[j * 2] = inequality[j];
//                    slackenedEquation[j * 2 + 1] = -inequality[j];
//                }
//                constraintConstants.add(inequality[inequality.length - 1]);
//                constraintCoefficients.add(slackenedEquation);
//            }

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

            // Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                Tree.constructTreeSegmentSimplex(functions, new ArrayList<>(constraintCoefficients),
                        new ArrayList<>(constraintConstants), SimplexType.SIMPLEX, num_dimension,
                        domain_boundary_length);
                stop_time = System.nanoTime();
                table_counter[0]++;
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_simplex += average_time_repeat / unique_runs;

            // Sign-Changing Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                Tree.constructTreeSegmentSimplex(functions, new ArrayList<>(constraintCoefficients),
                        new ArrayList<>(constraintConstants), SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension,
                        domain_boundary_length);
                stop_time = System.nanoTime();
                table_counter[0]++;
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_sign_changing_simplex += average_time_repeat / unique_runs;
        }

        writer.write(average_time_simplex + ",");
        writer.write(average_time_sign_changing_simplex + ",");
        writer.write(average_time_parametric_equation + "\n");
    }

    private static void time_tree_construction(int num_dimension, int num_inequality, int domain_boundary_length,
                                               BufferedWriter writer, int[] table_counter)
            throws IOException {
            long average_time_simplex = 0;
            long average_time_sign_changing_simplex = 0;
            long average_time_parametric_equation = 0;
            long average_time_repeat, start_time, stop_time;

            for (int i = 0; i < unique_runs; i++) {
                // Equations defining subdomain
                ArrayList<double[]> inequalities = generate_inequalities(0, num_dimension,
                        domain_boundary_length);
                Function[] functions = new Function[num_inequality];
                for (int j = 0; j < functions.length; j++) {
                    functions[j] = new Function(generate_equation(num_dimension));
                }


                // Uncomment if slack variables required
//            // Modifications for Simplex:
//            // Introduce slack variables
//            ArrayList<double[]> constraintCoefficients = new ArrayList<>();
//            // Separate constraint constants
//            ArrayList<Double> constraintConstants = new ArrayList<>();
//            for (double[] inequality : inequalities) {
//                // ignore constant at the end
//                double[] slackenedEquation = new double[inequality.length * 2 - 2];
//                for (int j = 0; j < inequality.length - 1; j++) {
//                    slackenedEquation[j * 2] = inequality[j];
//                    slackenedEquation[j * 2 + 1] = -inequality[j];
//                }
//                constraintConstants.add(inequality[inequality.length - 1]);
//                constraintCoefficients.add(slackenedEquation);
//            }

                // Modifications for Simplex:
                double[] function_values = new double[num_dimension + 1];
                function_values[function_values.length - 1] = 1;
                Function function = new Function(function_values);
                DomainSimplex d = new DomainSimplex(function, true, null, null, null);
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

                // Simplex
                average_time_repeat = 0;
                for (int j = 0; j < repeat_runs; j++) {
                    start_time = System.nanoTime();
                    Tree.constructTreeSimplex(functions, d, new ArrayList<>(constraintCoefficients),
                            new ArrayList<>(constraintConstants), SimplexType.SIMPLEX, num_dimension,
                            "IntersectionTree" + table_counter[0]);
                    stop_time = System.nanoTime();
                    table_counter[0]++;
                    average_time_repeat += (stop_time - start_time) / repeat_runs;
                }
                average_time_simplex += average_time_repeat / unique_runs;

                // Sign-Changing Simplex
                average_time_repeat = 0;
                for (int j = 0; j < repeat_runs; j++) {
                    start_time = System.nanoTime();
                    Tree.constructTreeSimplex(functions, d, new ArrayList<>(constraintCoefficients),
                            new ArrayList<>(constraintConstants), SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension,
                            "IntersectionTree" + table_counter[0]);
                    stop_time = System.nanoTime();
                    table_counter[0]++;
                    average_time_repeat += (stop_time - start_time) / repeat_runs;
                }
                average_time_sign_changing_simplex += average_time_repeat / unique_runs;
            }

            writer.write(average_time_simplex + ",");
            writer.write(average_time_sign_changing_simplex + ",");
            writer.write(average_time_parametric_equation + "\n");
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
    private static double[] generate_equation(int num_dimension) {
        double[] equation = new double[num_dimension + 1];
        for (int i = 0; i < equation.length - 1; i++) {
            equation[i] = Math.random() * 2 - 1;
        }
        equation[equation.length - 1] = Math.random() - 0.5;
        return equation;
    }

    // Generated equations are in the form: ax1 + bx2 + cx3 + ... = d where double[] = {a, b, c, d}.
    private static ArrayList<double[]> generate_inequalities(int num_inequality, int num_dimension,
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

    public static void parseDataFiles() {
        // TODO: parse data files for new data file format with 2 point memorization techniques included
        try {
            File individual_inequality_naive = new File("Data_Individual_Partition_Variable_Inequalities_Simplex.txt");
            individual_inequality_naive.delete();
            File individual_inequality_sign_changing = new File("Data_Individual_Partition_Variable_Inequalities_Sign_Changing_Simplex.txt");
            individual_inequality_sign_changing.createNewFile();
            File individual_dimension_naive = new File("Data_Individual_Partition_Variable_Dimensions_Simplex.txt");
            individual_inequality_naive.createNewFile();
            File individual_dimension_sign_changing = new File("Data_Individual_Partition_Variable_Dimensions_Sign_Changing_Simplex.txt");
            individual_inequality_sign_changing.createNewFile();
            File individual_domain_naive = new File("Data_Individual_Partition_Variable_Domain_Simplex.txt");
            individual_inequality_naive.createNewFile();
            File individual_domain_sign_changing = new File("Data_Individual_Partition_Variable_Domain_Sign_Changing_Simplex.txt");
            individual_inequality_sign_changing.createNewFile();

            File inputFile = new File("individual_feasibility_checks.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            int lineCount = 1;

            while (line != null && lineCount < 3) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 100) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(individual_inequality_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(individual_inequality_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 104) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 112) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(individual_dimension_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(individual_dimension_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 116) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 125) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(individual_domain_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(individual_domain_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File path_inequality_naive = new File("Data_Tree_Path_Variable_Inequalities_Simplex.txt");
            path_inequality_naive.createNewFile();
            File path_inequality_sign_changing = new File("Data_Tree_Path_Variable_Inequalities_Sign_Changing_Simplex.txt");
            path_inequality_sign_changing.createNewFile();
            File path_dimension_naive = new File("Data_Tree_Path_Variable_Dimensions_Simplex.txt");
            path_inequality_naive.createNewFile();
            File path_dimension_sign_changing = new File("Data_Tree_Path_Variable_Dimensions_Sign_Changing_Simplex.txt");
            path_inequality_sign_changing.createNewFile();
            File path_domain_naive = new File("Data_Tree_Path_Variable_Domain_Simplex.txt");
            path_inequality_naive.createNewFile();
            File path_domain_sign_changing = new File("Data_Tree_Path_Variable_Domain_Sign_Changing_Simplex.txt");
            path_inequality_sign_changing.createNewFile();

            File inputFile = new File("tree_path.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            int lineCount = 1;

            while (line != null && lineCount < 3) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 102) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(path_inequality_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(path_inequality_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 106) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 114) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(path_dimension_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(path_dimension_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 118) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 127) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(path_domain_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(path_domain_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File full_inequality_naive = new File("Data_Full_Tree_Variable_Inequalities_Simplex.txt");
            full_inequality_naive.createNewFile();
            File full_inequality_sign_changing = new File("Data_Full_Tree_Variable_Inequalities_Sign_Changing_Simplex.txt");
            full_inequality_sign_changing.createNewFile();
            File full_dimension_naive = new File("Data_Full_Tree_Variable_Dimensions_Simplex.txt");
            full_inequality_naive.createNewFile();
            File full_dimension_sign_changing = new File("Data_Full_Tree_Variable_Dimensions_Sign_Changing_Simplex.txt");
            full_inequality_sign_changing.createNewFile();
            File full_domain_naive = new File("Data_Full_Tree_Variable_Domain_Simplex.txt");
            full_inequality_naive.createNewFile();
            File full_domain_sign_changing = new File("Data_Full_Tree_Variable_Domain_Sign_Changing_Simplex.txt");
            full_inequality_sign_changing.createNewFile();

            File inputFile = new File("tree_construction.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            int lineCount = 1;

            while (line != null && lineCount < 3) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 102) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(full_inequality_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(full_inequality_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 106) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 114) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(full_dimension_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(full_dimension_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 118) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 127) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(full_domain_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(full_domain_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
