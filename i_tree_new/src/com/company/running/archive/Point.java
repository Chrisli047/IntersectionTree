package com.company.running.archive;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Point {

    double[] point;

    public Point(double[] point) {
        this.point = point;
    }

    public byte[] toByte() {
        ByteBuffer buffer = ByteBuffer.allocate(point.length * Double.BYTES);

        for (double p : point) {
            buffer.putDouble(p);
        }

        byte[] bytes = buffer.array();
        return bytes;
    }

    public static Point toPoint(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        double[] doubles = new double[bytes.length / Double.BYTES];

        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = buffer.getDouble();
        }

        Point toPoint = new Point(doubles);
        return toPoint;
    }

    public double[] getPoint() {
        return this.point;
    }

    @Override
    public String toString() {
        return "x1: " + this.point[0] + " " + "x2: " + this.point[1];
    }

    static boolean equal(Point pointA, Point pointB) {
        if (Arrays.equals(pointA.point, pointB.point)) {
            return true;
        }

        return false;
    }

}
