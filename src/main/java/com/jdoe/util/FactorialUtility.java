package com.jdoe.util;

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

}
