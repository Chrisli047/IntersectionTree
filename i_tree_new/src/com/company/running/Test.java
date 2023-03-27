package com.company.running;

import java.io.IOException;
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
        for (int num_inequality = 3; num_inequality <= 100; num_inequality++) {
            ArrayList<double[]> equations = generate_inequalities(num_inequality, num_dimension_default);
        }
        for (int num_dimension = 2; num_dimension <= 10; num_dimension++) {
            ArrayList<double[]> equations = generate_inequalities(num_inequality_default, num_dimension);
            // DRY num_inequality var
        }
        // iterate over #inequality (3-100: 50) and #dimenstion (2-10: 5)
        // 10 times: average the 10 time results
        //  generate inequalities
        //  per technique:
        //   if simplex: introduce slack variables (- version of each coefficient)
        //   run 10 times and average time
        // store results for input (technique, #inequality, #dimension)/output (time) in respective file (param var)
    }

    // Generated equations are in the form: a1x1 + a2x1 + b1x2 + b2x2 ≤ d (boundary_length) where double[] = {a, b, d}.
    private static ArrayList<double[]> generate_inequalities(int num_inequality, int num_dimension) {
        int boundary_length = 1;
        // Scale + 1 = maximum coefficient value. Larger values allow for lines to be more similar to an axis line.
        int coefficient_scale = 100;
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
            double[] inequality = new double[num_dimension];
            for (int j = 0; j < inequality.length; j++) {
                // 1 ≤ |coefficient| ≤ coefficient_scale + 1
                double coefficient = coefficient_scale * Math.random() + 1;
                // 50% chance to be negative
                if (Math.random() < 0.5) {
                    coefficient *= -1;
                }
                inequality[j] = coefficient;
            }
            inequalities.add(inequality);
        }

        return inequalities;
    }
}
