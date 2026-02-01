package com.doe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Join two matrices by stacking them on top of each other.
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
public class DoeUnion {

    /**
     * Join two matrices by stacking them on top of each other.
     *
     * @param H1 The matrix that goes on top of the new matrix
     * @param H2 The matrix that goes on bottom of the new matrix
     * @return The new matrix that contains the rows of H1 on top of the rows of H2
     */
    public static RealMatrix matrixUnion(RealMatrix H1, RealMatrix H2) {
        int rows1 = H1.getRowDimension();
        int rows2 = H2.getRowDimension();
        int cols = H1.getColumnDimension();

        // Check that both matrices have the same number of columns
        if (cols != H2.getColumnDimension()) {
            throw new IllegalArgumentException("Both matrices must have the same number of columns");
        }

        // Create a new matrix with the combined rows
        RealMatrix result = new Array2DRowRealMatrix(rows1 + rows2, cols);

        // Copy data from H1
        for (int i = 0; i < rows1; i++) {
            for (int j = 0; j < cols; j++) {
                result.setEntry(i, j, H1.getEntry(i, j));
            }
        }

        // Copy data from H2
        for (int i = 0; i < rows2; i++) {
            for (int j = 0; j < cols; j++) {
                result.setEntry(rows1 + i, j, H2.getEntry(i, j));
            }
        }

        return result;
    }
}
