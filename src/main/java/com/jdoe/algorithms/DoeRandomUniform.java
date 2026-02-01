package com.doe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Generate random samples from a uniform distribution over [0, 1).
 * <p>
 * This function returns an array of shape [(num_points, dimension)](file:///home/noor/Downloads/pyDOE3-master/doc/conf.py#L31-L36) where each entry
 * is drawn from a uniform distribution on the half-open interval [0.0, 1.0).
 */
public class DoeRandomUniform {

    /**
     * Generate random samples from a uniform distribution over [0, 1).
     *
     * @param numPoints Number of random points to generate (number of rows in the output array)
     * @param dimension Dimensionality of each random point (number of columns in the output array)
     * @param seed Random seed for reproducibility
     * @return An array of shape [(num_points, dimension)](file:///home/noor/Downloads/pyDOE3-master/doc/conf.py#L31-L36) containing random samples
     *         from a uniform distribution over [0, 1)
     */
    public static RealMatrix randomUniform(int numPoints, int dimension, Integer seed) {
        RandomGenerator rng;
        if (seed != null) {
            rng = new JDKRandomGenerator(seed);
        } else {
            rng = new JDKRandomGenerator();
        }

        // Generate the random matrix
        double[][] result = new double[numPoints][dimension];
        for (int i = 0; i < numPoints; i++) {
            for (int j = 0; j < dimension; j++) {
                result[i][j] = rng.nextDouble();
            }
        }

        return new Array2DRowRealMatrix(result);
    }

    /**
     * Overloaded method with default seed (null)
     */
    public static RealMatrix randomUniform(int numPoints, int dimension) {
        return randomUniform(numPoints, dimension, null);
    }
}
