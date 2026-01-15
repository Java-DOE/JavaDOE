package com.jdoe.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FactorialUtility {

    /**
     * Utility method for creating validating the generetor passed
     */
    public static void validateGeneretor( String generetor ) {
        // validate generetor string
        if ( !generetor.matches( "^[A-Za-z +\\-]+$" ) ) {
            throw new IllegalArgumentException( "Generator string contains invalid characters. Allowed: letters, space, +, -" );
        }
        int nFactors = generetor.trim().split("\\s+").length;
        if ( generetor.split( " " ).length != nFactors ) {
            throw new IllegalArgumentException( "Generator does not match the number of factors" );
        }
    }

    // Helper method to calculate number of factors at given resolution
    public static int nFacAtRes(int k, int res) {
        // This calculates total factors possible with k base factors at resolution res
        // For k base factors, total possible factors = k + C(k, res-1) + C(k, res) + ...
        int total = k; // Base factors

        // Add combinations starting from length (res-1) up to k
        for (int r = res - 1; r <= k; r++) {
            total += combinations(k, r);
        }

        return total;
    }

    public static int combinations(int n, int k) {
        if (k < 0 || k > n) return 0;
        if (k == 0 || k == n) return 1;

        int result = 1;
        for (int i = 1; i <= k; i++) {
            result = result * (n - k + i) / i;
        }
        return result;
    }


    // Helper to generate all combinations of size k from a list
    public static List<List<String>> generateCombinations(List<String> items, int k) {
        List<List<String>> result = new ArrayList<>();
        if (k == 0) {
            result.add(new ArrayList<>());
            return result;
        }
        if (k > items.size()) {
            return result;
        }

        // Using backtracking to generate combinations
        generateCombinationsHelper(items, k, 0, new ArrayList<>(), result);
        return result;
    }

    private static void generateCombinationsHelper(List<String> items, int k, int start,
                                                   List<String> current, List<List<String>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < items.size(); i++) {
            current.add(items.get(i));
            generateCombinationsHelper(items, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }


    public static List<List<Integer>> generateCombinationsIndices(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (k == 0) {
            result.add(new ArrayList<>());
            return result;
        }
        if (k > n) {
            return result;
        }

        generateCombinationsIndicesHelper(n, k, 0, new ArrayList<>(), result);
        return result;
    }

    public static void generateCombinationsIndicesHelper(int n, int k, int start,
                                                          List<Integer> current,
                                                          List<List<Integer>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < n; i++) {
            current.add(i);
            generateCombinationsIndicesHelper(n, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static List<List<List<Integer>>> generateCombinationsFromList(List<List<Integer>> items, int k) {
        List<List<List<Integer>>> result = new ArrayList<>();
        if (k == 0) {
            result.add(new ArrayList<>());
            return result;
        }
        if (k > items.size()) {
            return result;
        }

        generateCombinationsFromListHelper(items, k, 0, new ArrayList<>(), result);
        return result;
    }

    public static void generateCombinationsFromListHelper(List<List<Integer>> items, int k, int start,
                                                           List<List<Integer>> current,
                                                           List<List<List<Integer>>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < items.size(); i++) {
            current.add(items.get(i));
            generateCombinationsFromListHelper(items, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static int compareVectors(double[] a, double[] b) {
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            int cmp = Double.compare(a[i], b[i]);
            if (cmp != 0) return cmp;
        }
        return Integer.compare(a.length, b.length);
    }


}
