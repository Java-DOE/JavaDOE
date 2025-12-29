package com.jdoe.algorithms;

import static org.junit.Assert.*;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link BoxBehnkenDOE}.
 */
public class BoxBehnkenDOETest {

    @Test(expected = IllegalArgumentException.class)
    public void testBoxBehnkenDesign_LessThanThreeFactors_ThrowsException() {
        BoxBehnkenDOE.boxBehnkenDesign(2);
    }

    @Test
    public void testBoxBehnkenDesign_FourFactors_DimensionsCorrect() {
        int factors = 4;

        RealMatrix design = BoxBehnkenDOE.boxBehnkenDesign(factors);

        // For Box-Behnken:
        // rows = 0.5 * k * (k - 1) * 2^2 + center
        // = 0.5 * 4 * 3 * 4 + 4 = 24 + 4 = 28
        assertNotNull(design);
        assertEquals(28, design.getRowDimension());
        assertEquals(4, design.getColumnDimension());
    }

    @Test
    public void testBoxBehnkenDesign_FourFactors_OnlyTwoNonZeroPerRow() {
        int factors = 4;
        RealMatrix design = BoxBehnkenDOE.boxBehnkenDesign(factors);

        int nonCenterRows = 24; // last 4 are center points

        for (int r = 0; r < nonCenterRows; r++) {
            int nonZeroCount = 0;
            for (int c = 0; c < factors; c++) {
                double v = design.getEntry(r, c);
                if (v != 0.0) {
                    nonZeroCount++;
                    assertTrue(v == -1.0 || v == 1.0);
                }
            }
            assertEquals(2, nonZeroCount);
        }
    }

    @Test
    public void testBoxBehnkenDesign_FourFactors_CenterPointsAreZero() {
        int factors = 4;
        RealMatrix design = BoxBehnkenDOE.boxBehnkenDesign(factors);

        int rows = design.getRowDimension();
        int center = factors;

        for (int r = rows - center; r < rows; r++) {
            for (int c = 0; c < factors; c++) {
                assertEquals(0.0, design.getEntry(r, c), 0.0);
            }
        }
    }

    @Test
    public void testBoxBehnkenDesign_LevelsRestrictedToMinusOneZeroPlusOne() {
        RealMatrix design = BoxBehnkenDOE.boxBehnkenDesign(5);

        for (int r = 0; r < design.getRowDimension(); r++) {
            for (int c = 0; c < design.getColumnDimension(); c++) {
                double v = design.getEntry(r, c);
                assertTrue(v == -1.0 || v == 0.0 || v == 1.0);
            }
        }
    }

    @Test
    public void testBoxBehnkenDesign_RowCountFormulaHolds() {
        int factors = 6;
        RealMatrix design = BoxBehnkenDOE.boxBehnkenDesign(factors);

        int expectedRows =
                (int) (0.5 * factors * (factors - 1) * 4) + factors;

        assertEquals(expectedRows, design.getRowDimension());
    }
}
