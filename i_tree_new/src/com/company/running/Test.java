package com.company.running;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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
        byte[] bd = d.toByte();
        Domain originalDomain = Domain.toDomain(bd);
        originalDomain.printDomain();

        Function f = new Function(new double[]{1.0, 2.0, 3.0});
        byte[] fb = f.toByte();
        Function originalFunction = Function.toFunction(fb);
        System.out.println(originalFunction);
    }

    public static void testInsertRecord() {
        Point[] pA = new Point[] {new Point(new double[]{1.0, 2.0}), new Point(new double[]{2.0, 3.0})};
        Segment[] sA = new Segment[] {new Segment(1, 2), new Segment(2, 3)};
        Domain d = new Domain(pA, sA);
        byte[] bd = d.toByte();
        Domain originalDomain = Domain.toDomain(bd);
        originalDomain.printDomain();

        Function f = new Function(new double[]{1.0, 2.0, 3.0});
        byte[] fb = f.toByte();
        Function originalFunction = Function.toFunction(fb);
        System.out.println(originalFunction);

        NodeRecord nodeRecord = new NodeRecord(d, f, -1, -1);
        nodeRecord.insertToMySql();
    }

    public static void testGetRecordById(int id) {
        NodeRecord nodeRecord = NodeRecord.getRecordById(id, false);
        nodeRecord.d.printDomain();
        System.out.println(nodeRecord.f);
        System.out.println(nodeRecord.leftID);
        System.out.println(nodeRecord.rightID);
    }

    public static void testUpdateRecord() {
        NodeRecord nodeRecord = NodeRecord.updateRecord(1, 2,2, false);
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
        Tree.constructTree(functions, d);
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
        Tree.constructTreeSimplex(functions, d, allConstraintCoefficients, allConstraintConstants, simplexType);
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

    public static void test_individual_feasibility_checks() {
        // Does not include boundary inequalities
        int num_inequality_default = 50;
        int num_dimension_default = 5;

        System.out.println("Variable #Inequalities\n");
        for (int num_inequality = 3; num_inequality <= 100; num_inequality++) {
            System.out.println("#Inequalities = " + num_inequality);
            time_individual_feasibility_checks(num_dimension_default, num_inequality);
        }

        System.out.println("\n\nVariable #Dimensions\n");
        for (int num_dimension = 2; num_dimension <= 10; num_dimension++) {
            System.out.println("#Dimensions = " + num_dimension);
            time_individual_feasibility_checks(num_dimension, num_inequality_default);
        }
    }

    private static void time_individual_feasibility_checks(int num_dimension, int num_inequality) {
        // Scale + 1 = maximum coefficient value. Larger values allow for lines to be more similar to an axis line.
        int coefficient_scale = 100;

        int unique_runs = 10;
        int repeat_runs = 10;
        long average_time_simplex = 0;
        long average_time_sign_changing_simplex = 0;
        long average_time_parametric_equation = 0;
        long average_time_repeat, start_time, stop_time;

        for (int i = 0; i < unique_runs; i++) {
            // Equations defining subdomain
            ArrayList<double[]> equations = generate_inequalities(num_inequality, num_dimension, coefficient_scale);
            // Equation of line for feasibility checking. Hijacks generate_inequalities().
            Function function = new Function(generate_equation(num_dimension, coefficient_scale));

            // Modifications for Simplex:
            // Introduce slack variables
            ArrayList<double[]> constraintCoefficients = new ArrayList<>();
            // Separate constraint constants
            ArrayList<Double> constraintConstants = new ArrayList<>();
            for (double[] equation : equations) {
                // ignore constant at the end
                double[] slackenedEquation = new double[equation.length * 2 - 2];
                for (int j = 0; j < equation.length - 1; j++) {
                    slackenedEquation[j * 2] = equation[j];
                    slackenedEquation[j * 2 + 1] = -equation[j];
                }
                constraintConstants.add(equation[equation.length - 1]);
                constraintCoefficients.add(slackenedEquation);
            }

            // Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                DomainSimplex.ifPartitionsDomain(constraintCoefficients, constraintConstants, function,
                        SimplexType.SIMPLEX);
                stop_time = System.nanoTime();
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_simplex += average_time_repeat / unique_runs;

            // Sign-Changing Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                DomainSimplex.ifPartitionsDomain(constraintCoefficients, constraintConstants, function,
                        SimplexType.SIGN_CHANGING_SIMPLEX);
                stop_time = System.nanoTime();
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_sign_changing_simplex += average_time_repeat / unique_runs;

            // Parametric Equation
            // TODO: Xiyao: get average_time_parametric_equation here
        }

        System.out.println("Simplex: " + average_time_simplex);
        System.out.println("Sign-Changing Simplex: " + average_time_sign_changing_simplex);
        System.out.println("Parametric Equation: " + average_time_parametric_equation);
    }

    private static double[] generate_equation(int num_dimension, int coefficient_scale) {
        double[] equation = new double[num_dimension + 1];
        for (int i = 0; i < num_dimension; i++) {
            // 50% chance to be negative
            // No scaling required, only relevant to other coefficients in the same equation.
            equation[i] = coefficient_scale * Math.random() + 1;
        }
        equation[num_dimension] = 1;
        return equation;
    }

    // Generated equations are in the form: a1x1 + a2x1 + b1x2 + b2x2 + ... ≤ c where double[] = {a, b, c}.
    private static ArrayList<double[]> generate_inequalities(int num_inequality, int num_dimension,
                                                             int coefficient_scale) {
        int boundary_length = 1;
        ArrayList<double[]> inequalities = new ArrayList<>();

        // Generate boundaries
        for (int i = 0; i < num_dimension; i++) {
            // 1x ≥ 0 --> -1x ≤ 0
            double[] lower_bound = new double[num_dimension + 1];
            lower_bound[i] = -1;
            // 1x ≤ boundary_length
            double[] upper_bound = new double[num_dimension + 1];
            upper_bound[i] = 1;
            upper_bound[upper_bound.length - 1] = boundary_length;
            inequalities.add(lower_bound);
            inequalities.add(upper_bound);
        }

        // We can prove that the set of all equations that intersect the circle (or higher dimensional figure) inscribed
        // by the domain boundary is equal to the set of all equations ax1 + bx2 + cx3 + ... = d for all a, b, c, ..., d
        // where a^2 + b^2 + c^2 + ... > 1 and d > 0. Thus, we generate a subset of this set where the absolute value of
        // all coefficients |a|, |b|, |c|, ... > 1.
        for (int i = 0; i < num_inequality; i++) {
            double[] inequality = new double[num_dimension+1];

            // Set a, b, c, ...
            for (int j = 0; j < num_dimension; j++) {
                // 1 ≤ |coefficient| ≤ coefficient_scale + 1
                double coefficient = coefficient_scale * Math.random() + 1;
                // 50% chance to be negative
                if (Math.random() < 0.5) {
                    coefficient *= -1;
                }
                inequality[j] = coefficient;
            }
            // Set d. Varying d is equivalent to varying each of a, b, c, ... so we can always keep it as 1.
            inequality[num_dimension] = 1;

            // The inequalities are very likely to be infeasible (contradictory), so we can force them to all accept one
            // point to ensure there is no contradiction. The center-most point in the domain boundary is likely to
            // result in the most complex subdomain, so we will choose that point.
            double point_value = 0;
            for (int j = 0; j < num_dimension; j++) {
                // point value = boundary_length/2
                point_value += inequality[j] * boundary_length/2;
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
