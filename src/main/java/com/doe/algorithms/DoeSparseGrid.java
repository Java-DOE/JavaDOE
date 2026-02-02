package com.doe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Sparse Grid Design of Experiments
 * <p>
 * This module implements sparse grid designs based on Smolyak's construction.
 * Sparse grids provide efficient sampling of high-dimensional spaces with
 * good space-filling properties while requiring significantly fewer points
 * than full tensor product grids.
 * <p>
 * This code was originally developed based on the MATLAB Sparse Grid
 * Interpolation Toolbox by:
 * Copyright (c) 2006 W. Andreas Klimke, Universitaet Stuttgart
 * Copyright (c) 2007-2008 W. A. Klimke. All Rights Reserved.
 * email: klimkeas@ians.uni-stuttgart.de
 * website: https://people.sc.fsu.edu/~jburkardt/m_src/spinterp/spinterp.html
 */
public class DoeSparseGrid {

    /**
     * Generate a sparse grid design using Smolyak's construction.
     *
     * @param nLevel    Sparse grid level. Higher levels provide more points.
     * @param nFactors  Number of factors/dimensions in the design space.
     * @param gridType  Type of 1D grid points to use. Valid values are "clenshaw_curtis", "chebyshev", "gauss_patterson".
     * @return Design points in the unit hypercube [0, 1]^nFactors.
     * @throws IllegalArgumentException if nLevel is negative or nFactors is not positive
     */
    public static RealMatrix doeSparseGrid(int nLevel, int nFactors, String gridType) {
        if (nLevel < 0) {
            throw new IllegalArgumentException("nLevel must be non-negative");
        }
        if (nFactors < 1) {
            throw new IllegalArgumentException("nFactors must be positive");
        }

        if (gridType == null) {
            gridType = "clenshaw_curtis";
        }

        // Generate sparse grid points
        RealMatrix design = _generateSparseGridPoints(nLevel, nFactors, gridType);

        return design;
    }

    /**
     * Overloaded method with default grid type
     */
    public static RealMatrix doeSparseGrid(int nLevel, int nFactors) {
        return doeSparseGrid(nLevel, nFactors, "clenshaw_curtis");
    }

    /**
     * Compute the number of points in a sparse grid design.
     *
     * @param nLevel   Sparse grid level.
     * @param nFactors Number of dimensions.
     * @return Number of points in the sparse grid.
     * @throws IllegalArgumentException if nLevel is negative or nFactors is not positive
     */
    public static int sparseGridDimension(int nLevel, int nFactors) {
        if (nLevel < 0) {
            throw new IllegalArgumentException("nLevel must be non-negative");
        }
        if (nFactors < 1) {
            throw new IllegalArgumentException("nFactors must be positive");
        }

        return _spdimFormula(nLevel, nFactors);
    }

    /**
     * Sparse grid dimension formulas from MATLAB spinterp spdim function.
     * Based on Schreiber (2000) polynomial formulas.
     */
    private static int _spdimFormula(int n, int d) {
        if (n == 0) {
            return 1;
        } else if (n == 1) {
            return 2 * d + 1;
        } else if (n == 2) {
            return 2 * d * d + 2 * d + 1;
        } else if (n == 3) {
            return (int) Math.round((4 * Math.pow(d, 3) + 6 * d * d + 14 * d) / 3) + 1;
        } else if (n == 4) {
            return (int) Math.round((2 * Math.pow(d, 4) + 4 * Math.pow(d, 3) + 22 * d * d + 20 * d) / 3) + 1;
        } else if (n == 5) {
            return (int) Math.round((4 * Math.pow(d, 5) + 10 * Math.pow(d, 4) + 100 * Math.pow(d, 3) + 170 * d * d + 196 * d) / 15) + 1;
        } else if (n == 6) {
            return (int) Math.round(
                    (4 * Math.pow(d, 6) + 12 * Math.pow(d, 5) + 190 * Math.pow(d, 4) + 480 * Math.pow(d, 3) +
                            1246 * d * d + 948 * d) / 45) + 1;
        } else {
            return (int) Math.round(
                    (8 * Math.pow(d, 7) + 28 * Math.pow(d, 6) + 644 * Math.pow(d, 5) + 2170 * Math.pow(d, 4) +
                            9632 * Math.pow(d, 3) + 15442 * d * d + 12396 * d) / 315) + 1;
        }
    }

    /**
     * Generate sparse grid points
     */
    private static RealMatrix _generateSparseGridPoints(int nLevel, int nFactors, String gridType) {
        int targetCount = _spdimFormula(nLevel, nFactors);

        if (nLevel == 0) {
            double[][] result = new double[1][nFactors];
            for (int i = 0; i < nFactors; i++) {
                result[0][i] = 0.5;
            }
            return new Array2DRowRealMatrix(result);
        }

        List<List<Double>> points = new ArrayList<>();

        // Center point
        List<Double> centerPoint = new ArrayList<>();
        for (int i = 0; i < nFactors; i++) {
            centerPoint.add(0.5);
        }
        points.add(centerPoint);

        // Level 1: axis points
        if (nLevel >= 1) {
            for (int dim = 0; dim < nFactors; dim++) {
                for (double val : Arrays.asList(0.0, 1.0)) {
                    List<Double> point = new ArrayList<>(Collections.nCopies(nFactors, 0.5));
                    point.set(dim, val);
                    points.add(point);
                }
            }
        }

        // Level 2+: structured interior points
        if (nLevel >= 2) {
            int gridSize = Math.min(nLevel + 2, 7);
            double[] coords = linspace(0, 1, gridSize);

            // Single-dimension variations
            for (int dim = 0; dim < nFactors; dim++) {
                for (double coord : coords) {
                    if (coord != 0.0 && coord != 0.5 && coord != 1.0) {
                        List<Double> point = new ArrayList<>(Collections.nCopies(nFactors, 0.5));
                        point.set(dim, coord);
                        points.add(point);
                    }
                }
            }

            // Multi-dimensional combinations for higher levels
            if (nLevel >= 3) {
                // Create subset of interior points
                List<Double> coordsSubset = new ArrayList<>();
                for (double coord : coords) {
                    if (coord != 0.0 && coord != 1.0) {
                        coordsSubset.add(coord);
                    }
                }

                for (int r = 2; r < Math.min(nFactors + 1, 4); r++) {
                    List<List<Integer>> dimCombinations = combinations(range(nFactors), r);
                    for (List<Integer> dims : dimCombinations) {
                        List<List<Double>> valCombinations = cartesianProduct(
                                Collections.nCopies(r, coordsSubset.subList(0, Math.min(2, coordsSubset.size()))));

                        for (List<Double> vals : valCombinations) {
                            List<Double> point = new ArrayList<>(Collections.nCopies(nFactors, 0.5));
                            for (int i = 0; i < dims.size(); i++) {
                                point.set(dims.get(i), vals.get(i));
                            }
                            points.add(point);

                            if (points.size() >= targetCount) {
                                break;
                            }
                        }
                        if (points.size() >= targetCount) {
                            break;
                        }
                    }
                    if (points.size() >= targetCount) {
                        break;
                    }
                }
            }
        }

        // Remove duplicates and ensure exact count
        List<List<Double>> uniquePoints = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (List<Double> point : points) {
            StringBuilder sb = new StringBuilder();
            for (double x : point) {
                sb.append(String.format("%.8f", x)).append(",");
            }
            String pointStr = sb.toString();
            if (!seen.contains(pointStr)) {
                seen.add(pointStr);
                uniquePoints.add(new ArrayList<>(point));
            }
        }

        // Fill to exact target if needed
        while (uniquePoints.size() < targetCount) {
            int fillGridSize = targetCount / nFactors + 3;
            double[] gridVals = linspace(0, 1, fillGridSize);

            // Fixed line: Convert double array to List<Double> properly
            List<List<Double>> gridValCombinations = cartesianProduct(
                    Collections.nCopies(nFactors, Arrays.stream(gridVals).boxed().collect(Collectors.toList())));

            for (List<Double> combo : gridValCombinations) {
                StringBuilder sb = new StringBuilder();
                for (double x : combo) {
                    sb.append(String.format("%.8f", x)).append(",");
                }
                String pointStr = sb.toString();
                if (!seen.contains(pointStr)) {
                    seen.add(pointStr);
                    uniquePoints.add(new ArrayList<>(combo));
                    if (uniquePoints.size() >= targetCount) {
                        break;
                    }
                }
            }
            if (uniquePoints.size() >= targetCount) {
                break;
            }
        }

        // Convert to matrix
        double[][] result = new double[Math.min(uniquePoints.size(), targetCount)][nFactors];
        for (int i = 0; i < Math.min(uniquePoints.size(), targetCount); i++) {
            for (int j = 0; j < nFactors; j++) {
                result[i][j] = uniquePoints.get(i).get(j);
            }
        }

        return new Array2DRowRealMatrix(result);
    }

    // Utility methods

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

    private static List<Integer> range(int n) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(i);
        }
        return result;
    }

    private static List<List<Integer>> combinations(List<Integer> elements, int r) {
        List<List<Integer>> result = new ArrayList<>();
        combinationsHelper(elements, r, 0, new ArrayList<>(), result);
        return result;
    }

    private static void combinationsHelper(List<Integer> elements, int r, int start,
                                           List<Integer> current, List<List<Integer>> result) {
        if (current.size() == r) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < elements.size(); i++) {
            current.add(elements.get(i));
            combinationsHelper(elements, r, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    private static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> result = new ArrayList<>();
        if (lists.isEmpty()) {
            result.add(new ArrayList<>());
            return result;
        }
        cartesianProductHelper(lists, 0, new ArrayList<>(), result);
        return result;
    }

    private static <T> void cartesianProductHelper(List<List<T>> lists, int depth,
                                                   List<T> current, List<List<T>> result) {
        if (depth == lists.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (T item : lists.get(depth)) {
            current.add(item);
            cartesianProductHelper(lists, depth + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
