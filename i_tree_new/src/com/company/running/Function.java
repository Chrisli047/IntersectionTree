package com.company.running;

import java.nio.ByteBuffer;
import java.util.Arrays;

// TODO: TECH DEBT REFACTOR
// TODO: add doc comments
public class Function {
    private final double[] function;

    public Function(double[] function) {
        this.function = function;
    }

    public double[] getFunction() {
        return function.clone();
    }

    public double[] getCoefficients() {
        return Arrays.copyOf(function, function.length - 1);
    }

    public double getConstant() {
        return function[function.length - 1];
    }

    // TODO: make serialization/deserialization static in all cases (this, nodeData)
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
