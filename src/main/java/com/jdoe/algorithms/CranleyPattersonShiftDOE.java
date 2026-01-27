package com.jdoe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;

public class CranleyPattersonShiftDOE {

    public static RealMatrix cranleyPattersonShift(double[][] points, Integer seed) {
        if (points == null || points.length == 0 || points[0].length == 0) {
            throw new IllegalArgumentException("Input `points` must be a non-null 2D array with at least one element.");
        }

        RealMatrix pointsMatrix = new Array2DRowRealMatrix(points);

        int nSamples = pointsMatrix.getRowDimension();
        int dim = pointsMatrix.getColumnDimension();

        // Initialize random generator with seed if provided
        RandomDataGenerator rng;
        if (seed != null) {
            JDKRandomGenerator jdkRng = new JDKRandomGenerator();
            jdkRng.setSeed(seed);
            rng = new RandomDataGenerator(jdkRng);  // Use Apache Commons Math generator
        } else {
            rng = new RandomDataGenerator();
        }

        // Generate random shift vector
        double[] shiftVector = new double[dim];
        for (int i = 0; i < dim; i++) {
            shiftVector[i] = rng.nextUniform(0.0, 1.0);
        }

        // Create a matrix with the shift vector replicated for each sample
        double[][] shiftMatrixData = new double[nSamples][dim];
        for (int i = 0; i < nSamples; i++) {
            for (int j = 0; j < dim; j++) {
                shiftMatrixData[i][j] = shiftVector[j];
            }
        }

        RealMatrix shiftMatrix = new Array2DRowRealMatrix(shiftMatrixData);

        // Add the shift vector to all points
        RealMatrix shiftedPoints = pointsMatrix.add(shiftMatrix);

        // Wrap values to [0, 1) using modulo operation
        double[][] resultData = shiftedPoints.getData();
        for (int i = 0; i < nSamples; i++) {
            for (int j = 0; j < dim; j++) {
                resultData[i][j] = resultData[i][j] - Math.floor(resultData[i][j]);
            }
        }

        return new Array2DRowRealMatrix(resultData);
    }

    /**
     * Overloaded method without seed parameter
     */
    public static RealMatrix cranleyPattersonShift(double[][] points) {
        return cranleyPattersonShift(points, null);
    }
}
