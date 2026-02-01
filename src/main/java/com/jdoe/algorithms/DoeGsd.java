package com.doe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class DoeGsd {

    /**
     * Create a Generalized Subset Design (GSD).
     *
     * @param factorLevels Number of factor levels per factor in design.
     * @param reductionFactor Reduction factor (bigger than 1). Larger reductionFactor means fewer
     *                        experiments in the design and more possible complementary designs.
     * @param numberOfComplementaryDesigns Number of complementary GSD-designs (default 1). The complementary
     *                                    designs are balanced analogous to fold-over in two-level fractional
     *                                    factorial designs.
     * @return n m-by-k matrices where k is the number of factors (equal
     *         to the length of factorLevels. The number of rows, m, will
     *         be approximately equal to the grand product of the factor levels
     *         divided by reductionFactor.
     */
    public static RealMatrix[] gsd(int[] factorLevels, int reductionFactor, int numberOfComplementaryDesigns) {
        // Input validation
        for (int level : factorLevels) {
            if (level <= 0) {
                throw new IllegalArgumentException("factorLevels has to be sequence of positive integers");
            }
        }
        if (reductionFactor <= 1) {
            throw new IllegalArgumentException("reductionFactor has to be integer larger than 1");
        }
        if (numberOfComplementaryDesigns <= 0) {
            throw new IllegalArgumentException("numberOfComplementaryDesigns has to be positive integer");
        }

        List<List<List<Integer>>> partitions = _makePartitions(factorLevels, reductionFactor);
        int[][] latinSquare = _makeLatinSquare(reductionFactor);
        RealMatrix[] orthogonalArrays = _makeOrthogonalArrays(latinSquare, factorLevels.length);

        RealMatrix[] designs;
        try {
            designs = new RealMatrix[orthogonalArrays.length];
            for (int i = 0; i < orthogonalArrays.length; i++) {
                RealMatrix mappedDesign = _mapPartitionsToDesign(partitions, orthogonalArrays[i]);
                // Subtract 1 from each element (equivalent to Python's -1)
                double[][] designData = mappedDesign.getData();
                for (double[] row : designData) {
                    for (int col = 0; col < row.length; col++) {
                        row[col] -= 1;
                    }
                }
                designs[i] = new Array2DRowRealMatrix(designData);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("reductionFactor too large compared to factor levels");
        }

        if (numberOfComplementaryDesigns == 1) {
            return new RealMatrix[]{designs[0]};
        } else {
            RealMatrix[] result = new RealMatrix[numberOfComplementaryDesigns];
            System.arraycopy(designs, 0, result, 0, numberOfComplementaryDesigns);
            return result;
        }
    }

    /**
     * Overloaded method with default number of complementary designs (1)
     */
    public static RealMatrix gsd(int[] factorLevels, int reductionFactor) {
        RealMatrix[] result = gsd(factorLevels, reductionFactor, 1);
        return result[0];
    }

    /**
     * Augment latin-square to the specified number of columns to produce
     * an orthogonal array.
     */
    private static RealMatrix[] _makeOrthogonalArrays(int[][] latinSquare, int numberOfColumns) {
        int[] firstRow = latinSquare[0];
        RealMatrix[] matrixArray = new RealMatrix[firstRow.length];
        for (int i = 0; i < firstRow.length; i++) {
            double[][] singleValueMatrix = {{firstRow[i]}};
            matrixArray[i] = new Array2DRowRealMatrix(singleValueMatrix);
        }

        while (matrixArray[0].getColumnDimension() < numberOfColumns) {
            RealMatrix[] newMatrixArray = new RealMatrix[matrixArray.length];

            for (int i = 0; i < matrixArray.length; i++) {
                RealMatrix currentMatrix = matrixArray[i];
                List<RealMatrix> subMatrices = new ArrayList<>();

                for (int j = 0; j < firstRow.length; j++) {
                    int constantValue = firstRow[j];
                    RealMatrix otherMatrix = matrixArray[latinSquare[i][j]];

                    // Create constant vector
                    int otherRowCount = otherMatrix.getRowDimension();
                    double[][] constantVector = new double[otherRowCount][1];
                    for (int row = 0; row < otherRowCount; row++) {
                        constantVector[row][0] = constantValue;
                    }

                    // Horizontally stack constant vector with other matrix
                    double[][] combinedData = new double[otherRowCount][1 + otherMatrix.getColumnDimension()];
                    for (int row = 0; row < otherRowCount; row++) {
                        combinedData[row][0] = constantValue;
                        System.arraycopy(otherMatrix.getData()[row], 0, combinedData[row], 1, otherMatrix.getColumnDimension());
                    }

                    subMatrices.add(new Array2DRowRealMatrix(combinedData));
                }

                // Vertically stack all sub-matrices
                List<double[]> allRows = new ArrayList<>();
                for (RealMatrix subMatrix : subMatrices) {
                    double[][] subMatrixData = subMatrix.getData();
                    for (double[] row : subMatrixData) {
                        allRows.add(row);
                    }
                }

                double[][] stackedMatrixData = new double[allRows.size()][];
                for (int idx = 0; idx < allRows.size(); idx++) {
                    stackedMatrixData[idx] = allRows.get(idx);
                }

                newMatrixArray[i] = new Array2DRowRealMatrix(stackedMatrixData);
            }

            matrixArray = newMatrixArray;

            if (matrixArray[0].getColumnDimension() == numberOfColumns) {
                break;
            }
        }

        return matrixArray;
    }

    /**
     * Map partitioned factor to final design using orthogonal-array produced
     * by augmenting latin square.
     */
    private static RealMatrix _mapPartitionsToDesign(List<List<List<Integer>>> partitions, RealMatrix orthogonalArray) {
        // Calculate max and min values in the matrix
        double[][] orthogonalArrayData = orthogonalArray.getData();
        int maxPartitionIndex = (int) orthogonalArrayData[0][0]; // Initialize with first element
        int minPartitionIndex = (int) orthogonalArrayData[0][0]; // Initialize with first element

        for (double[] row : orthogonalArrayData) {
            for (double value : row) {
                if (value > maxPartitionIndex) {
                    maxPartitionIndex = (int) value;
                }
                if (value < minPartitionIndex) {
                    minPartitionIndex = (int) value;
                }
            }
        }

        maxPartitionIndex++; // Increment to match the original logic
        if (!(partitions.size() == maxPartitionIndex && minPartitionIndex == 0)) {
            throw new IllegalArgumentException("Orthogonal array indexing does not match partition structure");
        }

        List<List<int[]>> mappingsList = new ArrayList<>();

        for (double[] row : orthogonalArrayData) {
            int[] rowIndexArray = new int[row.length];
            for (int i = 0; i < row.length; i++) {
                rowIndexArray[i] = (int) row[i];
            }

            boolean hasEmptyPartition = false;
            for (int factorIndex = 0; factorIndex < rowIndexArray.length; factorIndex++) {
                int partitionIndex = rowIndexArray[factorIndex];
                if (partitions.get(partitionIndex).get(factorIndex).isEmpty()) {
                    hasEmptyPartition = true;
                    break;
                }
            }

            if (hasEmptyPartition) {
                continue;
            }

            List<List<Integer>> partitionSets = new ArrayList<>();
            for (int factorIndex = 0; factorIndex < rowIndexArray.length; factorIndex++) {
                int partitionIndex = rowIndexArray[factorIndex];
                partitionSets.add(partitions.get(partitionIndex).get(factorIndex));
            }

            List<int[]> cartesianProduct = computeCartesianProduct(partitionSets);
            mappingsList.add(cartesianProduct);
        }

        // Combine all mappings
        List<int[]> allMappings = new ArrayList<>();
        for (List<int[]> mapping : mappingsList) {
            allMappings.addAll(mapping);
        }

        double[][] resultData = new double[allMappings.size()][];
        for (int i = 0; i < allMappings.size(); i++) {
            resultData[i] = new double[allMappings.get(i).length];
            for (int j = 0; j < allMappings.get(i).length; j++) {
                resultData[i][j] = allMappings.get(i)[j];
            }
        }

        return new Array2DRowRealMatrix(resultData);
    }

    /**
     * Compute cartesian product of lists of integers
     */
    private static List<int[]> computeCartesianProduct(List<List<Integer>> listOfLists) {
        List<int[]> result = new ArrayList<>();
        if (listOfLists.isEmpty()) {
            return result;
        }

        computeCartesianProductRecursive(listOfLists, 0, new int[listOfLists.size()], result);
        return result;
    }

    private static void computeCartesianProductRecursive(List<List<Integer>> listOfLists, int depth, int[] current, List<int[]> result) {
        if (depth == listOfLists.size()) {
            result.add(current.clone());
            return;
        }

        for (int value : listOfLists.get(depth)) {
            current[depth] = value;
            computeCartesianProductRecursive(listOfLists, depth + 1, current, result);
        }
    }

    /**
     * Balanced partitioning of factors.
     */
    private static List<List<List<Integer>>> _makePartitions(int[] factorLevels, int numberOfPartitions) {
        List<List<List<Integer>>> partitions = new ArrayList<>();

        for (int partitionIndex = 1; partitionIndex <= numberOfPartitions; partitionIndex++) {
            List<List<Integer>> partition = new ArrayList<>();

            for (int factorLevel : factorLevels) {
                List<Integer> part = new ArrayList<>();

                for (int levelIndex = 0; levelIndex < factorLevel; levelIndex++) {
                    int calculatedIndex = partitionIndex + levelIndex * numberOfPartitions;
                    if (calculatedIndex <= factorLevel) {
                        part.add(calculatedIndex);
                    }
                }

                partition.add(part);
            }

            partitions.add(partition);
        }

        return partitions;
    }

    /**
     * Create a latin square of size n
     */
    private static int[][] _makeLatinSquare(int n) {
        int[][] latinSquare = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                latinSquare[i][j] = (i + j) % n;
            }
        }

        return latinSquare;
    }
}
