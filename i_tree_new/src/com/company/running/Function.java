package com.company.running;

import java.nio.ByteBuffer;

public class Function {
    double[] coefficients;

    public Function(double[] coefficients) {
        this.coefficients = coefficients;
    }

    public byte[] toByte(int dimension) {
        ByteBuffer buffer = ByteBuffer.allocate((dimension + 1) * Double.BYTES);

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
        String toString = "";
        for (int i = 0; i < this.coefficients.length - 1; i++) {
            toString += "c" + i + ": " + this.coefficients[i] + " ";
        }
        toString += "constant: " + this.coefficients[this.coefficients.length - 1];
        return toString;
    }
}
