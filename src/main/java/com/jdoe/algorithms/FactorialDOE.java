package com.jdoe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
     * A full factorial design enumerates all possible combinations of discrete levels
     * for the provided design factors. Each row in the resulting matrix corresponds
     * to one combination of factor levels, and each column corresponds to a design factor.
     * This method is useful in design of experiments (DOE) where exhaustive sampling
     * of the factor space is required.
     * <p>
     * The input is an integer array where each element represents the number of discrete
     * levels for a corresponding design factor.
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
     * Notes:
     * - Number of rows = product of all input factor levels.
     * - Number of columns = number of design factors.
     * - Values are zero-indexed, representing the level number for each factor.
     * - This method only generates the combinations; it does not perform optimization.
     *
     * @param designFactorLevelArray an array where each element specifies the number of levels for a design factor
     */
    public static RealMatrix FullFactorial(int[] designFactorLevelArray) {
        // calculate matrix size by number of combinations
        int length = designFactorLevelArray.length;
        int combinationCount = 1;
        for (int i = 0; i < length; i++) {
            combinationCount *= designFactorLevelArray[i];
        }
        log.debug("Total Combinations: " + combinationCount);
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
        log.debug(matrix.toString());
        return matrix;
    }

}
