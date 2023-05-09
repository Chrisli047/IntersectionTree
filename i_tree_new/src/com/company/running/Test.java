package com.company.running;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void testBuffer() {
        Point p = new Point(new double[]{1.0, 2.0});
        byte[] b = p.toByte();
        Point originalPoint = Point.toPoint(b);
        System.out.println(originalPoint);

        Segment s = new Segment(1, 2);
        byte[] bs = s.toByte();
        Segment originalSegment = Segment.toSegment(bs);
        System.out.println(originalSegment.startPointID);
        System.out.println(originalSegment.endPointID);

        Point[] pA = new Point[] {new Point(new double[]{1.0, 2.0}), new Point(new double[]{2.0, 3.0})};
        Segment[] sA = new Segment[] {new Segment(1, 2), new Segment(2, 3)};
        Domain d = new Domain(pA, sA);
        byte[] bd = d.toByte(2);
        Domain originalDomain = Domain.toDomain(bd, 2);
        originalDomain.printDomain();

        Function f = new Function(new double[]{1.0, 2.0, 3.0});
        byte[] fb = f.toByte(2);
        Function originalFunction = Function.toFunction(fb);
        System.out.println(originalFunction);
    }

    public static void testInsertRecord() {
        Point[] pA = new Point[] {new Point(new double[]{1.0, 2.0}), new Point(new double[]{2.0, 3.0})};
        Segment[] sA = new Segment[] {new Segment(1, 2), new Segment(2, 3)};
        Domain d = new Domain(pA, sA);
        byte[] bd = d.toByte(2);
        Domain originalDomain = Domain.toDomain(bd, 2);
        originalDomain.printDomain();

        Function f = new Function(new double[]{1.0, 2.0, 3.0});
        byte[] fb = f.toByte(2);
        Function originalFunction = Function.toFunction(fb);
        System.out.println(originalFunction);

        NodeRecord nodeRecord = new NodeRecord(d, f, -1, -1);
        nodeRecord.insertToMySql(2, "IntersectionTree");
    }

    public static void testGetRecordById(int id) {
        NodeRecord nodeRecord = NodeRecord.getRecordById(id, false, 2, "IntersectionTree");
        nodeRecord.d.printDomain();
        System.out.println(nodeRecord.f);
        System.out.println(nodeRecord.leftID);
        System.out.println(nodeRecord.rightID);
    }

    public static void testUpdateRecord() {
        NodeRecord nodeRecord = NodeRecord.updateRecord(1, 2,2, false, 2, "IntersectionTree");
        nodeRecord.d.printDomain();
        System.out.println(nodeRecord.f);
        System.out.println(nodeRecord.leftID);
        System.out.println(nodeRecord.rightID);
    }

    public static void testFindCornerPoints() {
        double[][] range = {{0, 10}, {0, 10}};

        List<double[]> res = Domain.findCornerPoints(range);

        for (double[] arr : res) {
            for (double d : arr) {
                System.out.print(d + " ");
            }
            System.out.println();
        }
    }

    public static void testFindBoundaryLines() {
        double[][] range = {{0, 10}, {0, 10}};

        List<double[]> points = Domain.findCornerPoints(range);
        for (double[] arr : points) {
            for (double d : arr) {
                System.out.print(d + " ");
            }
            System.out.println();
        }

        List<int[]> res = Domain.findBoundaryLines(points);

        for (int i = 0; i < res.size(); i++) {
            int[] arr = res.get(i);
            for (int j = 0; j < arr.length; j++) {
                System.out.print(arr[j] + " ");
            }
            System.out.println();
        }

    }

    public static void testConstructTree() throws IOException {

        DataReader dataReader = new DataReader("/Users/xiyaoli/Desktop/Study/research_program/information_element/i_tree_data/data/initDomains/initDomains_d_2.json",
                "/Users/xiyaoli/Desktop/Study/research_program/information_element/i_tree_data/data/input/data_d_2_records_5_initDomainID_1.json");
//                "/Users/maximpopov/Documents/data_d_2_records_5_initDomainID_1.json");        //      Get coefficients
        double[][] coefficientSet = dataReader.coefficientSet();
//        Queue<Function> functions = new LinkedList<Function>();
        Function[] functions = new Function[coefficientSet.length];
        for (int i = 0; i < coefficientSet.length; i++) {
            functions[i] = (new Function(coefficientSet[i]));
        }

        double[][] range = {{0, 10}, {0, 10}};

        List<double[]> points = Domain.findCornerPoints(range);
        for (double[] arr : points) {
            for (double d : arr) {
                System.out.print(d + " ");
            }
            System.out.println();
        }
        //        construct point array for domain
        Point[] pA = new Point[points.size()];
        for (int i = 0; i < pA.length; i++) {
            pA[i] = new Point(points.get(i));
        }

        List<int[]> res = Domain.findBoundaryLines(points);

        for (int i = 0; i < res.size(); i++) {
            int[] arr = res.get(i);
            for (int j = 0; j < arr.length; j++) {
                System.out.print(arr[j] + " ");
            }
            System.out.println();
        }
        //        construct segment array for domain
        Segment[] sA = new Segment[res.size()];
        for(int i = 0; i < sA.length; i++) {
            sA[i] = new Segment(res.get(i)[0], res.get(i)[1]);
        }

//        construct domain
        Domain d = new Domain(pA, sA);
        d.printDomain();
//        call constructTree method
        Tree.constructTree(functions, d, 2, "intersection-tree");
//        construct segment array for domain

//        unfinished
    }

    public static void testConstructTreeSimplex(SimplexType simplexType) throws IOException {
        DataReader dataReader = new DataReader("/Users/xiyaoli/Desktop/Study/research_program/information_element/i_tree_data/data/initDomains/initDomains_d_2.json",
                "/Users/xiyaoli/Desktop/Study/research_program/information_element/i_tree_data/data/input/data_d_2_records_5_initDomainID_1.json");
//                "/Users/maximpopov/Documents/data_d_2_records_5_initDomainID_1.json");
        // Get coefficients
        double[][] coefficientSet = dataReader.coefficientSet();
        // Queue<Function> functions = new LinkedList<Function>();
        Function[] functions = new Function[coefficientSet.length];
        for (int i = 0; i < coefficientSet.length; i++) {
            functions[i] = (new Function(coefficientSet[i]));
        }

        // define initial domain
        ArrayList<double[]> allConstraintCoefficients = new ArrayList<>();
        ArrayList<Double> allConstraintConstants = new ArrayList<>();
        // x1 >= 0 --> -x1 <= 0
        allConstraintCoefficients.add(new double[]{-1, 0});
        allConstraintConstants.add(0.0);
        // x1 <= 10
        allConstraintCoefficients.add(new double[]{1, 0});
        allConstraintConstants.add(10.0);
        // x2 >= 0 --> -x2 <= 0
        allConstraintCoefficients.add(new double[]{0, -1});
        allConstraintConstants.add(0.0);
        // x2 <= 10
        allConstraintCoefficients.add(new double[]{0, 1});
        allConstraintConstants.add(10.0);

        Function function = new Function(new double[]{0, 0, 1});

        // construct domain
        DomainSimplex d = new DomainSimplex(function, true);
        d.printDomain();

        // call constructTree method
        Tree.constructTreeSimplex(functions, d, allConstraintCoefficients, allConstraintConstants, simplexType, 2,
                "intersection-tree");
        // construct segment array for domain

        // unfinished
    }

    public static void testFindIntersectionPoints(){
        double[][] range = {{0, 10}, {0, 10}};

        List<double[]> points = Domain.findCornerPoints(range);
        for (double[] arr : points) {
            for (double d : arr) {
                System.out.print(d + " ");
            }
            System.out.println();
        }
        //        construct point array for domain
        Point[] pA = new Point[points.size()];
        for (int i = 0; i < pA.length; i++) {
            pA[i] = new Point(points.get(i));
        }

        List<int[]> res = Domain.findBoundaryLines(points);

        for (int i = 0; i < res.size(); i++) {
            int[] arr = res.get(i);
            for (int j = 0; j < arr.length; j++) {
                System.out.print(arr[j] + " ");
            }
            System.out.println();
        }
        //        construct segment array for domain
        Segment[] sA = new Segment[res.size()];
        for(int i = 0; i < sA.length; i++) {
            sA[i] = new Segment(res.get(i)[0], res.get(i)[1]);
        }

        //        construct function array
        Function f = new Function(new double[] {1, 1, 1});
        Function[] fs  = new Function[1];
        fs[0] = f;

//        construct point array for domain
//        Point[] pA = new Point[] {new Point(new double[]{0.0, 0.0}),
//                new Point(new double[]{5.0, 0.0}),
//                new Point(new double[]{5.0, 5.0}),
//                new Point(new double[]{0.0, 5.0})};
//        construct segment array for domain
//        Segment[] sA = new Segment[] {new Segment(0, 1), new Segment(1, 2), new Segment(2, 3), new Segment(3, 0)};

//        construct domain
        Domain d = new Domain(pA, sA);
        d.printDomain();

        Domain[] partitionedDomain = Partition.partitionDomain(d, f);
        for (Domain pd :partitionedDomain
             ) {
            pd.printDomain();
        }
    }

    // *******************
    // Tree Accuracy Tests
    // *******************

    public static void simplexTests() {
        originLineTest();
        treePathTest();
    }

    public static void treePathTest() {
        int num_inequality = 100;
        int num_dimension = 10;
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

        int numPartitionsSimplex = Tree.constructTreeSegmentSimplex(functions, constraintCoefficients,
                constraintConstants, SimplexType.SIMPLEX, num_dimension, domain_boundary_length);
        int numPartitionsSignChangingSimplex = Tree.constructTreeSegmentSimplex(functions, constraintCoefficients,
                constraintConstants, SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension, domain_boundary_length);
        if (numPartitionsSimplex != numPartitionsSignChangingSimplex) {
            throw new IllegalStateException("Num nodes should be the same, but is " + numPartitionsSimplex +
                    " for Simplex and " + numPartitionsSignChangingSimplex + "for SignChangingSimplex");
        }
    }

    // Given n lines going through the origin with a positive finite slope there should be n+1 subdomains or 2n+1 nodes
    public static void originLineTest() {
        int num_inequality = 100;
        int num_dimension = 10;
        int domain_boundary_length = 1;

        int expectedNumNodes = 2 * num_inequality + 1;

        // Equations defining subdomain
        ArrayList<double[]> inequalities = generate_inequalities_origin(0, num_dimension,
                domain_boundary_length);
        Function[] functions = new Function[num_inequality];
        for (int j = 0; j < functions.length; j++) {
            functions[j] = new Function(generate_equation_origin(num_dimension));
        }

        // Modifications for Simplex:
        double[] function_values = new double[num_dimension + 1];
        function_values[function_values.length - 1] = 1;
        Function function = new Function(function_values);
        DomainSimplex d = new DomainSimplex(function, true);
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
        int numNodesSimplex = Tree.constructTreeSimplex(functions, d, constraintCoefficients, constraintConstants,
                SimplexType.SIMPLEX, num_dimension, "OriginLineTestSimplex");
        if (numNodesSimplex != expectedNumNodes) {
            throw new IllegalStateException("Num nodes Simplex should be " + expectedNumNodes + ", but is "
                    + numNodesSimplex);
        }

        // Sign-Changing Simplex
        int numNodesSignChangingSimplex = Tree.constructTreeSimplex(functions, d, constraintCoefficients,
                constraintConstants, SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension,
                "OriginLineTestSignChangingSimplex");
        if (numNodesSignChangingSimplex != expectedNumNodes) {
            throw new IllegalStateException("Num nodes SignChangingSimplex should be " + expectedNumNodes + ", but is "
                    + numNodesSignChangingSimplex);
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

    public static void collect_data() throws IOException {
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
                DomainSimplex.ifPartitionsDomain(constraintCoefficients, constraintConstants, function,
                        SimplexType.SIMPLEX, num_dimension);
                stop_time = System.nanoTime();
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_simplex += average_time_repeat / unique_runs;

            // Sign-Changing Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                DomainSimplex.ifPartitionsDomain(constraintCoefficients, constraintConstants, function,
                        SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension);
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
                Tree.constructTreeSegmentSimplex(functions, constraintCoefficients, constraintConstants, SimplexType.SIMPLEX, num_dimension, domain_boundary_length);
                stop_time = System.nanoTime();
                table_counter[0]++;
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_simplex += average_time_repeat / unique_runs;

            // Sign-Changing Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                Tree.constructTreeSegmentSimplex(functions, constraintCoefficients, constraintConstants, SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension, domain_boundary_length);
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
                DomainSimplex d = new DomainSimplex(function, true);
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
                    Tree.constructTreeSimplex(functions, d, constraintCoefficients, constraintConstants, SimplexType.SIMPLEX, num_dimension, "IntersectionTree" + table_counter[0]);
                    stop_time = System.nanoTime();
                    table_counter[0]++;
                    average_time_repeat += (stop_time - start_time) / repeat_runs;
                }
                average_time_simplex += average_time_repeat / unique_runs;

                // Sign-Changing Simplex
                average_time_repeat = 0;
                for (int j = 0; j < repeat_runs; j++) {
                    start_time = System.nanoTime();
                    Tree.constructTreeSimplex(functions, d, constraintCoefficients, constraintConstants, SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension, "IntersectionTree" + table_counter[0]);
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

    // Positive finite slope going through origin
    public static ArrayList<double[]> generate_inequalities_origin(int num_inequality, int num_dimension,
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

    public static void ParseDataFiles() {
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
