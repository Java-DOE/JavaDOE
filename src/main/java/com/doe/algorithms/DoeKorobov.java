package com.doe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Random;

/**
 * This module implements the Korobov lattice generator for quasi-random sampling.
 * <p>
 * Korobov lattices are a subclass of rank-1 lattice rules used for generating
 * low-discrepancy sequences. These sequences are widely applied in quasi-Monte Carlo
 * methods, global optimization, and numerical integration of high-dimensional functions.
 * <p>
 * The construction uses modular arithmetic to build the generator vector using a
 * single integer parameter. When the number of points and the generator are coprime,
 * the resulting design exhibits Latin Hypercube-like properties with excellent
 * uniform coverage.
 * <p>
 * This implementation is based on a simplified rank-1 lattice formulation and
 * uses a linear-time algorithm.
 */
public class DoeKorobov {

    /**
     * Generate a Korobov lattice design matrix.
     * <p>
     * Korobov lattices form a class of low-discrepancy sequences for quasi-Monte Carlo
     * methods. They are constructed using a generator vector derived from modular
     * exponentiation of a single integer. The generated matrix represents samples in
     * a uniform virtual grid.
     *
     * @param numPoints       Number of design points to generate
     * @param dimension       Number of dimensions in the design space
     * @param generatorParam  Generator parameter used in modular construction. If null, a random value
     *                        in [2, numPoints) is selected
     * @return design Integer-valued design matrix corresponding to bins on a modular grid
     * @throws IllegalArgumentException if generatorParam is not greater than 1
     * @note The Korobov method is a special case of a rank-1 lattice. The generator vector
     * is defined as: z_i = (a^i) mod N for i = 0 to d-1, where a is the generatorParam and N is numPoints.
     * The resulting design has uniformity properties ideal for integration and
     * high-dimensional optimization.
     * <p>
     * To ensure good coverage, it's recommended that gcd(generatorParam, numPoints) == 1.
     */
    public static RealMatrix korobovSequence(int numPoints, int dimension, Integer generatorParam) {
        int actualGeneratorParam = generatorParam != null ? generatorParam : new Random().nextInt(numPoints - 2) + 2;
        actualGeneratorParam %= numPoints;
        if (actualGeneratorParam <= 1) {
            throw new IllegalArgumentException("generatorParam must be greater than 1.");
        }

        int[] generatorVector = new int[dimension];
        generatorVector[0] = 1;
        for (int i = 1; i < dimension; i++) {
            generatorVector[i] = (actualGeneratorParam * generatorVector[i - 1]) % numPoints;
        }

        return rank1Lattice(numPoints, dimension, generatorVector);
    }

    /**
     * Overloaded method with default generatorParam (null)
     */
    public static RealMatrix korobovSequence(int numPoints, int dimension) {
        return korobovSequence(numPoints, dimension, null);
    }

    /**
     * Internal implementation of rank-1 lattice generation
     * This is a placeholder method that would need to be implemented separately
     * based on the actual rank1_lattice function from Python
     */
    private static RealMatrix rank1Lattice(int numPoints, int dimension, int[] generatorVector) {
        double[][] result = new double[numPoints][dimension];

        for (int i = 0; i < numPoints; i++) {
            for (int j = 0; j < dimension; j++) {
                result[i][j] = ((i * generatorVector[j]) % numPoints) / (double) numPoints;
            }
        }

        return new Array2DRowRealMatrix(result);
    }
}
