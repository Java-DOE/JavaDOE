package com.jdoe.util;

import java.util.Arrays;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenericDOEUtil {

    private static final Logger log = LogManager.getLogger( FactorialUtility.class );

    public static void matrixLogger( RealMatrix matrix, String designReference ){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format( "Matrix for Design { %s }:\n" , designReference ) );
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            sb.append( Arrays.toString(matrix.getRow(i))).append("\n");
        }
        log.info(sb.toString());
    }

}
