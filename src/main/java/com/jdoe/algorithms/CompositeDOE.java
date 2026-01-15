package com.jdoe.algorithms;

import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.jetbrains.annotations.NotNull;

import com.jdoe.util.GenericDOEUtil;

public class CompositeDOE {

    private static final String[] alphaParamLegalExpressions = { "orthogonal", "o", "rotatable", "r" };

    private static final String[] faceParamLegalExpressions = { "circumscribed", "ccc", "inscribed", "cci", "faced", "ccf" };

    public static void centralCompositeDesign( @NotNull int numberOfFactors, @NotNull int[] centerPoints, @NotNull String alpha,
            @NotNull String face ) {

        validateCompositeDegisnParameters( numberOfFactors, centerPoints, alpha, face );

        RealMatrix fullFactorial2LevelMatrix = FactorialDOE.fullFactorial2Level( numberOfFactors );

    }

    //<-----------------------------Utility Methods---------------------------------->

    public static void validateCompositeDegisnParameters( int numberOfFactors, int[] centerPoints, String alpha, String face ) {
        if ( numberOfFactors < 1 ) {
            throw new IllegalArgumentException( "number of factors must be greater then 1" );
        }
        if ( centerPoints.length == 2 ) {
            throw new IllegalArgumentException(
                    String.format( "Invalid number of values for \"center\" (expected 2, but got %d )", centerPoints.length ) );
        }
        if ( validateExpression( alpha, alphaParamLegalExpressions ) == Boolean.FALSE ) {
            throw new IllegalArgumentException(
                    String.format( "Invalid value for \"alpha\" (expected format %d )", alphaParamLegalExpressions.toString() ) );
        }
        if ( validateExpression( alpha, faceParamLegalExpressions ) == Boolean.FALSE ) {
            throw new IllegalArgumentException(
                    String.format( "Invalid value for \"face\" (expected format %d )", faceParamLegalExpressions.toString() ) );
        }
    }

    public static Boolean validateExpression( String expression, String[] legalExpressions ) {
        Boolean validInput = Boolean.FALSE;
        for ( String legalExpression : legalExpressions ) {
            if ( legalExpression.equals( expression ) ) {
                validInput = Boolean.TRUE;
                break;
            }
        }
        return validInput;
    }

    public static Boolean matrixUnion( RealMatrix firstMatrix, RealMatrix secondMatrix ) {
        double[][] finalMatrixRowModel = new double[ firstMatrix.getRowDimension() + secondMatrix.getRowDimension() ][];
        for ( int i = 0; i < firstMatrix.getRowDimension(); i++ ) {
            finalMatrixRowModel[ i ] = firstMatrix.getRow( i );
        }
        for ( int i = 0; i < secondMatrix.getRowDimension(); i++ ) {
            finalMatrixRowModel[ i ] = secondMatrix.getRow( i );
        }
        RealMatrix finalMatrix = new Array2DRowRealMatrix(finalMatrixRowModel);
        GenericDOEUtil.matrixLogger( finalMatrix, "matrixUnion" );
    }

}

//<-----------------------------STEPS---------------------------------->

/**
 * Detailed Steps to Port Central Composite Design Script to Java
 *
 * $ 1. Create CompositeDOE Class Structure Create CompositeDOE.java in com.jdoe.algorithms package Add imports:
 * org.apache.commons.math3.linear.* for matrix operations Define method signature: public static RealMatrix centralCompositeDesign(int n,
 * int[] center, String alpha, String face) Set default parameters handling for center, alpha, and face
 *
 * $ 2. Implement Input Validation Logic Add assertion: assert n > 1 : "\"n\" must be an integer greater than 1" Validate alpha parameter
 * with: alpha.toLowerCase() and check against allowed values Validate face parameter with: face.toLowerCase() and check against allowed
 * values Validate center array length: if (center.length != 2) throw IllegalArgumentException Format error messages to match Python
 * implementation
 *
 * $ 3. Implement Star Point Generation Create StarDOE.java class with star method Calculate alpha based on alpha type: For orthogonal:
 * alpha = Math.pow(2 * factorial(n), 0.25) where factorial(n) = n! For rotatable: alpha = Math.pow(2, 0.5) for 2D, Math.pow(3, 0.5) for 3D,
 * etc. Generate 2n star points with values [±alpha, 0, ..., 0], [0, ±alpha, ..., 0], etc. Return both star matrix and alpha value as an
 * Object array
 *
 * 4. Enhance FactorialDOE Class Add ff2n method that generates 2-level factorial design Create 2^n × n matrix with values -1 and +1 Use bit
 * manipulation: for each row i and column j, set value to ((i >> j) & 1) == 0 ? -1 : 1 Return RealMatrix object
 *
 * 5. Create UnionDOE Class Implement union method that combines two matrices vertically Use Array2DRowRealMatrix constructor to create new
 * matrix with combined rows Copy data from both input matrices to the result matrix Return the concatenated matrix
 *
 * 6. Create RepeatCenterDOE Class Implement repeatCenter method that generates center points Create repeats × n matrix filled with zeros
 * Use new Array2DRowRealMatrix(repeats, n) to initialize matrix Return the center point matrix
 *
 * 7. Implement Core Algorithm Logic Initialize H1 andH2 matrices based on face type For inscribed (cci): scale factorial points H1 =
 * H1.scalarMultiply(1.0/a) For faced (ccf): set alpha to 1.0 For circumscribed (ccc): keep original factorial points Generate center points
 * C1 and C2 using repeatCenter Combine matrices: H1 = union(H1, C1), H2 = union(H2, C2), H = union(H1, H2)
 *
 * 8. Handle Matrix Operations Implement matrix scaling using scalarMultiply() method from Commons Math Ensure all matrix dimensions are
 * compatible for union operations Use getSubMatrix() and setSubMatrix() for matrix manipulations Return final RealMatrix object
 *
 * 9. Create Unit Tests Create CompositeDOETest.java with comprehensive test cases Test all three face types: ccc, cci, ccf Test both alpha
 * types: orthogonal, rotatable Validate matrix dimensions: (2^n + 2n + sum(center)) × n Verify center point counts and star point
 * distances
 *
 * 10. Add Documentation and Error Handling Add JavaDoc explaining parameters, return value, and exceptions Implement proper exception
 * handling for invalid inputs Include example usage in documentation Follow the same error message format as Python implementation
 */