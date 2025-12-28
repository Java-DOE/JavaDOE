package com.jdoe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class BoxBehnkenDOE {

    public static void boxBehnkenDesign(int totalNumberOfFactors) {

        // validation
        if (totalNumberOfFactors < 3) {
            throw new IllegalArgumentException("design requires atleast 3 factors");
        }

        // making a 2 level design base for the bbdesign
        RealMatrix ff2levelMatrix = FactorialDOE.fullFactorial2Level(2);

        // result matrix dimension calculation
        int numberOfRows = (int) (0.5 * totalNumberOfFactors * (totalNumberOfFactors - 1) * ff2levelMatrix.getRowDimension());

        Array2DRowRealMatrix realMatrixFactory = new Array2DRowRealMatrix();
        RealMatrix resultMatrix = realMatrixFactory.createMatrix(numberOfRows, totalNumberOfFactors);

        // populate result matrix with base matrix
        for (int c = 0; c < ff2levelMatrix.getColumnDimension(); c++) {
            for (int r = 0; r < ff2levelMatrix.getRowDimension(); r++) {
                double entry = ff2levelMatrix.getEntry(r, c);
                resultMatrix.setEntry(r, c, entry);
            }
        }

    }
}
