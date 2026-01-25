package com.jdoe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class RepeatCenterDOE {

    public static RealMatrix repeatCenter(int numberOfFactors, int repeats) {
        return new Array2DRowRealMatrix(repeats, numberOfFactors);
    }

}
