package com.company.running;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;

public class DomainSimplex implements DomainType {
    double[] constraintCoefficients;
    double constraintConstant;
    HashSet<double[]> unknownSet;
    HashSet<double[]> maxSet;
    HashSet<double[]> minSet;

    public DomainSimplex(Function function, boolean lessThan, HashSet<double[]> unknownSet, HashSet<double[]> maxSet,
                         HashSet<double[]> minSet) {
        this.constraintCoefficients = functionToConstraintCoefficients(function);
        this.constraintConstant = function.coefficients[function.coefficients.length-1];
        if (!lessThan) {
            for (int i = 0; i < constraintCoefficients.length; i++) {
                constraintCoefficients[i] *= -1;
            }
        }
        this.unknownSet = unknownSet;
        this.maxSet = maxSet;
        this.minSet = minSet;
    }

    private static double[] functionToConstraintCoefficients(Function function) {
        double[] constraintCoefficients = new double[function.coefficients.length-1];
        System.arraycopy(function.coefficients, 0, constraintCoefficients, 0,
                function.coefficients.length - 1);
        return constraintCoefficients;
    }

    public byte[] toByte(int dimension, boolean storePoints) {
        int capacity = constraintCoefficients.length * Double.BYTES + Double.BYTES;
        if (storePoints) {
            capacity += 3 * Integer.BYTES + dimension * Double.BYTES * unknownSet.size() +
                    dimension * Double.BYTES * maxSet.size() + dimension * Double.BYTES * minSet.size();
        }
        ByteBuffer buffer = ByteBuffer.allocate(capacity);

        for (double c : constraintCoefficients) {
            buffer.putDouble(c);
        }

        buffer.putDouble(constraintConstant);

        if (unknownSet != null) {
            buffer.putInt(unknownSet.size());
            unknownSet.forEach(variableValues -> {
                for (int i = 0; i < variableValues.length; i++) {
                    buffer.putDouble(variableValues[i]);
                }
            });
        }

        if (maxSet != null) {
            buffer.putInt(maxSet.size());
            maxSet.forEach(variableValues -> {
                for (int i = 0; i < variableValues.length; i++) {
                    buffer.putDouble(variableValues[i]);
                }
            });
        }

        if (minSet != null) {
            buffer.putInt(minSet.size());
            minSet.forEach(variableValues -> {
                for (int i = 0; i < variableValues.length; i++) {
                    buffer.putDouble(variableValues[i]);
                }
            });
        }

        byte[] bytes = buffer.array();
        return bytes;
    }

    public static DomainSimplex toDomain(byte[] bytes, int dimension, boolean storedPoints) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        double[] coefficients = new double[dimension + 1];
        for (int i = 0; i < dimension; i++) {
            coefficients[i] = buffer.getDouble();
        }
        coefficients[coefficients.length - 1] = buffer.getInt();
        Function function = new Function(coefficients);

        HashSet<double[]> unknownSet = null;
        HashSet<double[]> maxSet = null;
        HashSet<double[]> minSet = null;

        if (storedPoints) {
            int unknownSetSize = buffer.getInt();
            for (int i = 0; i < unknownSetSize; i++) {
                double[] variableValues = new double[dimension];
                for (int j = 0; j < variableValues.length; j++) {
                    variableValues[j] = buffer.getDouble();
                }
                unknownSet.add(variableValues);
            }

            int maxSetSize = buffer.getInt();
            for (int i = 0; i < maxSetSize; i++) {
                double[] variableValues = new double[dimension];
                for (int j = 0; j < variableValues.length; j++) {
                    variableValues[j] = buffer.getDouble();
                }
                maxSet.add(variableValues);
            }

            int minSetSize = buffer.getInt();
            for (int i = 0; i < minSetSize; i++) {
                double[] variableValues = new double[dimension];
                for (int j = 0; j < variableValues.length; j++) {
                    variableValues[j] = buffer.getDouble();
                }
                minSet.add(variableValues);
            }
        }

        // lessThan has already been accounted for when initially created and then encoded
        return new DomainSimplex(function, true, unknownSet, maxSet, minSet);
    }

    // TODO: if max/min found check less
    public static boolean ifPartitionsDomain(ArrayList<double[]> allConstraintCoefficients,
                                             ArrayList<Double> allConstraintConstants,
                                             Function function,
                                             SimplexType simplexType,
                                             int dimension,
                                             HashSet<double[]> maxSet,
                                             HashSet<double[]> minSet,
                                             boolean maxFound,
                                             boolean minFound) {
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

        SimplexMarker max = null;
        SimplexMarker min = null;
        try {
            switch (simplexType) {
                case SIMPLEX:
                    if (!maxFound) {
                        max = new TwoPhaseSimplex(constraintVariableCoefficients, constraintConstants,
                                objectiveFunctionVariableCoefficients);
                    }
                    if (!minFound) {
                        min = new TwoPhaseSimplex(constraintVariableCoefficients, constraintConstants,
                                objectiveFunctionVariableCoefficientsMin);
                    }
                    break;
                case SIGN_CHANGING_SIMPLEX:
                    if (!maxFound) {
                        max = new TwoPhaseSignChangingSimplex(constraintVariableCoefficients, constraintConstants,
                                objectiveFunctionVariableCoefficients, true, objectiveFunctionConstant);
                    }
                    if (!minFound) {
                        min = new TwoPhaseSignChangingSimplex(constraintVariableCoefficients, constraintConstants,
                                objectiveFunctionVariableCoefficientsMin, false, objectiveFunctionConstant);
                    }
                    break;
                case POINT_REMEMBERING_PERMANENT_SIGN_CHANGING_SIMPLEX:
                case POINT_REMEMBERING_LOCAL_SIGN_CHANGING_SIMPLEX:
                    if (!maxFound) {
                        max = new TwoPhaseSignChangingPointMemorizingSimplex(constraintVariableCoefficients,
                                constraintConstants, objectiveFunctionVariableCoefficients, true,
                                objectiveFunctionConstant, maxSet, minSet);
                    }
                    if (!minFound) {
                        min = new TwoPhaseSignChangingPointMemorizingSimplex(constraintVariableCoefficients,
                                constraintConstants, objectiveFunctionVariableCoefficients, false,
                                objectiveFunctionConstant, maxSet, minSet);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Simplex type argument is invalid. Inputted argument: " +
                            simplexType);
            }
        } catch (ArithmeticException e) {
            // program is unbounded (should not be allowed in input) or infeasible (no partition)
            System.out.println(e.getMessage());
            return false;
        }

        boolean maxGood = maxFound || (max.value() > objectiveFunctionConstant);
        boolean minGood = minFound || (-min.value() < objectiveFunctionConstant);
        return maxGood && minGood;
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
