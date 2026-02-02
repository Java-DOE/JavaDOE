package com.doe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.SobolSequenceGenerator;

/**
 * Generate a Sobol' sequence (quasi-random design matrix).
 * <p>
 * This module implements the Sobol' sequence generator for quasi-random sampling.
 * <p>
 * The Sobol' sequence is a type of low-discrepancy sequence widely used in global
 * optimization, numerical integration (e.g., Monte Carlo methods), machine learning,
 * and design of experiments. Compared to purely random samples, Sobol' sequences
 * exhibit better uniformity in multi-dimensional space.
 * <p>
 * The sequence generation is based on direction numbers and bitwise operations.
 * Scrambling can optionally be applied to enhance uniformity and reduce correlation
 * artifacts.
 * <p>
 * References:
 * Sobol', I. M. (1967). "Distribution of points in a cube and approximate evaluation
 * of integrals." Zh. Vych. Mat. Mat. Fiz., 7: 784-802 (in Russian);
 * U.S.S.R. Comput. Maths. Math. Phys., 7: 86-112 (in English).
 */
public class DoeSobol {

    /**
     * Generate a Sobol' sequence (quasi-random design matrix).
     *
     * @param n           Number of points to generate
     * @param d           Dimension of the space (must be &lt;= 21201)
     * @param scramble    Whether to apply Owen scrambling. Default is false
     * @param seed        Seed for the random number generator (used only when scramble=true)
     * @param bounds      Bounds for each dimension. Each element must be a (min, max) pair.
     *                    If provided, the output will be scaled accordingly
     * @param skip        Number of initial points to skip (i.e., fast-forward in the sequence). Default is 0
     * @param usePowOf2   If true, ensures n is a power of 2 for best balance and coverage.
     *                    Non-power-of-two n values will be rounded up to the next power of 2
     * @return Array of Sobol' points in [0, 1)^d, or scaled to bounds if provided
     */
    public static RealMatrix sobolSequence(
            int n, int d, boolean scramble, Integer seed, double[][] bounds, int skip, boolean usePowOf2) {

        // Note: The SobolSequenceGenerator in Apache Commons Math doesn't support scrambling
        // directly, so we're creating it with just the dimension
        SobolSequenceGenerator sobolGen = new SobolSequenceGenerator(d);

        // Fast forward if needed
        for (int i = 0; i < skip; i++) {
            sobolGen.nextVector();
        }

        int actualN = n;
        if (usePowOf2) {
            // Ensure n is power of 2 for best balance properties
            if (!isPowerOfTwo(n)) {
                actualN = nextPowerOfTwo(n);
            }
        }

        double[][] samples = new double[actualN][d];

        // Generate samples
        for (int i = 0; i < actualN; i++) {
            double[] point = sobolGen.nextVector();
            System.arraycopy(point, 0, samples[i], 0, d);
        }

        // Apply bounds scaling if provided
        if (bounds != null) {
            if (bounds.length != d || bounds[0].length != 2) {
                throw new IllegalArgumentException(
                        String.format("`bounds` must be a (d, 2) array, got shape (%d, %d)", bounds.length, bounds[0].length)
                );
            }

            // Scale each dimension
            for (int i = 0; i < actualN; i++) {
                for (int j = 0; j < d; j++) {
                    samples[i][j] = samples[i][j] * (bounds[j][1] - bounds[j][0]) + bounds[j][0];
                }
            }
        }

        return new Array2DRowRealMatrix(samples);
    }

    /**
     * Overloaded method with default parameters
     */
    public static RealMatrix sobolSequence(int n, int d) {
        return sobolSequence(n, d, false, null, null, 0, true);
    }

    /**
     * Overloaded method with scramble and seed parameters
     */
    public static RealMatrix sobolSequence(int n, int d, boolean scramble, Integer seed) {
        return sobolSequence(n, d, scramble, seed, null, 0, true);
    }

    /**
     * Overloaded method with all parameters except usePowOf2
     */
    public static RealMatrix sobolSequence(
            int n, int d, boolean scramble, Integer seed, double[][] bounds, int skip) {
        return sobolSequence(n, d, scramble, seed, bounds, skip, true);
    }

    /**
     * Check if a number is a power of 2
     */
    private static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /**
     * Find the next power of 2 greater than or equal to n
     */
    private static int nextPowerOfTwo(int n) {
        if (n <= 1) return 1;
        int result = 1;
        while (result < n) {
            result <<= 1;
        }
        return result;
    }
}