package com.company.running;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Partition {

    public static ParametricNodeData[] partitionDomain(ParametricNodeData d, Function f) {

        List<Point> leftPoints = new ArrayList<>();
        List<Point> rightPoints = new ArrayList<>();
        List<Segment> leftSegments = new ArrayList<>();
        List<Segment> rightSegments = new ArrayList<>();
        List<Point> intersectionPoints = new ArrayList<>();

        List<Point[]> leftSegmentsByPoint = new ArrayList<>();
        List<Point[]> rightSegmentsByPoint = new ArrayList<>();

        for (Segment s:
             d.segment) {
            Point intersectionPoint = findIntersectionPoints(d.point, s, f);
            if (intersectionPoint != null) {
                intersectionPoints.add(intersectionPoint);
                leftPoints.add(intersectionPoint);
                rightPoints.add(intersectionPoint);
                if (evaluateFunction(d.point[s.startPointID].point, f.coefficients) < 0) {
                    leftSegmentsByPoint.add(new Point[] {d.point[s.startPointID], intersectionPoint});
                    rightSegmentsByPoint.add(new Point[] {d.point[s.endPointID], intersectionPoint});
                } else {
                    rightSegmentsByPoint.add(new Point[] {d.point[s.startPointID], intersectionPoint});
                    leftSegmentsByPoint.add(new Point[] {d.point[s.endPointID], intersectionPoint});
                }
                System.out.println(areCollinear(d.point[s.startPointID].point, d.point[s.endPointID].point, intersectionPoint.point));
            } else {
                if (evaluateFunction(d.point[s.startPointID].point, f.coefficients) < 0) {
                    leftSegmentsByPoint.add(new Point[] {d.point[s.startPointID], d.point[s.endPointID]});
                } else {
                    rightSegmentsByPoint.add(new Point[] {d.point[s.startPointID], d.point[s.endPointID]});
                }
                continue;
            }
        }

        for (Point p:
             d.point) {
            if (evaluateFunction(p.point, f.coefficients) < 0) {
                leftPoints.add(p);
            } else if (evaluateFunction(p.point, f.coefficients) > 0) {
                rightPoints.add(p);
            } else {
                continue;
            }
        }

        System.out.println("Left points: ");
        for (Point p:
             leftPoints) {
            System.out.println(p);
        }
        System.out.println("Right points: ");
        for (Point p:
                rightPoints) {
            System.out.println(p);
        }

//        get all possible intersection lines
        double[][] points = new double[intersectionPoints.size()][2];
        int index = 0;
        for (int i = 0; i < intersectionPoints.size(); i++) {
            points[i] = intersectionPoints.get(index++).point;
        }

        System.out.println();
        List<double[][]> possibleSegments = getAllSegments(points);
        for (double[][] array : possibleSegments) {
            Point p1 = new Point(array[0]);
            Point p2 = new Point(array[1]);
            leftSegmentsByPoint.add(new Point[] {p1, p2});
            rightSegmentsByPoint.add(new Point[] {p1, p2});
            System.out.println(p1);
            System.out.println(p2);
        }
//        end here, delete later

        System.out.println("right segments: ");
        for (Point[] p : rightSegmentsByPoint) {
            int index1 = findIndex(rightPoints, p[0].point);
            int index2 = findIndex(rightPoints, p[1].point);
            rightSegments.add(new Segment(index1, index2));
            System.out.println(rightPoints.get(index1));
            System.out.println(rightPoints.get(index2));
            System.out.println("================");
        }

        System.out.println("left segments: ");
        for (Point[] p : leftSegmentsByPoint) {
            int index1 = findIndex(leftPoints, p[0].point);
            int index2 = findIndex(leftPoints, p[1].point);
            leftSegments.add(new Segment(index1, index2));
            System.out.println(leftPoints.get(index1));
            System.out.println(leftPoints.get(index2));
            System.out.println("================");
        }

        // transform list to array
        Point[] leftPointsArray = new Point[leftPoints.size()];
        for(int i = 0; i < leftPointsArray.length; i++) {
            leftPointsArray[i] = leftPoints.get(i);
        }

        Point[] rightPointsArray = new Point[rightPoints.size()];
        for(int i = 0; i < rightPointsArray.length; i++) {
            rightPointsArray[i] = rightPoints.get(i);
        }

        Segment[] leftSegmentArray = new Segment[leftSegments.size()];
        for(int i = 0; i < leftSegmentArray.length; i++) {
            leftSegmentArray[i] = leftSegments.get(i);
        }

        Segment[] rightSegmentArray = new Segment[rightSegments.size()];
        for(int i = 0; i < rightSegmentArray.length; i++) {
            rightSegmentArray[i] = rightSegments.get(i);
        }

        ParametricNodeData leftDomain = new ParametricNodeData(leftPointsArray, leftSegmentArray);
        ParametricNodeData rightDomain = new ParametricNodeData(rightPointsArray, rightSegmentArray);

        return new ParametricNodeData[] {leftDomain, rightDomain};
    }

    public static double evaluateFunction(double[] point, double[] coefficients) {
        double result = 0;
        int n = point.length;
        for (int i = 0; i < n; i++) {
            result += coefficients[i] * point[i];
        }
        result -= coefficients[n]; // add the constant term
        return result;
    }

    public static Point findIntersectionPoints(Point[] p, Segment s, Function f) {
//        double[][] segment = {{0, 0, 0}, {5, 0, 0}}; // segment from (-1,-1) to (0,2) in 3D
//        double[] coefficients = {1, 1, 1, 1}; // the plane x + y + z = 1
        double[] intersection = findIntersection(p[s.startPointID].point, p[s.endPointID].point, f.coefficients);
        if (intersection != null) {
            Point res = new Point(intersection);
            System.out.println(res);
            return res;
        } else {
            return null;
        }
    }

    public static double[] findIntersection(double[] startPoint, double[] endPoint, double[] coefficients) {
        int n = startPoint.length; // number of dimensions
        double[] intersection = new double[n];

        // Parametric equations for the segment
        double[] d = new double[n];
        for (int i = 0; i < n; i++) {
            d[i] = endPoint[i] - startPoint[i];
        }

        // Parametric equations for the linear function
        double constant = coefficients[n]; // last element is the constant
        double[] a = new double[n];
        for (int i = 0; i < n; i++) {
            a[i] = coefficients[i];
        }

        // Solve for the parameter t in the equation startPoint + t*d = a*t + constant
        double t = (constant - dotProduct(a, startPoint)) / dotProduct(a, d);

        // Calculate the intersection point
        for (int i = 0; i < n; i++) {
            intersection[i] = startPoint[i] + t * d[i];
            // Check if intersection point is outside the range for this variable
            if ((intersection[i] < Math.min(startPoint[i], endPoint[i])) || (intersection[i] > Math.max(startPoint[i], endPoint[i]))) {
                return null;
            }
        }

        return intersection;
    }

    // Helper method to calculate the dot product of two vectors
    public static double dotProduct(double[] v1, double[] v2) {
        double result = 0.0;
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }
        return result;
    }

    public static boolean areCollinear(double[] p1, double[] p2, double[] p3) {
        int n = p1.length;
        boolean collinear = true;
        double epsilon = 1e-10; // tolerance for comparing double values

        // Calculate vectors between p1 and p2, and between p1 and p3
        double[] v1 = new double[n];
        double[] v2 = new double[n];
        for (int i = 0; i < n; i++) {
            v1[i] = p2[i] - p1[i];
            v2[i] = p3[i] - p1[i];
        }

        // Check if v1 and v2 are parallel
        double crossProduct = v1[0] * v2[1] - v1[1] * v2[0]; // for 2D points
        for (int i = 2; i < n; i++) {
            if (Math.abs(v1[0] * v2[i] - v1[i] * v2[0] - crossProduct) > epsilon) {
                collinear = false;
                break;
            }
        }

        // Check if p3 lies on the line segment defined by p1 and p2
        double lengthRatio1 = length(v2) / length(v1);
        double lengthRatio2 = length(subtract(p3, p1)) / length(v1);
        if (Math.abs(lengthRatio1 - lengthRatio2) > epsilon) {
            collinear = false;
        }

        return collinear;
    }

    // Helper method to calculate the length of a vector
    public static double length(double[] v) {
        double sum = 0.0;
        for (int i = 0; i < v.length; i++) {
            sum += v[i] * v[i];
        }
        return Math.sqrt(sum);
    }

    // Helper method to subtract two vectors
    public static double[] subtract(double[] v1, double[] v2) {
        int n = v1.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = v1[i] - v2[i];
        }
        return result;
    }

    public static List<double[][]> getAllSegments(double[][] points) {
        int n = points.length;
        List<double[][]> segments = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                double[][] segment = new double[2][n];
                segment[0] = points[i];
                segment[1] = points[j];
                segments.add(segment);
            }
        }
        return segments;
    }


    public static double[] pointsToLinearEquation(double[] p1, double[] p2) {
        int n = p1.length;
        double[] linearEquation = new double[n+1];
        double slope = (p2[1] - p1[1]) / (p2[0] - p1[0]);
        double yIntercept = p1[1] - slope * p1[0];
        linearEquation[0] = -slope;
        linearEquation[1] = 1;
        linearEquation[n] = yIntercept;
        for (int i = 2; i < n; i++) {
            linearEquation[i] = 0;
        }
        return linearEquation;
    }

    public static boolean ifPartition(List<Point> points, double[] c) {
        int numPos = 0;
        int numNeg = 0;
        for (Point p : points) {
            double result = c[c.length - 1];
            for (int i = 0; i < p.point.length; i++) {
                result += c[i] * p.point[i];
            }
            if (result > 0) {
                numPos++;
            } else if (result < 0) {
                System.out.println("------------");
                System.out.println(p);
                System.out.println(result);
                System.out.println("------------");
                numNeg++;
            }
            if (numPos > 0 && numNeg > 0) {
                return true;
            }
        }
        return false;
    }

    public static int findIndex(List<Point> list, double[] searchElement) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (Arrays.equals(list.get(i).point, searchElement)) {
                index = i;
                break;
            }
        }
        return index;
    }

}
