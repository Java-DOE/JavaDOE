package com.doe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Generate Saltelli samples using Sobol' sequences for sensitivity analysis.
 * <p>
 * This module implements Saltelli's sampling scheme based on Sobol' sequences for
 * global sensitivity analysis. It enables estimation of first-order, total-order, and
 * (second-order optional) Sobol' sensitivity indices. The implementation relies on a
 * custom Sobol' sequence generator.
 * <p>
 * Compared to random or Latin Hypercube sampling, this method provides better
 * convergence for variance-based sensitivity analysis using quasi-random low-discrepancy
 * sequences.
 * <p>
 * References:
 * 1. Sobol', I.M., 2001. Global sensitivity indices for nonlinear mathematical models and
 *    their Monte Carlo estimates. Mathematics and Computers in Simulation,
 *    The Second IMACS Seminar on Monte Carlo Methods 55, 271-280.
 * 2. Saltelli, A., 2002. Making best use of model evaluations to compute sensitivity indices.
 *    Computer Physics Communications, 145(2), 280-297.
 * 3. Campolongo, F., Saltelli, A., Cariboni, J., 2011.
 *    From screening to quantitative sensitivity analysis. A unified approach.
 *    Computer Physics Communications 182, 978-988.
 * 4. Owen, A. B., 2020. On dropping the first Sobol' point. arXiv:2008.08051 [cs, math, stat].
 */
public class DoeSaltelli {

    /**
     * Generate Saltelli samples using Sobol' sequences for sensitivity analysis.
     *
     * @param numVars           Number of input variables (dimensions)
     * @param N                 Base sample size (ideally a power of 2)
     * @param calcSecondOrder   If true, include second-order interaction terms. Default is true
     * @param skipValues        Number of Sobol' points to skip. If null, set automatically
     * @param scramble          Whether to use scrambling for Sobol' sequence. Default is false
     * @param seed              Random seed (only used if scramble=true)
     * @return Matrix of shape (N * (2 * num_vars + 2), num_vars) if calc_second_order=true,
     *         or (N * (num_vars + 2), num_vars) otherwise. Contains Saltelli samples in [0, 1]
     */
    public static RealMatrix saltelliSampling(
            int numVars,
            int N,
            boolean calcSecondOrder,
            Integer skipValues,
            boolean scramble,
            Integer seed) {

        int D = numVars;

        // Check if N is a power of 2
        if (!((N & (N - 1)) == 0 && N != 0)) {
            System.out.println("Warning: N = " + N + " is not a power of 2. This may affect Sobol sequence convergence.");
        }

        if (skipValues == null) {
            skipValues = Math.max(powerOfTwoCeil(N), 16);
        }

        // Generate base Sobol samples: shape (N + skip_values, 2*D)
        // Fixed method call to match the correct signature
        RealMatrix base = DoeSobol.sobolSequence(N + skipValues, 2 * D, scramble, seed, null, 0);

        int totalSamples;
        if (calcSecondOrder) {
            totalSamples = N * (2 * D + 2);
        } else {
            totalSamples = N * (D + 2);
        }

        double[][] saltelliMatrix = new double[totalSamples][D];
        int idx = 0;

        for (int i = skipValues; i < skipValues + N; i++) {
            // Extract A and B matrices
            double[] A = new double[D];
            double[] B = new double[D];
            for (int j = 0; j < D; j++) {
                A[j] = base.getEntry(i, j);
                B[j] = base.getEntry(i, D + j);
            }

            // Matrix A
            for (int j = 0; j < D; j++) {
                saltelliMatrix[idx][j] = A[j];
            }
            idx++;

            // Cross A_Bi
            for (int j = 0; j < D; j++) {
                double[] C = A.clone();
                C[j] = B[j];
                for (int k = 0; k < D; k++) {
                    saltelliMatrix[idx][k] = C[k];
                }
                idx++;
            }

            // Cross B_Ai (only if calc_second_order)
            if (calcSecondOrder) {
                for (int j = 0; j < D; j++) {
                    double[] C = B.clone();
                    C[j] = A[j];
                    for (int k = 0; k < D; k++) {
                        saltelliMatrix[idx][k] = C[k];
                    }
                    idx++;
                }
            }

            // Matrix B
            for (int j = 0; j < D; j++) {
                saltelliMatrix[idx][j] = B[j];
            }
            idx++;
        }

        return new Array2DRowRealMatrix(saltelliMatrix);
    }

    /**
     * Overloaded method with default parameters
     */
    public static RealMatrix saltelliSampling(int numVars, int N) {
        return saltelliSampling(numVars, N, true, null, false, null);
    }

    /**
     * Overloaded method with calcSecondOrder parameter
     */
    public static RealMatrix saltelliSampling(int numVars, int N, boolean calcSecondOrder) {
        return saltelliSampling(numVars, N, calcSecondOrder, null, false, null);
    }

    /**
     * Overloaded method with calcSecondOrder and skipValues parameters
     */
    public static RealMatrix saltelliSampling(int numVars, int N, boolean calcSecondOrder, int skipValues) {
        return saltelliSampling(numVars, N, calcSecondOrder, skipValues, false, null);
    }

    /**
     * Calculate the smallest power of 2 that is greater than or equal to n
     */
    private static int powerOfTwoCeil(int n) {
        if (n <= 1) return 1;
        int result = 1;
        while (result < n) {
            result <<= 1;
        }
        return result;
    }
}
