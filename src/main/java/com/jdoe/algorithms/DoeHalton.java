package com.doe.algorithms;

import org.apache.commons.math3.util.Precision;

/**
 * This module implements the Halton sequence, a low-discrepancy quasirandom sequence
 * used in numerical integration, sampling, and global optimization tasks.
 *
 * The Halton sequence generates points in a unit hypercube [0, 1]^d using radical
 * inversion with respect to a sequence of prime number bases. It is especially
 * useful in high-dimensional integration where uniformity and low correlation
 * between sample points are desired.
 *
 * Each dimension in the Halton sequence uses a unique base (a prime number), and
 * points are computed using the van der Corput sequence in that base.
 */
public class DoeHalton {

    /**
     * Generate a Halton sequence in a given dimension.
     *
     * The Halton sequence is a low-discrepancy, quasi-random point set commonly
     * used in numerical integration, sampling, and global optimization. Each
     * dimension uses a different prime base to generate values via the van der Corput
     * sequence.
     *
     * @param numPoints Number of points to generate in the sequence
     * @param dimension Number of dimensions (features) of the sequence
     * @param skip Number of initial points in the sequence to skip (default 0)
     * @return The generated Halton sequence points
     */
    public static double[][] haltonSequence(int numPoints, int dimension, int skip) {
        int[] bases = nextPrimes(dimension);

        // Preallocate the output array
        double[][] samples = new double[numPoints][dimension];

        for (int dim = 0; dim < dimension; dim++) {
            int base = bases[dim];
            for (int i = 0; i < numPoints; i++) {
                int index = i + skip;
                samples[i][dim] = vanDerCorput(index, base);
            }
        }

        return samples;
    }

    /**
     * Overloaded method with default skip value (0)
     */
    public static double[][] haltonSequence(int numPoints, int dimension) {
        return haltonSequence(numPoints, dimension, 0);
    }

    /**
     * Compute a single value of the van der Corput sequence.
     *
     * The van der Corput sequence generates low-discrepancy values in [0, 1) using
     * radical inversion in a specified base.
     *
     * @param index The index in the sequence
     * @param base The base to use (must be >= 2)
     * @return The van der Corput value at the given index and base
     */
    private static double vanDerCorput(int index, int base) {
        double result = 0.0;
        double f = 1.0 / base;
        int currentIndex = index;

        while (currentIndex > 0) {
            int mod = currentIndex % base;
            result += mod * f;
            f /= base;
            currentIndex /= base;
        }

        return result;
    }

    /**
     * Generate the first n prime numbers.
     *
     * @param n Number of prime numbers to generate
     * @return Array containing the first n prime numbers
     */
    private static int[] nextPrimes(int n) {
        int[] primes = new int[n];
        int count = 0;
        int candidate = 2;

        while (count < n) {
            if (isPrime(candidate)) {
                primes[count] = candidate;
                count++;
            }
            candidate++;
        }

        return primes;
    }

    /**
     * Check whether a number is prime.
     *
     * @param n The number to check
     * @return True if n is a prime number, False otherwise
     */
    private static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        if (n == 2) {
            return true;
        }
        if (n % 2 == 0) {
            return false;
        }

        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }
}
