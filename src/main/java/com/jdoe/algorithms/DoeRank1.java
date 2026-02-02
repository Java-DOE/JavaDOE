package com.doe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Generate a rank-1 lattice design matrix.
 * <p>
 * Rank-1 lattices are quasi-random designs used for numerical integration and
 * high-dimensional sampling. This algorithm generates deterministic points with
 * linear runtime.
 */
public class DoeRank1 {

    /**
     * Generate a rank-1 lattice design matrix.
     *
     * @param numPoints The number of points to generate
     * @param dimension The dimensionality of the space
     * @param generatorVector A generator vector of length dimension. If null, one is randomly generated
     *                        using integers in [2, numPoints)
     * @param randomGenerator RandomGenerator for reproducibility
     * @return The resulting integer-valued rank-1 lattice matrix. Each row represents
     *         a point in the design
     * @throws IllegalArgumentException if generatorVector doesn't match expected shape
     */
    public static RealMatrix rank1Lattice(int numPoints, int dimension, int[] generatorVector, RandomGenerator randomGenerator) {
        if (randomGenerator == null) {
            randomGenerator = new JDKRandomGenerator();
        }

        if (generatorVector == null) {
            generatorVector = new int[dimension];
            for (int i = 0; i < dimension; i++) {
                generatorVector[i] = randomGenerator.nextInt(numPoints - 2) + 2; // Random int in [2, numPoints)
            }
        }

        if (generatorVector.length != dimension) {
            throw new IllegalArgumentException(
                    String.format("Expected generator_vector of length (%d), got %d", dimension, generatorVector.length)
            );
        }

        // Apply modulo operation to ensure all values are within [0, numPoints)
        for (int i = 0; i < generatorVector.length; i++) {
            generatorVector[i] = ((generatorVector[i] % numPoints) + numPoints) % numPoints; // Handle negative values
        }

        // Generate the points using modular arithmetic
        int[][] points = new int[numPoints][dimension];
        for (int i = 0; i < numPoints; i++) {
            for (int j = 0; j < dimension; j++) {
                points[i][j] = (i * generatorVector[j]) % numPoints;
            }
        }

        // Convert to RealMatrix
        double[][] result = new double[numPoints][dimension];
        for (int i = 0; i < numPoints; i++) {
            for (int j = 0; j < dimension; j++) {
                result[i][j] = points[i][j];
            }
        }

        return new Array2DRowRealMatrix(result);
    }

    /**
     * Overloaded method with default random generator
     */
    public static RealMatrix rank1Lattice(int numPoints, int dimension, int[] generatorVector) {
        return rank1Lattice(numPoints, dimension, generatorVector, null);
    }

    /**
     * Overloaded method with default generator vector (null)
     */
    public static RealMatrix rank1Lattice(int numPoints, int dimension) {
        return rank1Lattice(numPoints, dimension, null, null);
    }
}
