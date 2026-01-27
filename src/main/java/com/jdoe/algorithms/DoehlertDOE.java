package com.jdoe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import java.util.ArrayList;
import java.util.List;

public class DoehlertDOE {

    /**
     * Generate a Doehlert design matrix for a given number of factors using a shell approach.
     *
     * @param numFactors Number of factors (variables)
     * @param numCenterPoints Number of center (replicate) points
     * @return Design matrix of shape (N, numFactors), where N = k^2 + k + C
     */
    public static RealMatrix doehlertShellDesign(int numFactors, int numCenterPoints) {
        if (numFactors < 1) {
            throw new IllegalArgumentException("Number of factors must be at least 1.");
        }

        List<double[]> designPoints = new ArrayList<>();

        // Add center points
        for (int i = 0; i < numCenterPoints; i++) {
            double[] centerPoint = new double[numFactors];
            for (int j = 0; j < numFactors; j++) {
                centerPoint[j] = 0.0;
            }
            designPoints.add(centerPoint);
        }

        // Add shells progressively
        for (int shellIndex = 1; shellIndex <= numFactors; shellIndex++) {
            int numShellPoints = shellIndex + 1;
            double radius = Math.sqrt(shellIndex / (double) numFactors);

            for (int pointIdx = 0; pointIdx < numShellPoints; pointIdx++) {
                double[] point = new double[numFactors];
                double angle = (2.0 * Math.PI * pointIdx) / numShellPoints;

                if (numFactors > 0) point[0] = radius * Math.cos(angle);
                if (numFactors > 1) point[1] = radius * Math.sin(angle);

                designPoints.add(point);
            }
        }

        // Convert list of points to matrix
        double[][] result = new double[designPoints.size()][numFactors];
        for (int i = 0; i < designPoints.size(); i++) {
            result[i] = designPoints.get(i);
        }

        return new Array2DRowRealMatrix(result);
    }

    /**
     * Overloaded method with default center points (1)
     */
    public static RealMatrix doehlertShellDesign(int numFactors) {
        return doehlertShellDesign(numFactors, 1);
    }

    /**
     * Generate a Doehlert design matrix using a simplex-based approach.
     *
     * @param numFactors Number of factors included in the design
     * @return Design matrix with experiments at different coded levels
     */
    public static RealMatrix doehlertSimplexDesign(int numFactors) {
        double[][] simplexMatrix = new double[numFactors + 1][numFactors];

        for (int i = 1; i <= numFactors; i++) {
            for (int j = 1; j <= i; j++) {
                if (j == i) {
                    simplexMatrix[i][j - 1] = Math.sqrt(i + 1) / Math.sqrt(2 * i);
                } else {
                    int denom = 2 * (i - (j - 1)) * (i - j);
                    simplexMatrix[i][i - j] = 1 / Math.sqrt(denom);
                }
            }
        }

        List<double[]> extraPoints = new ArrayList<>();
        for (int i = 0; i <= numFactors; i++) {
            List<Integer> rangeList = new ArrayList<>();
            for (int k = 1; k < i; k++) rangeList.add(k);
            for (int k = i + 1; k <= numFactors; k++) rangeList.add(k);

            for (int j : rangeList) {
                double[] point = new double[numFactors];
                for (int k = 0; k < numFactors; k++) {
                    point[k] = simplexMatrix[i][k] - simplexMatrix[j][k];
                }
                extraPoints.add(point);
            }
        }

        // Combine matrices
        int totalRows = (numFactors + 1) + extraPoints.size();
        double[][] fullMatrix = new double[totalRows][numFactors];

        // Copy simplex matrix
        for (int i = 0; i < simplexMatrix.length; i++) {
            fullMatrix[i] = simplexMatrix[i];
        }

        // Add extra points
        for (int i = 0; i < extraPoints.size(); i++) {
            fullMatrix[simplexMatrix.length + i] = extraPoints.get(i);
        }

        return new Array2DRowRealMatrix(fullMatrix);
    }

}
