package com.jdoe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class BoxBehnkenDOE {

    /**
     * Generates a Box–Behnken design matrix for response surface methodology (RSM).
     *
     * <p>
     * A Box–Behnken design is a three-level experimental design where factor levels take values {@code -1}, {@code 0}, and {@code +1}. In
     * each experimental run, exactly two factors vary at {@code ±1} while all remaining factors are held at the center level {@code 0}.
     * This structure allows efficient estimation of quadratic response surfaces while avoiding extreme corner points of the experimental
     * space.
     * </p>
     *
     * <p>
     * Design construction logic:
     * <ol>
     *   <li>Generate a full 2-level factorial design for two factors ({@code ±1}).</li>
     *   <li>Iterate over all unique pairs of factors {@code (i, j)}.</li>
     *   <li>For each factor pair, embed the 2-level factorial values into columns
     *       {@code i} and {@code j}, while setting all other factor columns to
     *       the center level {@code 0}.</li>
     *   <li>Concatenate all such pairwise designs into a single matrix.</li>
     *   <li>Append center-point runs (all factors set to {@code 0}) to improve
     *       model stability and allow pure-error estimation.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Matrix dimensions:
     * <ul>
     *   <li>Number of columns = {@code totalNumberOfFactors}</li>
     *   <li>Number of non-center rows =
     *       {@code 0.5 × k × (k − 1) × 2²}, where {@code k} is the number of factors</li>
     *   <li>Number of center-point rows =
     *       {@code k} for {@code k < 16}, otherwise taken from a predefined
     *       standard table</li>
     * </ul>
     * </p>
     *
     * <p>
     * Example (4 factors):
     * <pre>
     * {-1, -1,  0,  0}
     * { 1, -1,  0,  0}
     * {-1,  1,  0,  0}
     * { 1,  1,  0,  0}
     * {-1,  0, -1,  0}
     * ...
     * { 0,  0,  0,  0}  // center points
     * </pre>
     * </p>
     *
     * <p>
     * Notes:
     * <ul>
     *   <li>Only second-order (quadratic) models are supported.</li>
     *   <li>No run contains more than two active factors.</li>
     *   <li>Corner points of the full factorial space are intentionally excluded.</li>
     *   <li>Returned values are coded and must be scaled for physical experiments.</li>
     * </ul>
     * </p>
     *
     * @param totalNumberOfFactors
     *         the number of design factors (must be ≥ 3)
     *
     * @return a {@link RealMatrix} representing the Box–Behnken design
     *
     * @throws IllegalArgumentException
     *         if {@code totalNumberOfFactors < 3}
     * @see FactorialDOE#fullFactorial2Level(int)
     */

    public static RealMatrix boxBehnkenDesign( int totalNumberOfFactors ) {

        // validation
        if ( totalNumberOfFactors < 3 ) {
            throw new IllegalArgumentException( "design requires atleast 3 factors" );
        }

        // making a 2 level design base for the bbdesign
        RealMatrix ff2levelMatrix = FactorialDOE.fullFactorial2Level( 2 );

        // result matrix dimension calculation
        int numberOfRows = ( int ) ( 0.5 * totalNumberOfFactors * ( totalNumberOfFactors - 1 ) * ff2levelMatrix.getRowDimension() );

        RealMatrix resultMatrix = new Array2DRowRealMatrix( numberOfRows, totalNumberOfFactors );

        // populate result matrix with base matrix
        int row = 0;
        for ( int i = 0; i < totalNumberOfFactors - 1; i++ ) {
            for ( int j = i + 1; j < totalNumberOfFactors; j++ ) {
                for ( int r = 0; r < ff2levelMatrix.getRowDimension(); r++ ) {
                    // setting all columns to 0 first
                    for ( int c = 0; c < totalNumberOfFactors; c++ ) {
                        resultMatrix.setEntry( row, c, 0.0 );
                    }
                    // assign ±1 to the factor pair
                    resultMatrix.setEntry( row, i, ff2levelMatrix.getEntry( r, 0 ) );
                    resultMatrix.setEntry( row, j, ff2levelMatrix.getEntry( r, 1 ) );
                    row++;
                }
            }
        }

        int center;
        // center points
        if ( totalNumberOfFactors >= 16 ) {
            int[] predefinedPointsArr = new int[]{ 0, 0, 0, 3, 3, 6, 6, 6, 8, 9, 10, 12, 12, 13, 14, 15, 16 };
            center = predefinedPointsArr[ totalNumberOfFactors ];
        } else {
            center = totalNumberOfFactors;
        }
        RealMatrix centerPointRepeatMatrix = new Array2DRowRealMatrix( center, totalNumberOfFactors );

        // creating matrix and appending centerPointRepeatMatrix at the end
        int rows = resultMatrix.getRowDimension() + center;
        RealMatrix finalMatrix = new Array2DRowRealMatrix( rows, totalNumberOfFactors );

        finalMatrix.setSubMatrix( resultMatrix.getData(), 0, 0 );
        finalMatrix.setSubMatrix( centerPointRepeatMatrix.getData(), resultMatrix.getRowDimension(), 0 );
        return finalMatrix;
    }

}
