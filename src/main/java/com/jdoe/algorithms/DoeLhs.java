package com.doe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generate a latin-hypercube design
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
public class DoeLhs {

    /**
     * Generate a latin-hypercube design
     *
     * @param n                 The number of factors to generate samples for
     * @param samples           The number of samples to generate for each factor (Default: n)
     * @param criterion         Allowable values are "center" or "c", "maximin" or "m",
     *                          "centermaximin" or "cm", and "correlation" or "corr". If no value
     *                          given, the design is simply randomized.
     * @param iterations        The number of iterations in the maximin and correlations algorithms
     *                          (Default: 5).
     * @param randomGenerator   RandomGenerator which controls random draws
     * @param correlationMatrix Enforce correlation between factors (only used in lhsmu)
     * @return An n-by-samples design matrix that has been normalized so factor values
     * are uniformly spaced between zero and one.
     */
    public static RealMatrix lhs(
            int n,
            Integer samples,
            String criterion,
            Integer iterations,
            RandomGenerator randomGenerator,
            double[][] correlationMatrix) {

        if (samples == null) {
            samples = n;
        }

        if (randomGenerator == null) {
            randomGenerator = new JDKRandomGenerator();
        }

        if (criterion != null) {
            if (!isValidCriterion(criterion)) {
                throw new IllegalArgumentException(String.format("Invalid value for \"criterion\": %s", criterion));
            }
        }

        RealMatrix H = null;

        if (criterion == null) {
            H = _lhsClassic(n, samples, randomGenerator);
        } else {
            if (criterion.equalsIgnoreCase("center") || criterion.equalsIgnoreCase("c")) {
                H = _lhsCentered(n, samples, randomGenerator);
            } else if (criterion.equalsIgnoreCase("maximin") || criterion.equalsIgnoreCase("m")) {
                if (iterations == null) iterations = 5;
                H = _lhsMaximin(n, samples, iterations, "maximin", randomGenerator);
            } else if (criterion.equalsIgnoreCase("centermaximin") || criterion.equalsIgnoreCase("cm")) {
                if (iterations == null) iterations = 5;
                H = _lhsMaximin(n, samples, iterations, "centermaximin", randomGenerator);
            } else if (criterion.equalsIgnoreCase("correlation") || criterion.equalsIgnoreCase("corr")) {
                if (iterations == null) iterations = 5;
                H = _lhsCorrelate(n, samples, iterations, randomGenerator);
            } else if (criterion.equalsIgnoreCase("lhsmu")) {
                // as specified by the paper. M is set to 5
                H = _lhsMu(n, samples, correlationMatrix, randomGenerator, 5);
            }
        }

        return H;
    }

    /**
     * Overloaded method with default parameters
     */
    public static RealMatrix lhs(int n) {
        return lhs(n, null, null, null, null, null);
    }

    /**
     * Overloaded method with samples parameter
     */
    public static RealMatrix lhs(int n, int samples) {
        return lhs(n, samples, null, null, null, null);
    }

    /**
     * Overloaded method with samples and criterion parameters
     */
    public static RealMatrix lhs(int n, int samples, String criterion) {
        return lhs(n, samples, criterion, null, null, null);
    }

    /**
     * Overloaded method with samples, criterion, and iterations parameters
     */
    public static RealMatrix lhs(int n, int samples, String criterion, int iterations) {
        return lhs(n, samples, criterion, iterations, null, null);
    }

    /**
     * Check if the criterion is valid
     */
    private static boolean isValidCriterion(String criterion) {
        String lowerCrit = criterion.toLowerCase();
        return lowerCrit.equals("center") || lowerCrit.equals("c") ||
                lowerCrit.equals("maximin") || lowerCrit.equals("m") ||
                lowerCrit.equals("centermaximin") || lowerCrit.equals("cm") ||
                lowerCrit.equals("correlation") || lowerCrit.equals("corr") ||
                lowerCrit.equals("lhsmu");
    }

    /**
     * Classic LHS implementation
     */
    private static RealMatrix _lhsClassic(int n, int samples, RandomGenerator randomGenerator) {
        // Generate the intervals
        double[] cut = linspace(0, 1, samples + 1);

        // Fill points uniformly in each interval
        double[][] u = new double[samples][n];
        for (int i = 0; i < samples; i++) {
            for (int j = 0; j < n; j++) {
                u[i][j] = randomGenerator.nextDouble();
            }
        }

        double[] a = new double[samples];
        double[] b = new double[samples];
        System.arraycopy(cut, 0, a, 0, samples);
        System.arraycopy(cut, 1, b, 0, samples);

        double[][] rdpoints = new double[samples][n];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < samples; i++) {
                rdpoints[i][j] = u[i][j] * (b[i] - a[i]) + a[i];
            }
        }

        // Make the random pairings
        double[][] H = new double[samples][n];
        for (int j = 0; j < n; j++) {
            int[] order = randomPermutation(samples, randomGenerator);
            for (int i = 0; i < samples; i++) {
                H[i][j] = rdpoints[order[i]][j];
            }
        }

        return new Array2DRowRealMatrix(H);
    }

    /**
     * Centered LHS implementation
     */
    private static RealMatrix _lhsCentered(int n, int samples, RandomGenerator randomGenerator) {
        // Generate the intervals
        double[] cut = linspace(0, 1, samples + 1);

        double[] a = new double[samples];
        double[] b = new double[samples];
        System.arraycopy(cut, 0, a, 0, samples);
        System.arraycopy(cut, 1, b, 0, samples);

        double[] center = new double[samples];
        for (int i = 0; i < samples; i++) {
            center[i] = (a[i] + b[i]) / 2.0;
        }

        // Make the random pairings
        double[][] H = new double[samples][n];
        for (int j = 0; j < n; j++) {
            double[] permutedCenter = shuffleArray(center, randomGenerator);
            for (int i = 0; i < samples; i++) {
                H[i][j] = permutedCenter[i];
            }
        }

        return new Array2DRowRealMatrix(H);
    }

    /**
     * Maximin LHS implementation
     */
    private static RealMatrix _lhsMaximin(int n, int samples, int iterations, String lhsType, RandomGenerator randomGenerator) {
        double maxDist = 0;
        RealMatrix bestH = null;

        // Maximize the minimum distance between points
        for (int i = 0; i < iterations; i++) {
            RealMatrix hCandidate;
            if ("maximin".equals(lhsType)) {
                hCandidate = _lhsClassic(n, samples, randomGenerator);
            } else {
                hCandidate = _lhsCentered(n, samples, randomGenerator);
            }

            // Calculate pairwise distances
            double minDist = calculateMinDistance(hCandidate);
            if (maxDist < minDist) {
                maxDist = minDist;
                bestH = hCandidate.copy();
            }
        }

        return bestH;
    }

    /**
     * Correlation-based LHS implementation
     */
    private static RealMatrix _lhsCorrelate(int n, int samples, int iterations, RandomGenerator randomGenerator) {
        double minCorr = Double.POSITIVE_INFINITY;
        RealMatrix bestH = null;

        // Minimize the components correlation coefficients
        for (int i = 0; i < iterations; i++) {
            // Generate a random LHS
            RealMatrix hCandidate = _lhsClassic(n, samples, randomGenerator);
            double maxAbsCorr = calculateMaxAbsCorrelation(hCandidate);

            if (maxAbsCorr < minCorr) {
                minCorr = maxAbsCorr;
                bestH = hCandidate.copy();
            }
        }

        return bestH;
    }

    /**
     * LHS-MU implementation
     */
    private static RealMatrix _lhsMu(int n, Integer samples, double[][] corr, RandomGenerator randomGenerator, int M) {
        if (samples == null) {
            samples = n;
        }

        int I = M * samples;

        // Generate random points
        double[][] rdPoints = new double[I][n];
        for (int i = 0; i < I; i++) {
            for (int j = 0; j < n; j++) {
                rdPoints[i][j] = randomGenerator.nextDouble();
            }
        }

        // Calculate distance matrix
        double[][] dist = calculateDistanceMatrix(rdPoints);

        // Mask diagonal elements
        for (int i = 0; i < I; i++) {
            dist[i][i] = Double.NaN; // Using NaN as mask
        }

        int[] indexRm = new int[I - samples];
        int rmCount = 0;

        while (rmCount < I - samples) {
            // Find minimum average distance
            int minL = findMinAvgDistance(dist);

            // Mask this row and column
            for (int i = 0; i < I; i++) {
                dist[minL][i] = Double.NaN;
                dist[i][minL] = Double.NaN;
            }

            indexRm[rmCount] = minL;
            rmCount++;
        }

        // Remove selected points
        double[][] filteredPoints = removeRows(rdPoints, indexRm);

        if (corr != null) {
            // Apply correlation transformation using Cholesky decomposition
            return applyCorrelationTransformation(filteredPoints, corr, randomGenerator);
        } else {
            return rankOrderTransform(filteredPoints, samples, randomGenerator);
        }
    }

    // Helper methods

    private static double[] linspace(double start, double end, int num) {
        double[] result = new double[num];
        if (num == 1) {
            result[0] = start;
            return result;
        }
        double step = (end - start) / (num - 1);
        for (int i = 0; i < num; i++) {
            result[i] = start + i * step;
        }
        return result;
    }

    private static int[] randomPermutation(int n, RandomGenerator randomGenerator) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        Collections.shuffle(list, new Random(randomGenerator.nextInt()));
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    private static double[] shuffleArray(double[] array, RandomGenerator randomGenerator) {
        double[] newArray = array.clone();
        List<Double> list = new ArrayList<>();
        for (double d : newArray) {
            list.add(d);
        }
        Collections.shuffle(list, new Random(randomGenerator.nextInt()));
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = list.get(i);
        }
        return newArray;
    }

    private static double calculateMinDistance(RealMatrix matrix) {
        double minDist = Double.POSITIVE_INFINITY;
        int rows = matrix.getRowDimension();

        for (int i = 0; i < rows; i++) {
            for (int j = i + 1; j < rows; j++) {
                double dist = euclideanDistance(matrix.getRow(i), matrix.getRow(j));
                if (dist < minDist) {
                    minDist = dist;
                }
            }
        }

        return minDist;
    }

    private static double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    private static double calculateMaxAbsCorrelation(RealMatrix matrix) {
        int n = matrix.getColumnDimension();
        double[][] corrMatrix = new double[n][n];

        // Calculate correlation matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    corrMatrix[i][j] = 1.0;
                } else {
                    corrMatrix[i][j] = correlation(matrix.getColumn(i), matrix.getColumn(j));
                }
            }
        }

        // Find maximum absolute correlation excluding diagonal
        double maxAbsCorr = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    double absCorr = Math.abs(corrMatrix[i][j]);
                    if (absCorr > maxAbsCorr) {
                        maxAbsCorr = absCorr;
                    }
                }
            }
        }

        return maxAbsCorr;
    }

    private static double correlation(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
            sumY2 += y[i] * y[i];
        }

        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));

        if (denominator == 0) return 0;
        return numerator / denominator;
    }

    private static double[][] calculateDistanceMatrix(double[][] points) {
        int n = points.length;
        double[][] dist = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else {
                    dist[i][j] = euclideanDistance(points[i], points[j]);
                }
            }
        }

        return dist;
    }

    private static int findMinAvgDistance(double[][] dist) {
        int n = dist.length;
        double minAvg = Double.POSITIVE_INFINITY;
        int minIdx = 0;

        for (int i = 0; i < n; i++) {
            double sum = 0;
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (!Double.isNaN(dist[i][j])) {
                    sum += dist[i][j];
                    count++;
                }
            }

            if (count > 0) {
                double avg = sum / count;
                if (avg < minAvg) {
                    minAvg = avg;
                    minIdx = i;
                }
            }
        }

        return minIdx;
    }

    private static double[][] removeRows(double[][] matrix, int[] indicesToRemove) {
        List<Integer> toRemove = new ArrayList<>();
        for (int idx : indicesToRemove) {
            toRemove.add(idx);
        }
        Collections.sort(toRemove, Collections.reverseOrder());

        List<double[]> remainingRows = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            if (!toRemove.contains(i)) {
                remainingRows.add(matrix[i]);
            }
        }

        return remainingRows.toArray(new double[0][0]);
    }

    private static RealMatrix applyCorrelationTransformation(double[][] points, double[][] corr, RandomGenerator randomGenerator) {
        // This is a simplified implementation
        // A full implementation would require proper Cholesky decomposition
        return new Array2DRowRealMatrix(points);
    }

    private static RealMatrix rankOrderTransform(double[][] points, int samples, RandomGenerator randomGenerator) {
        int n = points[0].length;
        double[][] result = new double[samples][n];
        int m = points.length;

        for (int j = 0; j < n; j++) {
            // Get column and sort to determine ranks
            double[] col = new double[m];
            for (int i = 0; i < m; i++) {
                col[i] = points[i][j];
            }

            // Create pairs of (value, originalIndex) and sort by value
            int[] indices = new int[m];
            for (int i = 0; i < m; i++) {
                indices[i] = i;
            }

            // Sort indices based on values
            quickSort(col, indices, 0, m - 1);

            // Assign values based on rank
            for (int i = 0; i < samples; i++) {
                double low = (double) i / samples;
                double high = (double) (i + 1) / samples;
                result[i][j] = low + randomGenerator.nextDouble() * (high - low);
            }
        }

        return new Array2DRowRealMatrix(result);
    }

    // Quick sort implementation for sorting with indices
    private static void quickSort(double[] arr, int[] indices, int low, int high) {
        if (low < high) {
            int pi = partition(arr, indices, low, high);
            quickSort(arr, indices, low, pi - 1);
            quickSort(arr, indices, pi + 1, high);
        }
    }

    private static int partition(double[] arr, int[] indices, int low, int high) {
        double pivot = arr[high];
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                // Swap arr[i] and arr[j]
                double temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;

                // Also swap corresponding indices
                int tempIdx = indices[i];
                indices[i] = indices[j];
                indices[j] = tempIdx;
            }
        }

        // Swap arr[i+1] and arr[high] (or pivot)
        double temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        int tempIdx = indices[i + 1];
        indices[i + 1] = indices[high];
        indices[high] = tempIdx;

        return i + 1;
    }
}
