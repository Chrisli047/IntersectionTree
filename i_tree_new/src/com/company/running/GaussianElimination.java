package com.company.running;

/**
 ** Java Program to Implement Gaussian Elimination Algorithm
 **/

/** Class GaussianElimination **/
public class GaussianElimination {
    public static double[] solve(double[][] coefficients) {
        int n = coefficients.length;
        int m = coefficients[0].length - 1;

        // Forward elimination
        for (int k = 0; k < n; k++) {
            int pivotRow = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(coefficients[i][k]) > Math.abs(coefficients[pivotRow][k])) {
                    pivotRow = i;
                }
            }
            if (pivotRow != k) {
                double[] temp = coefficients[k];
                coefficients[k] = coefficients[pivotRow];
                coefficients[pivotRow] = temp;
            }
            if (Math.abs(coefficients[k][k]) < 1e-10) {
                // Matrix is singular, no unique solution exists
                return null;
            }
            for (int i = k + 1; i < n; i++) {
                double factor = coefficients[i][k] / coefficients[k][k];
                for (int j = k + 1; j <= m; j++) {
                    coefficients[i][j] -= factor * coefficients[k][j];
                }
            }
        }

        // Back substitution
        double[] solution = new double[n];
        for (int k = n - 1; k >= 0; k--) {
            double sum = 0;
            for (int j = k + 1; j < n; j++) {
                sum += coefficients[k][j] * solution[j];
            }
            solution[k] = (coefficients[k][m] - sum) / coefficients[k][k];
        }
        return solution;
    }
}
