package com.company.running;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
        DomainSignChangingSimplex d = new DomainSignChangingSimplex(function, true);
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
}
