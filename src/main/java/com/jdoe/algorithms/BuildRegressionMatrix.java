package com.jdoe.algorithms;

import java.util.ArrayList;
import java.util.List;

//Build a regression matrix using a DOE matrix and a list of monomials.
public class BuildRegressionMatrix {

    public static List< Integer > grep ( String pattern, String data ) {
        String[] dataArray = data.split( "" );
        List< Integer > occouranceIndexes = new ArrayList<>();
        for ( int i = 0; dataArray.length > i; i++ ) {
            if ( data.substring( i, i + pattern.length() ).equalsIgnoreCase( pattern ) ) {
                occouranceIndexes.add( i );
            }
        }
        return occouranceIndexes;
    }

}
