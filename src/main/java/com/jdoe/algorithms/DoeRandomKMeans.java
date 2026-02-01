package com.doe.algorithms;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * MacQueen's K-Means algorithm.
 * <p>
 * This implementation generates cluster centers using the MacQueen's K-Means algorithm
 * in a unit hypercube [0, 1]^dimension.
 */
public class DoeRandomKMeans {

    /**
     * MacQueen's K-Means algorithm.
     *
     * @param numPoints      Number of cluster centers to generate
     * @param dimension      Dimensionality of the space
     * @param numSteps       Number of iterations. Defaults to 100 * numPoints
     * @param initialPoints  Initial cluster centers. If null, random points in [0, 1]^dimension are used
     * @param randomGenerator RandomGenerator for reproducibility
     * @return Array of shape (numPoints, dimension) containing the cluster centers
     * @throws IllegalArgumentException if initialPoints doesn't match expected shape or bounds
     */
    public static RealMatrix randomKMeans(
            int numPoints,
            int dimension,
            Integer numSteps,
            RealMatrix initialPoints,
            RandomGenerator randomGenerator) {

        if (randomGenerator == null) {
            randomGenerator = new JDKRandomGenerator();
        }

        if (numSteps == null) {
            numSteps = 100 * numPoints;
        }

        // Initialize cluster centers
        RealMatrix clusterCenters;
        if (initialPoints == null) {
            clusterCenters = generateRandomMatrix(numPoints, dimension, randomGenerator);
        } else {
            if (initialPoints.getRowDimension() != numPoints || initialPoints.getColumnDimension() != dimension) {
                throw new IllegalArgumentException("initialPoints must have shape (numPoints, dimension)");
            }

            // Validate that all points are in [0, 1]^dimension
            for (int i = 0; i < initialPoints.getRowDimension(); i++) {
                for (int j = 0; j < initialPoints.getColumnDimension(); j++) {
                    double value = initialPoints.getEntry(i, j);
                    if (value < 0.0 || value > 1.0) {
                        throw new IllegalArgumentException("initialPoints must be in [0, 1]^dimension");
                    }
                }
            }

            clusterCenters = initialPoints.copy();
        }

        // Initialize counts for incremental mean
        double[] counts = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            counts[i] = 1.0;
        }

        for (int step = 0; step < numSteps; step++) {
            // Sample a random point in the unit hypercube
            double[] x = new double[dimension];
            for (int i = 0; i < dimension; i++) {
                x[i] = randomGenerator.nextDouble();
            }

            // Compute Euclidean distances to cluster centers
            double[] distances = new double[numPoints];
            for (int i = 0; i < numPoints; i++) {
                double sumSquares = 0.0;
                for (int j = 0; j < dimension; j++) {
                    double diff = clusterCenters.getEntry(i, j) - x[j];
                    sumSquares += diff * diff;
                }
                distances[i] = Math.sqrt(sumSquares);
            }

            // Find nearest cluster center
            int idx = 0;
            double minDist = distances[0];
            for (int i = 1; i < numPoints; i++) {
                if (distances[i] < minDist) {
                    minDist = distances[i];
                    idx = i;
                }
            }

            // Update cluster center incrementally (MacQueen's update)
            for (int j = 0; j < dimension; j++) {
                double newValue = (counts[idx] * clusterCenters.getEntry(idx, j) + x[j]) / (counts[idx] + 1);
                clusterCenters.setEntry(idx, j, newValue);
            }
            counts[idx] += 1;
        }

        return clusterCenters;
    }

    /**
     * Overloaded method with default parameters
     */
    public static RealMatrix randomKMeans(int numPoints, int dimension) {
        return randomKMeans(numPoints, dimension, null, null, null);
    }

    /**
     * Overloaded method with numSteps parameter
     */
    public static RealMatrix randomKMeans(int numPoints, int dimension, int numSteps) {
        return randomKMeans(numPoints, dimension, numSteps, null, null);
    }

    /**
     * Generate a random matrix with values in [0, 1]
     */
    private static RealMatrix generateRandomMatrix(int rows, int cols, RandomGenerator randomGenerator) {
        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = randomGenerator.nextDouble();
            }
        }
        return new Array2DRowRealMatrix(matrix);
    }
}
