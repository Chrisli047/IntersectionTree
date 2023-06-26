package com.company.running;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * A mathematical function representing a record's score:
 * (a, b, ..., c) from (ax + by + ... = c).
 * Where x, y, ... are record variables values, thus constants here.
 * Where a, b, ..., c are record variable coefficients, thus variables here.
 */
public class Function {
    private final double[] function;

    /**
     * Creates a function [a, b, ..., c] where a, b, ... are variable
     * coefficients and c the constant in (ax + by + ... = c).
     */
    public Function(double[] function) {
        this.function = function;
    }

    /**
     * @return the full function [a, b, ..., c] from (ax + by + ... = c)
     */
    public double[] getFunction() {
        return function.clone();
    }

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

    // TODO: static vs instance serialization/deserialization question after is MySQL necessary answer
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
