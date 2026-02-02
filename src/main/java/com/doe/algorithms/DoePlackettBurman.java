package com.doe.algorithms;

import org.apache.commons.math3.linear.*;

/**
 * Generate a Plackett-Burman design
 * <p>
 * This code was originally published by the following individuals for use with
 * Scilab:
 * Copyright (C) 2012 - 2013 - Michael Baudin
 * Copyright (C) 2012 - Maria Christopoulou
 * Copyright (C) 2010 - 2011 - INRIA - Michael Baudin
 * Copyright (C) 2009 - Yann Collette
 * Copyright (C) 2009 - CEA - Jean-Marc Martinez
 * <p>
 * Much thanks goes to these individuals. It has been converted to Python by
 * Abraham Lee.
 */
public class DoePlackettBurman {

    /**
     * Generate a Plackett-Burman design
     *
     * @param n The number of factors to create a matrix for
     * @return An orthogonal design matrix with n columns, one for each factor, and
     * the number of rows being the next multiple of 4 higher than n (e.g.,
     * for 1-3 factors there are 4 rows, for 4-7 factors there are 8 rows,
     * etc.)
     * @throws IllegalArgumentException if n is not a positive integer
     */
    public static RealMatrix pbdesign(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number of factors must be a positive integer");
        }

        int keep = n;
        n = 4 * (int) Math.ceil(n / 4.0);  // calculate the correct number of rows (multiple of 4)

        // Check if n is a valid multiple of 4
        if (!isValidMultipleOfFour(n)) {
            throw new IllegalArgumentException("Invalid inputs. n must be a multiple of 4.");
        }

        RealMatrix H;

        // Determine which base matrix to use based on n
        if (n == 4) {
            // N = 4 (base case)
            H = new Array2DRowRealMatrix(new double[][]{{1}});
        } else if (n == 12) {
            // N = 12
            H = createTwelveByTwelveMatrix();
        } else if (n == 20) {
            // N = 20
            H = createTwentyByTwentyMatrix();
        } else {
            // For larger matrices, find the base size and build up
            int baseSize = findBaseSize(n);
            if (baseSize == 4) {
                H = new Array2DRowRealMatrix(new double[][]{{1}});
            } else if (baseSize == 12) {
                H = createTwelveByTwelveMatrix();
            } else if (baseSize == 20) {
                H = createTwentyByTwentyMatrix();
            } else {
                throw new IllegalArgumentException("Unsupported matrix size: " + n);
            }

            // Calculate how many times to expand the base matrix
            int expansionFactor = n / H.getRowDimension();
            int power = (int) (Math.log(expansionFactor) / Math.log(2));

            // Kronecker product construction
            for (int i = 0; i < power; i++) {
                H = kroneckerExpansion(H);
            }
        }

        // Reduce the size of the matrix as needed
        int numRows = H.getRowDimension();
        int numCols = Math.min(H.getColumnDimension(), keep + 1);
        RealMatrix result = new Array2DRowRealMatrix(numRows, numCols);
        for (int i = 0; i < numRows; i++) {
            for (int j = 1; j < numCols; j++) {  // Skip first column (index 0)
                result.setEntry(i, j - 1, H.getEntry(i, j));
            }
        }

        // Flip the matrix upside down
        return flipud(result);
    }

    /**
     * Check if n is a valid multiple of 4 that can be formed using the allowed base matrices
     */
    private static boolean isValidMultipleOfFour(int n) {
        // Check if n can be expressed as one of the base sizes multiplied by powers of 2
        while (n % 2 == 0) {
            if (n == 4 || n == 12 || n == 20) {
                return true;
            }
            n /= 2;
        }
        return n == 4 || n == 12 || n == 20;
    }

    /**
     * Find the base size for a given n
     */
    private static int findBaseSize(int n) {
        int temp = n;
        while (temp % 2 == 0) {
            if (temp == 4 || temp == 12 || temp == 20) {
                return temp;
            }
            temp /= 2;
        }
        return temp;  // Should be 4, 12, or 20
    }

    /**
     * Create the 12x12 base matrix
     */
    private static RealMatrix createTwelveByTwelveMatrix() {
        double[][] matrix = new double[12][12];

        // First row: all ones
        for (int j = 0; j < 12; j++) {
            matrix[0][j] = 1;
        }

        // Remaining rows
        for (int i = 1; i < 12; i++) {
            matrix[i][0] = 1;  // First column
        }

        // Toeplitz matrix part
        int[] colFirst = {-1, -1, 1, -1, -1, -1, 1, 1, 1, -1, 1};  // First column after first element
        int[] rowFirst = {-1, 1, -1, 1, 1, 1, -1, -1, -1, 1, -1};  // First row after first element

        for (int i = 1; i < 12; i++) {
            for (int j = 1; j < 12; j++) {
                if (i <= j) {
                    // Moving up along anti-diagonals
                    int idx = j - i;
                    matrix[i][j] = (idx < rowFirst.length) ? rowFirst[idx] : matrix[i-1][j-1];
                } else {
                    // Moving down along anti-diagonals
                    int idx = i - j;
                    matrix[i][j] = (idx < colFirst.length) ? colFirst[idx] : matrix[i-1][j-1];
                }
            }
        }

        return new Array2DRowRealMatrix(matrix);
    }

    /**
     * Create the 20x20 base matrix
     */
    private static RealMatrix createTwentyByTwentyMatrix() {
        double[][] matrix = new double[20][20];

        // First row: all ones
        for (int j = 0; j < 20; j++) {
            matrix[0][j] = 1;
        }

        // Remaining rows
        for (int i = 1; i < 20; i++) {
            matrix[i][0] = 1;  // First column
        }

        // Hankel matrix part
        int[] colFirst = {-1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, 1, 1, 1, -1, -1, 1};
        int[] rowLast = {1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, 1, 1, 1, -1, -1};

        for (int i = 1; i < 20; i++) {
            for (int j = 1; j < 20; j++) {
                int idx = i + j - 2;  // For Hankel: constant along anti-diagonals
                if (idx < colFirst.length) {
                    matrix[i][j] = colFirst[idx];
                } else if (idx - (colFirst.length - 1) < rowLast.length) {
                    matrix[i][j] = rowLast[idx - (colFirst.length - 1)];
                } else {
                    // Fallback: replicate pattern
                    matrix[i][j] = matrix[i-1][j-1];
                }
            }
        }

        return new Array2DRowRealMatrix(matrix);
    }

    /**
     * Perform Kronecker expansion of the matrix
     */
    private static RealMatrix kroneckerExpansion(RealMatrix H) {
        int rows = H.getRowDimension();
        int cols = H.getColumnDimension();

        RealMatrix expanded = new Array2DRowRealMatrix(2 * rows, 2 * cols);

        // Upper left: H
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                expanded.setEntry(i, j, H.getEntry(i, j));
            }
        }

        // Upper right: H
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                expanded.setEntry(i, j + cols, H.getEntry(i, j));
            }
        }

        // Lower left: H
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                expanded.setEntry(i + rows, j, H.getEntry(i, j));
            }
        }

        // Lower right: -H
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                expanded.setEntry(i + rows, j + cols, -H.getEntry(i, j));
            }
        }

        return expanded;
    }

    /**
     * Flip the matrix upside down
     */
    private static RealMatrix flipud(RealMatrix matrix) {
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        RealMatrix flipped = new Array2DRowRealMatrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flipped.setEntry(i, j, matrix.getEntry(rows - 1 - i, j));
            }
        }

        return flipped;
    }
}
