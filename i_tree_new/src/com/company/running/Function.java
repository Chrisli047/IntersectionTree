package com.company.running;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * A mathematical function representing a record's score: (a, b, ..., c) from (ax + by + ... = c).
 * Where a, b, ..., are coefficients for variables x, y, ... and c is the lone constant.
 */
public record Function(double[] function) {

    /**
     * @return the coefficients [a, b, ...] not including c from (ax + by + ... = c)
     */
    public double[] getCoefficients() {
        return Arrays.copyOf(function, function.length - 1);
    }

    /**
     * @return the constant c from (ax + by + ... = c)
     */
    public double getConstant() {
        return function[function.length - 1];
    }

    public byte[] toByte() {
        ByteBuffer buffer = ByteBuffer.allocate(function.length * Double.BYTES);

        for (double p : function) {
            buffer.putDouble(p);
        }

        return buffer.array();
    }

    public static Function toFunction(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        double[] function = new double[bytes.length / Double.BYTES];

        for (int i = 0; i < function.length; i++) {
            function[i] = buffer.getDouble();
        }

        return new Function(function);
    }
}
