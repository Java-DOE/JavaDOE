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

    private static final Logger log = LogManager.getLogger( FactorialDOE.class );

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
     * @param designFactorLevelArray
     *         an array where each element specifies the number of levels for a design factor
     */
    public static RealMatrix fullFactorial( int[] designFactorLevelArray ) {
        // calculate matrix size by number of combinations
        int length = designFactorLevelArray.length;
        int combinationCount = 1;
        for ( int i = 0; i < length; i++ ) {
            combinationCount *= designFactorLevelArray[ i ];
        }
        log.debug( "Total Combinations: " + combinationCount );
        // create matrix for storing combinations
        Array2DRowRealMatrix matrixFactory = new Array2DRowRealMatrix();
        RealMatrix matrix = matrixFactory.createMatrix( combinationCount, length );

        // populate matrix
        for ( int rowNum = 0; rowNum < combinationCount; rowNum++ ) {
            int temp = rowNum;
            for ( int colNum = 0; colNum < designFactorLevelArray.length; colNum++ ) {
                matrix.setEntry( rowNum, colNum, temp % designFactorLevelArray[ colNum ] );
                temp /= designFactorLevelArray[ colNum ];
            }
        }
        log.debug( matrix.toString() );
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
     * @param designFactorCount
     *         the number of 2-level design factors
     *
     * @return a RealMatrix containing all possible combinations of factor levels
     *
     * @throws IllegalArgumentException
     *         if designFactorCount is negative
     */
    public static RealMatrix fullFactorial2Level( int designFactorCount ) {
        // create matrix
        Integer combinationCount = ( int ) Math.pow( 2, designFactorCount );
        Array2DRowRealMatrix matrixFactory = new Array2DRowRealMatrix();
        RealMatrix matrix = matrixFactory.createMatrix( combinationCount, designFactorCount );
        log.debug( "Total Combinations: " + combinationCount );
        // populate matrix
        // Generate all binary numbers from 0 to (2^k - 1)
        for ( int i = 0; i < combinationCount; i++ ) {
            // For each factor/column
            for ( int j = 0; j < designFactorCount; j++ ) {
                // Check the (k-j-1)-th bit from right
                // If bit is 0 → -1, if bit is 1 → 1
                int bitPosition = designFactorCount - j - 1;
                int bitValue = ( i >> bitPosition ) & 1;
                matrix.setEntry( i, j, bitValue == 0 ? -1 : 1 );
            }
        }
        log.debug( matrix.toString() );
        return matrix;
    }

    public static void fractionalFactorial( String generetor ) {
        // validate generetor string
        if ( !generetor.matches( "^[A-Za-z +\\-]+$" ) ) {
            throw new IllegalArgumentException( "Generator string contains invalid characters. Allowed: letters, space, +, -" );
        }
        int factorCount = 0;
        for ( int i = 0; i < generetor.length() - 1; i++ ) {
            if ( generetor.contains( " " ) ) {
                factorCount += 1;
            }
        }
        if ( generetor.split( " " ).length != factorCount ) {
            throw new IllegalArgumentException( "Generator does not match the number of factors" );
        }
    }

}
