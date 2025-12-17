package com.jdoe.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        Array2DRowRealMatrix matrixFactory = new Array2DRowRealMatrix();
        // validation
        FactorialUtility.validateGeneretor( generetor );
        // parsing generetor
        List< String > factorList = Arrays.asList( generetor.trim().toLowerCase().split( " " ) );
        List< Integer > combinationDesignFactorsIndexList = new ArrayList<>();
        List< Integer > mainDesignFactorIndexList = new ArrayList<>();
        for ( String factor : factorList ) {
            if ( factor.equalsIgnoreCase( " " ) ) {
                factorList.remove( factor );
            }
            List< String > factorSubList = Arrays.asList( factor.split( "" ) );
            if ( factorSubList.size() == 1 ) {
                mainDesignFactorIndexList.add( factorList.indexOf( factor ) );
            } else if ( factorSubList.size() > 1 ) {
                combinationDesignFactorsIndexList.add( factorList.indexOf( factor ) );
            }
        }
        // main design factor 2 level full factorial matrix
        int[] fullFactorialParam = new int[ mainDesignFactorIndexList.size() ];
        List< RealMatrix > mainDesignFactor2LFFMatrixList = new ArrayList<>();
        for ( int i = 0; mainDesignFactorIndexList.size() > i; i++ ) {
            mainDesignFactor2LFFMatrixList.add( fullFactorial2Level( 1 ) );
            fullFactorialParam[ i ] = 2; // fixed level size
        }
        // creating the final result matrix
        Integer rowNum = fullFactorial( fullFactorialParam ).getRowDimension();
        Integer colNum = factorList.size();
        RealMatrix finalDesignMatrix = matrixFactory.createMatrix( rowNum, colNum );

        // computing and populating matrix for main design factor factorial first
        for ( Integer mainDesignFactorIndexValue :mainDesignFactorIndexList) {
            for ( int i = 0; mainDesignFactor2LFFMatrixList.size() > i; i++ ) {
                RealMatrix currentMainDesignFactorMatrix = mainDesignFactor2LFFMatrixList.get( i );
                for ( int j = 0; currentMainDesignFactorMatrix.getRowDimension() >= j; j++ ) {
                    double currentMainDesignFactorMatrixValue = currentMainDesignFactorMatrix.getRow( j )[ 0 ];
                    finalDesignMatrix.setEntry( j, mainDesignFactorIndexValue, currentMainDesignFactorMatrixValue );
                }
            }
        }
        // computing combination design factor and populate the final design matrix with it
        //? worry about the "-" and "+" cases in the genereter later first populate with product
        for ( Integer combinationDesignFactorIndexValue : combinationDesignFactorsIndexList ) {
            String[] factorCombinationSplit = factorList.get( combinationDesignFactorIndexValue ).split( "" );
            /////////////// matching mainDesingnFactor ///////////////////////////////
            List< Integer > matchingMainFactorFinalMatrixIndexList = new ArrayList<>();
            for ( int c = 0; factorCombinationSplit.length > c; c++ ) {
                for ( int m = 0; mainDesignFactorIndexList.size() > m; m++ ) {
                    String currDesignFactorName = factorList.get( m );
                    if ( factorCombinationSplit[ c ].equalsIgnoreCase( currDesignFactorName ) ) {
                        matchingMainFactorFinalMatrixIndexList.add( m );
                    }
                }
            }
            //////////////////////////////////////////////////////////////
            double mainFactorsProductValue = 1.0;
            for ( int c = 0; matchingMainFactorFinalMatrixIndexList.size() > c; c++ ) {
                for ( int r = 0; finalDesignMatrix.getRowDimension() > r; r++ ) {
                    double mainFactorValue = finalDesignMatrix.getEntry( r, c );
                    mainFactorsProductValue *= mainFactorValue;
                }
                for ( int r = 0; finalDesignMatrix.getRowDimension() > r; r++ ) {
                    // setting the combination factor value in the final matrix
                    finalDesignMatrix.setEntry( r, combinationDesignFactorIndexValue, mainFactorsProductValue );
                }
            }
        }

        log.info( finalDesignMatrix );
    }

}
