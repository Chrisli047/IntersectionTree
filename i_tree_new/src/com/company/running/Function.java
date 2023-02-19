package com.company.running;

import java.nio.ByteBuffer;

public class Function {
    double[] coefficients;

    public Function(double[] coefficients) {
        this.coefficients = coefficients;
    }

    public byte[] toByte() {
        ByteBuffer buffer = ByteBuffer.allocate((Constants.DIMENSION + 1) * Double.BYTES);

        for (double p : coefficients) {
            buffer.putDouble(p);
        }

        byte[] bytes = buffer.array();
        return bytes;
    }

    public static Function toFunction(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        double[] doubles = new double[bytes.length / Double.BYTES];

        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = buffer.getDouble();
        }

        Function toFunction = new Function(doubles);
        return toFunction;
    }


    @Override
    public String toString() {
        return "c1: " + this.coefficients[0] + " " + "c2: " + this.coefficients[1] + " " + "constant: " + this.coefficients[2];
    }
}
