package com.company.running;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ParametricNodeData implements NodeData {
    Segment[] segment;
    Point[] point;
    public int dimension;

    public ParametricNodeData(Point[] point, Segment[] segment, int dimension) {
        this.point = point;
        this.segment = segment;
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }

    public byte[] toByte() {
        ByteBuffer buffer = ByteBuffer.allocate( Integer.BYTES +
                4 + point.length * dimension * Double.BYTES
                        + 4 + segment.length * 2 * Integer.BYTES);

        buffer.putInt(dimension);

        buffer.putInt(point.length);
        for (Point p : point) {
            for (Double d : p.point) {
                buffer.putDouble(d);
            }
        }

        buffer.putInt(segment.length);
        for (Segment s: segment) {
            buffer.putInt(s.startPointID);
            buffer.putInt(s.endPointID);
        }

        byte[] bytes = buffer.array();
        return bytes;
    }

    public ParametricNodeData toData(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        int dimension = buffer.getInt();

        int numOfPoint = buffer.getInt();
        System.out.println("The number of points is: " + numOfPoint);

        Point[] pArray = new Point[numOfPoint];
        for (int i = 0; i < numOfPoint; i++) {
            double[] p = new double[dimension];
            for (int j = 0; j < dimension; j++) {
                p[j] = buffer.getDouble();
            }
            Point newPoint = new Point(p);
            pArray[i] = newPoint;
        }

        int numOfSegment = buffer.getInt();
        Segment[] sArray = new Segment[numOfSegment];
        for (int i = 0; i < numOfSegment; i++) {
            Segment s = new Segment(buffer.getInt(), buffer.getInt());
            sArray[i] = s;
        }

        ParametricNodeData d = new ParametricNodeData(pArray, sArray, dimension);
        return d;
    }

    public static List<double[]> findCornerPoints(double[][] range) {
        int dimensions = range.length;
        int numPoints = (int) Math.pow(2, dimensions);
        List<double[]> cornerPoints = new ArrayList<>();

        for (int i = 0; i < numPoints; i++) {
            double[] point = new double[dimensions];
            int mask = 1;
            for (int j = 0; j < dimensions; j++) {
                if ((i & mask) == 0) {
                    point[j] = range[j][0];
                } else {
                    point[j] = range[j][1];
                }
                mask = mask << 1;
            }
            cornerPoints.add(point);
        }

        return cornerPoints;
    }

    public static List<int[]> findBoundaryLines(List<double[]> cornerPoints) {
        List<int[]> boundaryLines = new ArrayList<>();
        int dimensions = cornerPoints.get(0).length;

        for (int i = 0; i < cornerPoints.size(); i++) {
            double[] point1 = cornerPoints.get(i);
            for (int j = i + 1; j < cornerPoints.size(); j++) {
                double[] point2 = cornerPoints.get(j);
                int numSharedCoords = 0;
                int sharedCoordIndex = -1;
                for (int k = 0; k < dimensions; k++) {
                    if (point1[k] == point2[k]) {
                        numSharedCoords++;
                        sharedCoordIndex = k;
                    }
                }
                if (numSharedCoords == 1) {
                    int[] boundaryLine = new int[2];
                    boundaryLine[0] = i;
                    boundaryLine[1] = j;
                    boundaryLines.add(boundaryLine);
                }
            }
        }

        return boundaryLines;
    }

    public static boolean ifPartitionsDomain(ParametricNodeData d, double[] c) {
        if (d.point[0].point.length != c.length - 1) {
            System.out.println(d.point[0].point.length);
            System.out.println(c.length - 1);
            throw new IllegalArgumentException("Dimensionality of domain and coefficient vector must match");
        }
        int numPos = 0;
        int numNeg = 0;
        for (Point p : d.point) {
            double result = -c[c.length - 1];
            for (int i = 0; i < p.point.length; i++) {
                result += c[i] * p.point[i];
            }
            if (result > 0) {
                numPos++;
            } else if (result < 0) {
                numNeg++;
            }
            if (numPos > 0 && numNeg > 0) {
                return true;
            }
        }
        return false;
    }

//    public Domain[] Partition(Function f) {
//        GaussianElimination
//    }


    public void printDomain() {
        System.out.println("Print domain: ");
        System.out.println("Points: ");
        for (Point p : this.point) {
            System.out.println(p);
        }
        System.out.println("Line segments: ");
        for (Segment s : this.segment) {
            System.out.println(s);
        }
        System.out.println();
    }
}
