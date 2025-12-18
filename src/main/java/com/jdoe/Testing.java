package com.jdoe;

import com.jdoe.algorithms.FactorialDOE;

/**
 * Hello world!
 *
 */
public class Testing
{
    //! for testing purposes only  does not require a main entry point
    public static void main( String[] args )
    {
        int[] arrayOfLevels = {2,3,6};
//        FactorialDOE.fullFactorial(arrayOfLevels);
//        FactorialDOE.fullFactorial2Level( arrayOfLevels.length );
        FactorialDOE.fractionalFactorial( "a b -ab" );
        FactorialDOE.fractionalFactorial( "a b +ab" );
    }
}
