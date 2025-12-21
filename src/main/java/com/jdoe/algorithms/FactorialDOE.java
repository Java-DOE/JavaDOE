package com.jdoe.algorithms;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jdoe.util.FactorialUtility;

/*
 * Copyright (c) 2025 Noor Mustafa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class FactorialDOE {

    private static final Logger log = LogManager.getLogger(FactorialDOE.class);

    /**
     * Generates a full factorial design matrix for a set of design factors.
     * <p>
     * A full factorial design enumerates all possible combinations of discrete levels for the provided design factors. Each row in the
     * resulting matrix corresponds to one combination of factor levels, and each column corresponds to a design factor. This method is
     * useful in design of experiments (DOE) where exhaustive sampling of the factor space is required.
     * <p>
     * The input is an integer array where each element represents the number of discrete levels for a corresponding design factor.
     * <p>
     * Example:
     * <pre>
     * int[] factorLevels = {2, 3, 6}; // 3 design factors: 2 levels, 3 levels, 6 levels
     * FactorialDOE.FullFactorial(factorLevels);
     * </pre>
     * <p>
     * Example output (debug log):
     * <pre>
     * Total combinations: 36
     * Matrix:
     * {{0.0,0.0,0.0},
     *  {1.0,0.0,0.0},
     *  {0.0,1.0,0.0},
     *  {1.0,1.0,0.0},
     *  {0.0,2.0,0.0},
     *  {1.0,2.0,0.0},
     *  ...
     *  {1.0,2.0,5.0}}
     * </pre>
     * <p>
     * Notes: - Number of rows = product of all input factor levels. - Number of columns = number of design factors. - Values are
     * zero-indexed, representing the level number for each factor. - This method only generates the combinations; it does not perform
     * optimization.
     *
     * @param designFactorLevelArray an array where each element specifies the number of levels for a design factor
     */
    public static RealMatrix fullFactorial(Integer[] designFactorLevelArray) {
        // calculate matrix size by number of combinations
        int length = designFactorLevelArray.length;
        int combinationCount = 1;
        for (int i = 0; i < length; i++) {
            combinationCount *= designFactorLevelArray[i];
        }
        // create matrix for storing combinations
        Array2DRowRealMatrix matrixFactory = new Array2DRowRealMatrix();
        RealMatrix matrix = matrixFactory.createMatrix(combinationCount, length);

        // populate matrix
        for (int rowNum = 0; rowNum < combinationCount; rowNum++) {
            int temp = rowNum;
            for (int colNum = 0; colNum < designFactorLevelArray.length; colNum++) {
                matrix.setEntry(rowNum, colNum, temp % designFactorLevelArray[colNum]);
                temp /= designFactorLevelArray[colNum];
            }
        }
        return matrix;
    }

    /**
     * Generates a full factorial design matrix for 2-level design factors.
     * <p>
     * This method creates a design matrix where each factor has exactly two levels: -1 and +1. It's commonly used in screening experiments
     * and fractional factorial designs.
     * <p>
     * The resulting matrix will have 2^k rows (where k is the number of factors) and k columns. Each row represents a unique combination of
     * factor levels.
     * <p>
     * Example usage:
     * <pre>
     * {@code
     * RealMatrix design = FactorialDOE.fullFactorial2Level(3);
     * // Generates an 8x3 matrix with all combinations of -1 and +1
     * }
     * </pre>
     * <p>
     * Sample output for 2 factors:
     * <pre>
     * {{-1.0, -1.0},
     *  { 1.0, -1.0},
     *  {-1.0,  1.0},
     *  { 1.0,  1.0}}
     * </pre>
     *
     * @param designFactorCount the number of 2-level design factors
     * @return a RealMatrix containing all possible combinations of factor levels
     * @throws IllegalArgumentException if designFactorCount is negative
     */
    public static RealMatrix fullFactorial2Level(int designFactorCount) {
        // create matrix
        Integer combinationCount = (int) Math.pow(2, designFactorCount);
        Array2DRowRealMatrix matrixFactory = new Array2DRowRealMatrix();
        RealMatrix matrix = matrixFactory.createMatrix(combinationCount, designFactorCount);
        // populate matrix
        // Generate all binary numbers from 0 to (2^k - 1)
        for (int i = 0; i < combinationCount; i++) {
            // For each factor/column
            for (int j = 0; j < designFactorCount; j++) {
                // Check the (k-j-1)-th bit from right
                // If bit is 0 → -1, if bit is 1 → 1
                int bitPosition = designFactorCount - j - 1;
                int bitValue = (i >> bitPosition) & 1;
                matrix.setEntry(i, j, bitValue == 0 ? -1 : 1);
            }
        }
        return matrix;
    }

    /**
     * Generates a fractional factorial design matrix from a generator string.
     *
     * <p>
     * This method creates a design matrix for a fractional factorial experiment based on a user-provided generator string. The generator
     * string defines main factors and combination factors, optionally with a leading '+' or '-' sign for combination factors. Each main
     * factor is treated as a 2-level factor (-1 and +1). Combination factors are computed as the row-wise product of the relevant main
     * factor columns, with signs applied if specified.
     * </p>
     *
     * <p>
     * The resulting matrix includes:
     * <ul>
     *   <li>Main factor columns: each column corresponds to a main factor, populated using a 2-level full factorial design.</li>
     *   <li>Combination factor columns: each column is calculated by multiplying the main factor columns involved in that combination, with an optional leading sign applied.</li>
     * </ul>
     * </p>
     *
     * <p>
     * Example generator strings:
     * <ul>
     *   <li>"a b ab" – two main factors a, b, and one combination factor ab.</li>
     *   <li>"a b -ab" – two main factors a, b, and one combination factor ab with a negative sign applied.</li>
     * </ul>
     * </p>
     *
     * <p>
     * Steps performed by this method:
     * <ol>
     *   <li>Validate the generator string using {@link FactorialUtility#validateGeneretor(String)}.</li>
     *   <li>Parse the generator into main factors and combination factors.</li>
     *   <li>Generate a 2-level full factorial design for the main factors.</li>
     *   <li>Populate the final design matrix with main factor values.</li>
     *   <li>For each combination factor, compute the row-wise product of its associated main factor columns.</li>
     *   <li>If a leading sign ('+' or '-') exists for a combination factor, apply it to the computed product.</li>
     *   <li>Populate the final design matrix with the computed combination factor values.</li>
     *   <li>Log the final matrix for inspection.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Notes:
     * <ul>
     *   <li>The number of rows in the final matrix is 2^k, where k is the number of main factors.</li>
     *   <li>The number of columns equals the total number of factors in the generator string (main + combination).</li>
     *   <li>Combination factors can involve multiple main factors.</li>
     *   <li>Leading '+' signs are optional and do not modify the product; leading '-' signs negate the product.</li>
     * </ul>
     * </p>
     *
     * @param generetor A generator string defining main factors and combination factors. Main factors are single letters, combination factors are
     *                  multiple letters possibly prefixed with '+' or '-' to indicate sign.
     * @throws IllegalArgumentException If the generator string is invalid according to {@link FactorialUtility#validateGeneretor(String)}.
     * @see FactorialUtility#validateGeneretor(String)
     */
    public static RealMatrix fractionalFactorial(String generetor) {
        Array2DRowRealMatrix matrixFactory = new Array2DRowRealMatrix();
        // validation
        FactorialUtility.validateGeneretor(generetor);
        // parsing generetor
        List<String> factorList = Arrays.asList(generetor.trim().toLowerCase().split(" "));
        List<Integer> combinationDesignFactorsIndexList = new ArrayList<>();
        List<Integer> mainDesignFactorIndexList = new ArrayList<>();
        for (String factor : factorList) {
            if (factor.equalsIgnoreCase(" ")) {
                factorList.remove(factor);
            }
            List<String> factorSubList = Arrays.asList(factor.split(""));
            if (factorSubList.size() == 1) {
                mainDesignFactorIndexList.add(factorList.indexOf(factor));
            } else if (factorSubList.size() > 1) {
                combinationDesignFactorsIndexList.add(factorList.indexOf(factor));
            }
        }
        // main design factor 2 level full factorial matrix
        RealMatrix mainDesignFactors2LFFMatrix = fullFactorial2Level(mainDesignFactorIndexList.size());
        // creating the final result matrix
        Integer rowNum = fullFactorial2Level(mainDesignFactorIndexList.size()).getRowDimension();
        Integer colNum = factorList.size();
        RealMatrix finalDesignMatrix = matrixFactory.createMatrix(rowNum, colNum);

        // computing and populating matrix for main design factor factorial first
        for (Integer mainDesignFactorIndexValue : mainDesignFactorIndexList) {
            for (int j = 0; mainDesignFactors2LFFMatrix.getRowDimension() > j; j++) {
                double currentMainDesignFactorMatrixValue = mainDesignFactors2LFFMatrix.getRow(j)[mainDesignFactorIndexValue];
                finalDesignMatrix.setEntry(j, mainDesignFactorIndexValue, currentMainDesignFactorMatrixValue);
            }
        }
        // computing combination design factor and populate the final design matrix with it
        for (Integer combinationDesignFactorIndexValue : combinationDesignFactorsIndexList) {
            String signOfCurrentCombinationDesignFactor = "";
            List<String> factorCombinationSplit = Arrays.asList(factorList.get(combinationDesignFactorIndexValue).split(""));
            if (factorList.get(combinationDesignFactorIndexValue).startsWith("-") || factorList.get(combinationDesignFactorIndexValue).startsWith("+")) {
                signOfCurrentCombinationDesignFactor = factorList.get(combinationDesignFactorIndexValue).split("")[0];
            }
            List<Integer> matchingMainFactorFinalMatrixIndexList = new ArrayList<>();
            for (int c = 0; factorCombinationSplit.size() > c; c++) {
                for (int m = 0; mainDesignFactorIndexList.size() > m; m++) {
                    String currDesignFactorName = factorList.get(m);
                    if (factorCombinationSplit.get(c).equalsIgnoreCase(currDesignFactorName)) {
                        matchingMainFactorFinalMatrixIndexList.add(m);
                    }
                }
            }
            for (int r = 0; finalDesignMatrix.getRowDimension() > r; r++) {
                double mainFactorsProductValue = 1.0;
                for (int c = 0; matchingMainFactorFinalMatrixIndexList.size() > c; c++) {
                    double mainFactorValue = finalDesignMatrix.getEntry(r, c);
                    mainFactorsProductValue *= mainFactorValue;
                }
                if (!signOfCurrentCombinationDesignFactor.isBlank()) { // condition to handle signs in combination design factors
                    if (signOfCurrentCombinationDesignFactor.equalsIgnoreCase("-")) {
                        mainFactorsProductValue = -mainFactorsProductValue;
                    }
                }
                // setting the combination factor value in the final matrix
                finalDesignMatrix.setEntry(r, combinationDesignFactorIndexValue, mainFactorsProductValue);
            }
        }


        return finalDesignMatrix;
    }

    /**
     * Generates a fractional factorial design matrix based on the desired number of factors and resolution.
     * <p>
     * This method automatically generates an optimal generator string for a fractional factorial design
     * based on the specified number of factors (`n`) and resolution (`res`). It ensures that the design meets
     * the required resolution while minimizing the number of experimental runs.
     * <p>
     * The resolution determines the degree to which main effects and interactions are confounded:
     * <ul>
     *   <li><strong>Resolution III</strong>: Main effects are not confounded with each other but may be confounded with two-factor interactions.</li>
     *   <li><strong>Resolution IV</strong>: Main effects are not confounded with each other or with two-factor interactions,
     *       but two-factor interactions may be confounded with each other.</li>
     *   <li><strong>Resolution V</strong>: Main effects and two-factor interactions are not confounded with each other,
     *       but two-factor interactions may be confounded with three-factor interactions.</li>
     * </ul>
     * </p>
     * <p>
     * The algorithm works as follows:
     * <ol>
     *   <li>Determines the minimum number of base factors required to accommodate `n` factors at the given resolution.</li>
     *   <li>Generates base factors labeled as lowercase letters (e.g., a, b, c, ...).</li>
     *   <li>Creates additional factors by combining base factors according to the resolution rule.</li>
     *   <li>Constructs a generator string from all factors (base and combined).</li>
     *   <li>Calls {@link #fractionalFactorial(String)} to generate the design matrix.</li>
     * </ol>
     * </p>
     * <p>
     * Example usage:
     * <pre>
     * {@code
     * RealMatrix design = FactorialDOE.fractionalFactorialByResolution(5, 3);
     * // Generates a 2^(5-2) design (8 runs) with resolution III
     * }
     * </pre>
     * </p>
     *
     * @param n   the total number of factors in the design
     * @param res the desired resolution (must be 3, 4, or 5)
     * @return a RealMatrix containing the fractional factorial design
     * @throws IllegalArgumentException if resolution is not in the range [3, 5], or if the design is not feasible
     * @see #fractionalFactorial(String)
     * @see FactorialUtility#nFacAtRes(int, int)
     * @see FactorialUtility#generateCombinations(List, int)
     */
    // NOTE:
    // Combinations factors = which factors/levels are tested together
    // Resolution = how clearly we can separate their effects after testing
    public static RealMatrix fractionalFactorialByResolution(int n, int res) {
        // Validating resolution
        if (res < 3 || res > 5) {
            throw new IllegalArgumentException("resolution number should be in range of 3 to 5");
        }

        // 1. Find minimum base factors needed
        Integer minFac = null;

        for (int k = res - 1; k < n; k++) {
            if (FactorialUtility.nFacAtRes(k, res) >= n) {
                minFac = k;
                break;
            }
        }

        if (minFac == null) {
            throw new IllegalArgumentException("design not possible");
        }
        // 2. Check if we have enough letters (theoretical, but included for completeness)
        if (minFac > 26) { // Only 26 letters in alphabet
            throw new IllegalArgumentException("design requires too many base-factors.");
        }

        // 3. Get base factors as letters
        List<String> mainFactors = new ArrayList<>();
        for (int i = 0; i < minFac; i++) {
            mainFactors.add(String.valueOf((char) ('a' + i)));
        }

        // 4. Generate combinations for extra factors
        List<String> extraFactors = new ArrayList<>();
        int neededFactors = n - mainFactors.size();

        // Generate combinations from length (res-1) up to length of factors
        for (int r = res - 1; r <= mainFactors.size() && neededFactors > 0; r++) {
            // Generate all combinations of size r from factors
            List<List<String>> combos = FactorialUtility.generateCombinations(mainFactors, r);

            for (List<String> combo : combos) {
                if (neededFactors <= 0) break;

                // Joining combination (e.g., ["a", "b"] -> "ab")
                String comboStr = String.join("", combo);
                extraFactors.add(comboStr);
                neededFactors--;
            }
        }

        // 5. Combine all factors
        List<String> allFactors = new ArrayList<>(mainFactors);
        allFactors.addAll(extraFactors);

        // 6. Create generator string
        String generator = String.join(" ", allFactors);
        return fractionalFactorial(generator);

    }

    /**
     * Finds the optimal generator string for a 2-level fractional-factorial design
     * with the specified number of factors and erased factors.
     *
     * <p>
     * This method performs an exhaustive search (with optional limit) to find the
     * generator string that minimizes aliasing between low-order interactions.
     * </p>
     *
     * @param nFactors    The number of factors in the full factorial design
     * @param nErased     The number of factors to "remove" to create the fractional design
     * @param maxAttempts The maximum number of models to attempt. Positive values limit attempts,
     *                    zero or negative values indicate all combinations should be attempted.
     * @return An array containing: [0] generator string, [1] alias map, [2] alias vector
     * @throws IllegalArgumentException if parameters are invalid or design is too complex
     * @see #fracFactAliasing(RealMatrix)
     */
    public static Object[] fracFactOpt(int nFactors, int nErased, int maxAttempts) {
        if (nFactors > 20) {
            throw new IllegalArgumentException("Design too big, use 20 factors or less");
        }

        if (nFactors < 2) {
            throw new IllegalArgumentException("Design too small");
        }

        if (nErased < 0) {
            throw new IllegalArgumentException("Number of erased factors must be non-negative");
        }

        int nMainFactors = nFactors - nErased;

        // Calculate number of possible aliases
        int nAliases = 0;
        for (int i = 2; i <= nMainFactors; i++) {
            nAliases += FactorialUtility.combinations(nMainFactors, i);
        }

        if (nErased > FactorialUtility.combinations(nAliases, nErased)) {
            throw new IllegalArgumentException("Too many erased factors to create aliasing");
        }

        // Generate main factor names (a, b, c, ...)
        List<String> mainFactorNames = new ArrayList<>();
        for (int i = 0; i < nMainFactors; i++) {
            mainFactorNames.add(String.valueOf((char) ('a' + i)));
        }

        String mainDesign = String.join(" ", mainFactorNames);

        // Generate all possible aliases
        List<List<Integer>> aliases = new ArrayList<>();
        for (int r = 2; r <= nMainFactors; r++) {
            aliases.addAll(FactorialUtility.generateCombinationsIndices(nMainFactors, r));
        }

        // Sort by combination length and then lexicographically
        aliases.sort((a, b) -> {
            if (a.size() != b.size()) {
                return Integer.compare(b.size(), a.size()); // Reverse order
            }
            for (int i = 0; i < a.size(); i++) {
                int cmp = Integer.compare(a.get(i), b.get(i));
                if (cmp != 0) return cmp;
            }
            return 0;
        });

        String bestDesign = null;
        List<String> bestMap = new ArrayList<>();
        double[] bestVector = new double[nFactors];
        Arrays.fill(bestVector, nFactors);

        int designRows = (int) Math.pow(2, nMainFactors);

        // Try combinations of aliases
        List<List<List<Integer>>> allCombinations = FactorialUtility.generateCombinationsFromList(aliases, nErased);

        // Limit attempts if needed
        if (maxAttempts > 0 && allCombinations.size() > maxAttempts) {
            allCombinations = allCombinations.subList(0, maxAttempts);
        }

        for (List<List<Integer>> aliasing : allCombinations) {
            List<String> aliasingNames = new ArrayList<>();
            for (List<Integer> alias : aliasing) {
                StringBuilder sb = new StringBuilder();
                for (Integer idx : alias) {
                    sb.append(mainFactorNames.get(idx));
                }
                aliasingNames.add(sb.toString());
            }

            String aliasingDesign = String.join(" ", aliasingNames);
            String completeDesign = mainDesign + " " + aliasingDesign;

            RealMatrix design = fractionalFactorial(completeDesign);

            if (design.getRowDimension() == designRows && design.getColumnDimension() == nFactors) {
                Object[] aliasResult = fracFactAliasing(design);
                @SuppressWarnings("unchecked")
                List<String> aliasMap = (List<String>) aliasResult[0];
                double[] aliasVector = (double[]) aliasResult[1];

                if (FactorialUtility.compareVectors(aliasVector, bestVector) < 0) {
                    bestDesign = completeDesign;
                    bestMap = new ArrayList<>(aliasMap);
                    bestVector = Arrays.copyOf(aliasVector, aliasVector.length);
                }
            }
        }

        return new Object[]{bestDesign, bestMap, bestVector};
    }

    /**
     * Finds the aliasings in a design, given the contrasts.
     *
     * <p>
     * This method analyzes a fractional factorial design matrix to identify which factors and
     * interactions are aliased with each other. In fractional factorial designs, some effects
     * are confounded (aliased) with others due to the reduced number of experimental runs.
     * </p>
     *
     * <p>
     * The method works by:
     * <ol>
     *   <li>Computing the contrast matrix for all possible factor combinations</li>
     *   <li>Grouping factor combinations that have identical contrasts (these are aliased)</li>
     *   <li>Generating a human-readable alias map showing which factors/interactions are aliased</li>
     *   <li>Creating an alias vector that quantifies the degree of aliasing between different order interactions</li>
     * </ol>
     * </p>
     *
     * <p>
     * Example output for a 2^3-1 design with generator "a b -ab":
     * <pre>
     * Alias map: ["a = bc", "b = ac", "c = ab", "abc = 1"]
     * </pre>
     * This shows that factor 'a' is aliased with the bc interaction, and so on.
     * </p>
     *
     * <p>
     * The alias vector provides quantitative information about the aliasing structure.
     * It can be converted to a matrix using utility methods to analyze how many aliasings
     * exist between i-th and j-th order interactions.
     * </p>
     *
     * @param design A design matrix like those returned by {@link #fractionalFactorial(String)}
     *               or {@link #fractionalFactorialByResolution(int, int)}. The matrix should contain
     *               factor levels coded as -1 and +1.
     * @return An array containing:
     * <ul>
     *   <li>[0] - A List<String> representing the alias map. Each string shows a group of
     *             aliased factors/interactions in the format "a = bc = def"</li>
     *   <li>[1] - A double[] representing the alias vector, which quantifies the aliasing
     *             between different order interactions</li>
     * </ul>
     * @throws IllegalArgumentException if the number of factors exceeds 20 (to prevent excessive computation)
     * @see #fracFactOpt(int, int, int)
     * @see #fractionalFactorial(String)
     * @see #fractionalFactorialByResolution(int, int)
     */
    public static Object[] fracFactAliasing(RealMatrix design) {
        int nRounds = design.getRowDimension();
        int nFactors = design.getColumnDimension();

        if (nFactors > 20) {
            throw new IllegalArgumentException("Design too big, use 20 factors or less");
        }

        // Generate factor names (a, b, c, ...)
        char[] factorNames = new char[nFactors];
        for (int i = 0; i < nFactors; i++) {
            factorNames[i] = (char) ('a' + i);
        }

        // Generate all combinations of factors
        List<List<Integer>> allCombinations = new ArrayList<>();
        for (int r = 1; r <= nFactors; r++) {
            allCombinations.addAll(FactorialUtility.generateCombinationsIndices(nFactors, r));
        }

        // Group combinations by their contrast values
        Map<String, List<List<Integer>>> aliases = new HashMap<>();

        for (List<Integer> combination : allCombinations) {
            // Calculate contrast for this combination
            double[] contrast = new double[nRounds];
            for (int i = 0; i < nRounds; i++) {
                contrast[i] = 1.0;
                for (Integer factorIdx : combination) {
                    contrast[i] *= design.getEntry(i, factorIdx);
                }
            }

            // Create a key from the contrast array
            String key = Arrays.toString(contrast);

            if (!aliases.containsKey(key)) {
                aliases.put(key, new ArrayList<>());
            }
            aliases.get(key).add(combination);
        }

        // Process aliases into readable format
        List<List<List<Integer>>> aliasesList = new ArrayList<>(aliases.values());

        // Sort the aliases list
        aliasesList.sort((list1, list2) -> {
            // Compare by sizes of combinations
            List<Integer> sizes1 = new ArrayList<>();
            List<Integer> sizes2 = new ArrayList<>();

            for (List<Integer> l : list1) sizes1.add(l.size());
            for (List<Integer> l : list2) sizes2.add(l.size());

            Collections.sort(sizes1);
            Collections.sort(sizes2);

            for (int i = 0; i < Math.min(sizes1.size(), sizes2.size()); i++) {
                int cmp = Integer.compare(sizes1.get(i), sizes2.get(i));
                if (cmp != 0) return cmp;
            }

            return Integer.compare(sizes1.size(), sizes2.size());
        });

        List<String> aliasesReadable = new ArrayList<>();
        double[][] aliasMatrix = new double[nFactors][nFactors];

        for (List<List<Integer>> aliasGroup : aliasesList) {
            List<String> groupNames = new ArrayList<>();
            for (List<Integer> combination : aliasGroup) {
                StringBuilder sb = new StringBuilder();
                for (Integer idx : combination) {
                    sb.append(factorNames[idx]);
                }
                groupNames.add(sb.toString());
            }

            String aliasReadable = String.join(" = ", groupNames);
            aliasesReadable.add(aliasReadable);

            // Update alias matrix
            List<Integer> sizes = new ArrayList<>();
            for (List<Integer> combination : aliasGroup) {
                sizes.add(combination.size());
            }

            for (int i = 0; i < sizes.size(); i++) {
                for (int j = i + 1; j < sizes.size(); j++) {
                    int size1 = sizes.get(i);
                    int size2 = sizes.get(j);
                    if (size1 <= size2) {
                        aliasMatrix[size1 - 1][size2 - 1] += 1;
                    } else {
                        aliasMatrix[size2 - 1][size1 - 1] += 1;
                    }
                }
            }
        }

        // Convert matrix to vector using aliasVectorIndices logic
        int vectorSize = nFactors * (nFactors + 1) / 2;
        double[] aliasVector = new double[vectorSize];
        int idx = 0;

        for (int i = 0; i < nFactors; i++) {
            for (int j = i; j < nFactors; j++) {
                aliasVector[idx++] = aliasMatrix[i][j];
            }
        }

        return new Object[]{aliasesReadable, aliasVector};
    }


}
