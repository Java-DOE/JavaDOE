package com.jdoe.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jdoe.util.BuildRegressionMatrixUtility;

public class BuildRegressionMatrix {

    /**
     * Builds a regression matrix from an experimental design matrix and a mathematical model string.
     *
     * <p>
     * This method constructs a regression matrix by evaluating mathematical expressions defined in
     * the model string using values from the experimental design matrix. Each token in the model
     * string represents a mathematical expression involving variables (x00, x01, etc.) that correspond
     * to columns in the experimental design matrix.
     * </p>
     *
     * <p>
     * The method supports two modes of operation:
     * <ul>
     *   <li><strong>Matrix Mode</strong>: When the experimental design matrix has multiple columns,
     *       each row of the design matrix is used to evaluate all expressions in the model string.
     *       The resulting regression matrix will have dimensions [n_rows × n_terms], where n_rows
     *       is the number of rows in the design matrix and n_terms is the number of terms in the model.</li>
     *   <li><strong>Vector Mode</strong>: When the experimental design matrix has a single column,
     *       the matrix is treated as a vector of variables, and the regression matrix will have
     *       dimensions [n_terms × 1], where each row represents the evaluation of one expression.</li>
     * </ul>
     * </p>
     *
     * <p>
     * The model string should contain space-separated mathematical expressions. Variables are
     * represented as x followed by zero-padded indices (e.g., x00, x01, x02...). The method
     * supports standard mathematical operations: addition (+), subtraction (-), multiplication (*),
     * division (/), and exponentiation (^), as well as parentheses for grouping.
     * </p>
     *
     * <p>
     * Example usage:
     * <pre>
     * // Matrix mode example
     * double[][] design = {{1.0, 2.0}, {3.0, 4.0}};
     * String model = "x00 x01 x00^2 x01^2 x00*x01";
     * double[][] regressionMatrix = BuildRegressionMatrix.buildRegressionMatrix(design, model, null);
     * // Result: 2x5 matrix with evaluated expressions
     *
     * // Vector mode example
     * double[][] design = {{1.0}, {2.0}, {3.0}};
     * String model = "x00 x00^2 x00^3";
     * double[][] regressionMatrix = BuildRegressionMatrix.buildRegressionMatrix(design, model, null);
     * // Result: 3x1 matrix with evaluated expressions
     * </pre>
     * </p>
     *
     * <p>
     * If the experimental design matrix has a single row but multiple columns, it will be
     * automatically transposed to column format before processing. The buildFlags parameter
     * allows selective evaluation of specific terms in the model string.
     * </p>
     *
     * <p>
     * Supported mathematical operations:
     * <ul>
     *   <li>Basic arithmetic: +, -, *, /</li>
     *   <li>Exponentiation: ^</li>
     *   <li>Parentheses for grouping: ( )</li>
     *   <li>Variable expressions: x00, x01, etc.</li>
     * </ul>
     * </p>
     *
     * @param experimentalDesignMatrix A 2D array containing experimental design data.
     *        In matrix mode, each row represents an experimental run and each column represents
     *        a factor. In vector mode, each row represents a variable value.
     * @param modelString A space-separated string containing mathematical expressions to evaluate.
     *        Each token represents an expression involving variables (x00, x01, etc.) and mathematical
     *        operations.
     * @param buildFlags A boolean array indicating which terms in the model string to evaluate.
     *        If null, all terms will be evaluated. If provided, only terms with true values will
     *        be processed, though the resulting matrix will still have columns for all terms
     *        (with potentially unused columns).
     * @return A 2D array representing the regression matrix where each element is the result
     *         of evaluating the corresponding mathematical expression using values from the
     *         experimental design matrix. The dimensions depend on the mode:
     *         - Matrix mode: [n_rows × n_terms] where n_rows is rows in design matrix
     *         - Vector mode: [n_terms × 1] where n_terms is number of valid terms in model
     * @throws IllegalArgumentException if the mathematical expressions are invalid or contain
     *         syntax errors, or if division by zero occurs during evaluation
     * @throws ArithmeticException if division by zero occurs during evaluation
     * @see BuildRegressionMatrixUtility#grep(String[], String)
     * @see FactorialDOE#fullFactorial(Integer[])
     * @see BoxBehnkenDOE#boxBehnkenDesign(int)
     */
    public static double[][] buildRegressionMatrix(double[][] experimentalDesignMatrix,
            String modelString,
            boolean[] buildFlags) {

        // Spliting the model string into individual tokens
        String[] listOfTokens = modelString.split(" ");

        // Determine the size index based on the matrix dimensions
        int sizeIndex;
        if (experimentalDesignMatrix[0].length == 1) {
            // For vector mode (single column)
            sizeIndex = String.valueOf(experimentalDesignMatrix.length - 1).length();
        } else {
            // For matrix mode
            sizeIndex = String.valueOf(experimentalDesignMatrix[0].length - 1).length();
        }

        // If buildFlags is null, create a default array with all true values
        if (buildFlags == null) {
            buildFlags = new boolean[listOfTokens.length];
            Arrays.fill(buildFlags, true);
        }

        // Test if the matrix has the wrong orientation (single row instead of columns)
        if (experimentalDesignMatrix.length == 1 && experimentalDesignMatrix[0].length > 1) {
            // Transpose the matrix (single row to single column)
            double[][] transposedMatrix = new double[experimentalDesignMatrix[0].length][1];
            for (int i = 0; i < experimentalDesignMatrix[0].length; i++) {
                transposedMatrix[i][0] = experimentalDesignMatrix[0][i];
            }
            experimentalDesignMatrix = transposedMatrix;
        }

        // Filter tokens based on buildFlags
        List<String> filteredTokens = new ArrayList<>();
        for (int i = 0; i < listOfTokens.length; i++) {
            if (buildFlags[i]) {
                filteredTokens.add(listOfTokens[i]);
            }
        }

        // Determine mode and number of variables
        boolean isVectorMode = (experimentalDesignMatrix[0].length == 1);
        int numberOfVariables;

        if (isVectorMode) {
            numberOfVariables = experimentalDesignMatrix.length;
        } else {
            numberOfVariables = experimentalDesignMatrix[0].length;
        }

        // Create variable replacement patterns
        String[][] variableReplacements = new String[numberOfVariables][2];
        for (int i = 0; i < numberOfVariables; i++) {
            String paddedIndex = String.format("%0" + sizeIndex + "d", i);
            variableReplacements[i][0] = "x" + paddedIndex;
            if (isVectorMode) {
                variableReplacements[i][1] = "H[" + i + "]";
            } else {
                variableReplacements[i][1] = "H[row][" + i + "]";
            }
        }

        // Applying variable replacements to all filtered tokens
        String[] processedTokens = new String[filteredTokens.size()];
        for (int tokenIndex = 0; tokenIndex < filteredTokens.size(); tokenIndex++) {
            String token = filteredTokens.get(tokenIndex);
            for (int varIndex = 0; varIndex < numberOfVariables; varIndex++) {
                token = token.replace(variableReplacements[varIndex][0],
                        variableReplacements[varIndex][1]);
            }
            processedTokens[tokenIndex] = token;
        }

        // Building the regression matrix
        double[][] regressionMatrix;

        if (isVectorMode) {
            // Vector mode: single column output
            regressionMatrix = new double[filteredTokens.size()][1];

            for (int tokenIndex = 0; tokenIndex < filteredTokens.size(); tokenIndex++) {
                String expression = processedTokens[tokenIndex];
                // Replace H[i] with actual values
                for (int i = 0; i < numberOfVariables; i++) {
                    String placeholder = "H[" + i + "]";
                    String value = String.valueOf(experimentalDesignMatrix[i][0]);
                    expression = expression.replace(placeholder, value);
                }
                regressionMatrix[tokenIndex][0] = BuildRegressionMatrixUtility.evaluateMathExpression(expression);
            }
        } else {
            // Matrix mode: one row per design point
            int numRows = experimentalDesignMatrix.length;
            int numCols = filteredTokens.size();
            regressionMatrix = new double[numRows][numCols];

            for (int row = 0; row < numRows; row++) {
                for (int tokenIndex = 0; tokenIndex < filteredTokens.size(); tokenIndex++) {
                    String expression = processedTokens[tokenIndex];
                    // Replacing H[row][i] with actual values
                    for (int i = 0; i < numberOfVariables; i++) {
                        String placeholder = "H[row][" + i + "]";
                        String value = String.valueOf(experimentalDesignMatrix[row][i]);
                        expression = expression.replace(placeholder, value);
                    }
                    // Replacing 'row' placeholder
                    expression = expression.replace("row", String.valueOf(row));
                    regressionMatrix[row][tokenIndex] = BuildRegressionMatrixUtility.evaluateMathExpression(expression);
                }
            }
        }

        return regressionMatrix;
    }

}