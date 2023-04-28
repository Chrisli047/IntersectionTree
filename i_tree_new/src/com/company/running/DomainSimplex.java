package com.company.running;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class DomainSimplex implements DomainType {
    double[] constraintCoefficients;
    double constraintConstant;

    public DomainSimplex(Function function, boolean lessThan) {
        this.constraintCoefficients = functionToConstraintCoefficients(function);
        this.constraintConstant = function.coefficients[function.coefficients.length-1];
        if (!lessThan) {
            for (int i = 0; i < constraintCoefficients.length; i++) {
                constraintCoefficients[i] *= -1;
            }
        }
    }

    private static double[] functionToConstraintCoefficients(Function function) {
        double[] constraintCoefficients = new double[function.coefficients.length-1];
        System.arraycopy(function.coefficients, 0, constraintCoefficients, 0,
                function.coefficients.length - 1);
        return constraintCoefficients;
    }

    public byte[] toByte(int dimension) {
        ByteBuffer buffer = ByteBuffer.allocate(
                constraintCoefficients.length * Double.BYTES + Double.BYTES);

        for (double c : constraintCoefficients) {
            buffer.putDouble(c);
        }

        buffer.putDouble(constraintConstant);

        byte[] bytes = buffer.array();
        return bytes;
    }

    public static DomainSimplex toDomain(byte[] bytes, int dimension) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        double[] coefficients = new double[dimension + 1];
        for (int i = 0; i < dimension; i++) {
            coefficients[i] = buffer.getDouble();
        }
        coefficients[coefficients.length - 1] = buffer.getInt();
        Function function = new Function(coefficients);

        //lessThan has already been accounted for when initially created and then encoded
        DomainSimplex d = new DomainSimplex(function, false);
        return d;
    }

    public static boolean ifPartitionsDomain(ArrayList<double[]> allConstraintCoefficients,
                                             ArrayList<Double> allConstraintConstants,
                                             Function function,
                                             SimplexType simplexType,
                                             int dimension) {
        double[] objectiveFunctionVariableCoefficients = functionToConstraintCoefficients(function);
        double objectiveFunctionConstant = function.coefficients[function.coefficients.length-1];

        double[][] constraintVariableCoefficients =
                new double[allConstraintCoefficients.size()][dimension];
        for (int i = 0; i < constraintVariableCoefficients.length; i++) {
            constraintVariableCoefficients[i] = allConstraintCoefficients.get(i);
        }
        double[] constraintConstants = new double[allConstraintConstants.size()];
        for (int i = 0; i < constraintConstants.length; i++) {
            constraintConstants[i] = allConstraintConstants.get(i);
        }

        //minimization multiplies objective function by -1
        double[] objectiveFunctionVariableCoefficientsMin = new double[objectiveFunctionVariableCoefficients.length];
        for (int i = 0; i < objectiveFunctionVariableCoefficientsMin.length; i++) {
            objectiveFunctionVariableCoefficientsMin[i] = -objectiveFunctionVariableCoefficients[i];
        }

        SimplexMarker max;
        SimplexMarker min;
        try {
            switch (simplexType) {
                case SIMPLEX:
                    max = new TwoPhaseSimplex(constraintVariableCoefficients, constraintConstants,
                            objectiveFunctionVariableCoefficients);
                    min = new TwoPhaseSimplex(constraintVariableCoefficients, constraintConstants,
                            objectiveFunctionVariableCoefficientsMin);
                    break;
                case SIGN_CHANGING_SIMPLEX:
                    max = new TwoPhaseSignChangingSimplex(constraintVariableCoefficients, constraintConstants,
                            objectiveFunctionVariableCoefficients, true);
                    min = new TwoPhaseSignChangingSimplex(constraintVariableCoefficients, constraintConstants,
                            objectiveFunctionVariableCoefficientsMin, false);
                    break;
                default:
                    throw new IllegalArgumentException("Simplex type argument is invalid. Inputted argument: " +
                            simplexType);
            }
        } catch (ArithmeticException e) {
            // TODO: we had infeasible problems because our constraints were thrown together wrong, we can return to exceptions here
            // program is unbounded (should not be allowed in input) or infeasible (no partition)
//            System.out.println(e.getMessage());
            return false;
        }

        double minValue = -min.value();
        double maxValue = max.value();
        return minValue < objectiveFunctionConstant && maxValue > objectiveFunctionConstant;
    }


    public void printDomain() {
        System.out.println("Print domain: ");
        System.out.println("Constraint Coefficients: ");
        for (double c : this.constraintCoefficients) {
            System.out.println(c);
        }
        System.out.println("Constraint Constant: " + this.constraintConstant);
        System.out.println();
    }
}
