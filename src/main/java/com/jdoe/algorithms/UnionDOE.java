package com.jdoe.algorithms;

import com.jdoe.util.GenericDOEUtil;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class UnionDOE {
    public static RealMatrix matrixUnion(RealMatrix firstMatrix, RealMatrix secondMatrix) {
        int totalRows = firstMatrix.getRowDimension() + secondMatrix.getRowDimension();
        int cols = firstMatrix.getColumnDimension();

        // Verify both matrices have the same number of columns
        if (cols != secondMatrix.getColumnDimension()) {
            throw new IllegalArgumentException("Both matrices must have the same number of columns");
        }

        double[][] finalMatrixRowModel = new double[totalRows][cols];

        // Copy rows from the first matrix
        for (int i = 0; i < firstMatrix.getRowDimension(); i++) {
            finalMatrixRowModel[i] = firstMatrix.getRow(i);
        }

        // Copy rows from the second matrix starting after the first matrix's rows
        for (int i = 0; i < secondMatrix.getRowDimension(); i++) {
            finalMatrixRowModel[firstMatrix.getRowDimension() + i] = secondMatrix.getRow(i);
        }

        RealMatrix finalMatrix = new Array2DRowRealMatrix(finalMatrixRowModel);
        GenericDOEUtil.matrixLogger(finalMatrix, "matrixUnion");
        return finalMatrix;
    }
}
