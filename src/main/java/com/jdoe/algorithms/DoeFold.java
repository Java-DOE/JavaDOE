package com.jdoe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class DoeFold {

    /**
     * Fold a design to reduce confounding effects.
     *
     * @param H The design matrix to be folded
     * @param columns Indices of columns to fold (Default: null). If columns=null is
     *                used, then all columns will be folded.
     * @return The folded design matrix
     */
    public static RealMatrix fold(double[][] H, int[] columns) {
        if (H.length == 0 || H[0].length == 0 || H.length != H[0].length) {
            throw new IllegalArgumentException("Input design matrix must be 2d.");
        }

        int nCols = H[0].length;

        if (columns == null) {
            columns = new int[nCols];
            for (int i = 0; i < nCols; i++) {
                columns[i] = i;
            }
        }

        double[][] Hf = new double[H.length][H[0].length];

        // Copy original matrix to Hf
        for (int i = 0; i < H.length; i++) {
            System.arraycopy(H[i], 0, Hf[i], 0, H[0].length);
        }

        for (int col : columns) {
            // Get unique values in column
            double[] vals = getUniqueValues(getColumn(H, col));

            if (vals.length != 2) {
                throw new IllegalArgumentException("Input design matrix must be 2-level factors only.");
            }

            for (int i = 0; i < H.length; i++) {
                Hf[i][col] = (H[i][col] == vals[1]) ? vals[0] : vals[1];
            }
        }

        // Combine original and folded matrices
        double[][] result = new double[H.length * 2][H[0].length];

        // Copy original matrix
        for (int i = 0; i < H.length; i++) {
            System.arraycopy(H[i], 0, result[i], 0, H[0].length);
        }

        // Copy folded matrix
        for (int i = 0; i < Hf.length; i++) {
            System.arraycopy(Hf[i], 0, result[H.length + i], 0, Hf[0].length);
        }

        return new Array2DRowRealMatrix(result);
    }

    /**
     * Overloaded method with default columns (all columns)
     */
    public static RealMatrix fold(double[][] H) {
        return fold(H, null);
    }

    /**
     * Helper method to extract a column from a 2D array
     */
    private static double[] getColumn(double[][] matrix, int colIndex) {
        double[] column = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            column[i] = matrix[i][colIndex];
        }
        return column;
    }

    /**
     * Helper method to get unique values from an array
     */
    private static double[] getUniqueValues(double[] arr) {
        java.util.Set<Double> uniqueSet = new java.util.LinkedHashSet<>();
        for (double val : arr) {
            uniqueSet.add(val);
        }
        return uniqueSet.stream().mapToDouble(Double::doubleValue).toArray();
    }
}

