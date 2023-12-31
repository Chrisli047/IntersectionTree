package com.company.running;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;

// TODO: unique classes for different solutions: no need to store ex. sets in non point memorizing solutions

// TODO: TECH DEBT REFACTOR
public class NodeData {
    public int intersectionIndex;
    public HashSet<double[]> unknownSet;
    public HashSet<double[]> maxSet;
    public HashSet<double[]> minSet;
    // TODO: dimension is tree property, not a node property
    public int dimension;

    public NodeData(int intersectionIndex, HashSet<double[]> unknownSet, HashSet<double[]> maxSet,
                    HashSet<double[]> minSet, int dimension) {
        this.intersectionIndex = intersectionIndex;
        this.unknownSet = unknownSet;
        this.maxSet = maxSet;
        this.minSet = minSet;
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }

    public byte[] toByte() {
        boolean storePoints = true;

        int capacity = 5 * Integer.BYTES;
        if (storePoints) {
            if (unknownSet != null) {
                capacity += dimension * Double.BYTES * unknownSet.size();
            }

            if (maxSet != null) {
                capacity += dimension * Double.BYTES * maxSet.size();
            }

            if (minSet != null) {
                capacity += dimension * Double.BYTES * minSet.size();
            }
        }

        ByteBuffer buffer = ByteBuffer.allocate(capacity);

        buffer.putInt(dimension);

        buffer.putInt(intersectionIndex);

        if (unknownSet != null) {
            buffer.putInt(unknownSet.size());
            unknownSet.forEach(variableValues -> {
                for (int i = 0; i < variableValues.length; i++) {
                    buffer.putDouble(variableValues[i]);
                }
            });
        } else {
            buffer.putInt(0);
        }

        if (maxSet != null) {
            buffer.putInt(maxSet.size());
            maxSet.forEach(variableValues -> {
                for (int i = 0; i < variableValues.length; i++) {
                    buffer.putDouble(variableValues[i]);
                }
            });
        } else {
            buffer.putInt(0);
        }

        if (minSet != null) {
            buffer.putInt(minSet.size());
            minSet.forEach(variableValues -> {
                for (int i = 0; i < variableValues.length; i++) {
                    buffer.putDouble(variableValues[i]);
                }
            });
        } else {
            buffer.putInt(0);
        }

        return buffer.array();
    }

    public NodeData toData(byte[] bytes) {
        boolean storedPoints = true;

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        HashSet<double[]> unknownSet = null;
        HashSet<double[]> maxSet = null;
        HashSet<double[]> minSet = null;

        int dimension = buffer.getInt();

        int intersectionIndex = buffer.getInt();

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
        return new NodeData(intersectionIndex, unknownSet, maxSet, minSet, dimension);
    }

    public static boolean ifPartitionsDomain(ArrayList<double[]> allConstraintCoefficients,
                                             ArrayList<Double> allConstraintConstants,
                                             Function function,
                                             SimplexType simplexType,
                                             int dimension,
                                             HashSet<double[]> maxSet,
                                             HashSet<double[]> minSet,
                                             boolean maxFound,
                                             boolean minFound) {
        double[] objectiveFunctionVariableCoefficients = function.getCoefficients();
        double objectiveFunctionConstant = function.getConstant();

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
}
