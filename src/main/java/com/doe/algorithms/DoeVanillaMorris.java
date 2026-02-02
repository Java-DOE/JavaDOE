package com.doe.algorithms;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generate samples using the Morris Method (Vanilla, no optimization).
 * <p>
 * This module implements the original (unoptimized) Morris method for
 * global sensitivity analysis, a computationally efficient screening
 * technique that estimates the importance of input variables by analyzing
 * one-at-a-time (OAT) trajectories through a discretized parameter space.
 * It is especially useful for identifying influential parameters in
 * high-dimensional models with relatively low computational cost.
 * <p>
 * References:
 * 1. Morris, M.D., 1991. Factorial Sampling Plans for Preliminary Computational Experiments.
 *    Technometrics 33, 161-174.
 * 2. Campolongo, F., Cariboni, J., &amp; Saltelli, A., 2007.
 *    An effective screening design for sensitivity analysis of large models.
 *    Environmental Modelling &amp; Software, 22(10), 1509-1518.
 * 3. Ruano, M.V., Ribes, J., Seco, A., Ferrer, J., 2012.
 *    An improved sampling strategy based on trajectory design for application
 *    of the Morris method to systems with many input factors.
 *    Environmental Modelling &amp; Software 37, 103-109.
 */
public class DoeVanillaMorris {

    /**
     * Generate samples using the Morris Method (Vanilla, no optimization).
     *
     * @param numVars   Number of input variables (i.e., the dimensionality of the problem)
     * @param N         Number of trajectories to generate
     * @param numLevels Number of levels in the grid (must be even). Default is 4
     * @param seed      Random seed for reproducibility
     * @return Matrix of shape (N * (num_vars + 1), num_vars) with Morris samples
     * @throws IllegalArgumentException if numLevels is not even
     */
    public static RealMatrix morrisSampling(int numVars, int N, int numLevels, Integer seed) {
        if (numLevels % 2 != 0) {
            throw new IllegalArgumentException("numLevels must be an even number");
        }

        RandomGenerator rng;
        if (seed != null) {
            rng = new JDKRandomGenerator(seed);
        } else {
            rng = new JDKRandomGenerator();
        }

        int D = numVars;

        double delta = (double) numLevels / (2 * (numLevels - 1));
        double[] G = linspace(0, 1 - delta, numLevels / 2);

        List<RealMatrix> samples = new ArrayList<>();

        for (int traj = 0; traj < N; traj++) {
            // Starting point x* on the grid
            double[] xStar = new double[D];
            for (int i = 0; i < D; i++) {
                xStar[i] = G[rng.nextInt(G.length)];
            }


            // Diagonal matrix of directions (+1 or -1)
            RealMatrix dStar = createDiagonalMatrix(D, () -> rng.nextBoolean() ? 1.0 : -1.0);
            // Lower-triangular B matrix
            RealMatrix B = createLowerTriangularMatrix(D + 1, D);

            // Random permutation matrix P*
            RealMatrix pStar = createRandomPermutationMatrix(D, rng);

            // J: ones matrix
            RealMatrix J = new Array2DRowRealMatrix(D + 1, D);
            for (int i = 0; i < D + 1; i++) {
                for (int j = 0; j < D; j++) {
                    J.setEntry(i, j, 1.0);
                }
            }

            // Construct B* (trajectory matrix)
            // B_star = x_star + delta / 2 * ((2 * B @ P_star - J) @ D_star + J)
            RealMatrix BPStar = B.multiply(pStar);
            RealMatrix twoBPStar = BPStar.scalarMultiply(2);
            RealMatrix twoBPStarMinusJ = twoBPStar.subtract(J);
            RealMatrix result = twoBPStarMinusJ.multiply(dStar);
            result = result.add(J);
            result = result.scalarMultiply(delta / 2);

            // Add xStar to each row of the result
            for (int i = 0; i < result.getRowDimension(); i++) {
                for (int j = 0; j < result.getColumnDimension(); j++) {
                    result.setEntry(i, j, result.getEntry(i, j) + xStar[j]);
                }
            }

            samples.add(result);
        }

        // Combine all trajectory matrices
        if (samples.isEmpty()) {
            return new Array2DRowRealMatrix(0, D);
        }

        int totalRows = 0;
        for (RealMatrix matrix : samples) {
            totalRows += matrix.getRowDimension();
        }

        RealMatrix finalResult = new Array2DRowRealMatrix(totalRows, D);
        int currentRow = 0;
        for (RealMatrix matrix : samples) {
            int rows = matrix.getRowDimension();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < D; j++) {
                    finalResult.setEntry(currentRow + i, j, matrix.getEntry(i, j));
                }
            }
            currentRow += rows;
        }

        return finalResult;
    }

    /**
     * Overloaded method with default numLevels (4)
     */
    public static RealMatrix morrisSampling(int numVars, int N, Integer seed) {
        return morrisSampling(numVars, N, 4, seed);
    }

    /**
     * Overloaded method with default numLevels (4) and seed (null)
     */
    public static RealMatrix morrisSampling(int numVars, int N) {
        return morrisSampling(numVars, N, 4, null);
    }

    /**
     * Create a diagonal matrix with values generated by the provided supplier
     */
    private static RealMatrix createDiagonalMatrix(int size, java.util.function.Supplier<Double> valueSupplier) {
        RealMatrix matrix = new Array2DRowRealMatrix(size, size);
        for (int i = 0; i < size; i++) {
            matrix.setEntry(i, i, valueSupplier.get());
        }
        return matrix;
    }

    /**
     * Create a lower-triangular matrix
     */
    private static RealMatrix createLowerTriangularMatrix(int rows, int cols) {
        RealMatrix matrix = new Array2DRowRealMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i > j) {
                    matrix.setEntry(i, j, 1.0);
                }
            }
        }
        return matrix;
    }

    /**
     * Create a random permutation matrix
     */
    private static RealMatrix createRandomPermutationMatrix(int size, RandomGenerator rng) {
        RealMatrix matrix = new Array2DRowRealMatrix(size, size);
        for (int i = 0; i < size; i++) {
            matrix.setEntry(i, i, 1.0);
        }

        // Perform random swaps to create a permutation matrix
        for (int i = size - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            if (i != j) {
                // Swap rows i and j
                for (int k = 0; k < size; k++) {
                    double temp = matrix.getEntry(i, k);
                    matrix.setEntry(i, k, matrix.getEntry(j, k));
                    matrix.setEntry(j, k, temp);
                }
            }
        }

        return matrix;
    }

    /**
     * Create a linearly spaced array
     */
    private static double[] linspace(double start, double end, int num) {
        if (num == 1) {
            return new double[]{start};
        }
        double[] result = new double[num];
        double step = (end - start) / (num - 1);
        for (int i = 0; i < num; i++) {
            result[i] = start + i * step;
        }
        return result;
    }
}