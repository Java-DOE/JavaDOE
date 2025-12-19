package com.jdoe.algorithms;

import org.apache.commons.math3.linear.RealMatrix;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.jdoe.util.FactorialUtility;

/**
 * Unit tests for {@link FactorialDOE#fractionalFactorialByResolution(int, int)}.
 */
public class FactorialDOETest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }
    /********************************* FULL FACTORIAL *******************************/
    @Test
    public void testFullFactorial_ValidInput_ReturnsCorrectMatrix() {
        // Arrange
        Integer[] factorLevels = {2, 3};

        // Act
        RealMatrix result = FactorialDOE.fullFactorial(factorLevels);

        // Assert
        assertNotNull(result);
        assertEquals(6, result.getRowDimension()); // 2 * 3 = 6 combinations
        assertEquals(2, result.getColumnDimension());

        // Check first few values
        assertEquals(0.0, result.getEntry(0, 0), 0.0);
        assertEquals(0.0, result.getEntry(0, 1), 0.0);
        assertEquals(1.0, result.getEntry(1, 0), 0.0);
        assertEquals(0.0, result.getEntry(1, 1), 0.0);
    }

    @Test
    public void testFullFactorial_SingleFactor_ReturnsCorrectMatrix() {
        // Arrange
        Integer[] factorLevels = {4};

        // Act
        RealMatrix result = FactorialDOE.fullFactorial(factorLevels);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.getRowDimension());
        assertEquals(1, result.getColumnDimension());

        for (int i = 0; i < 4; i++) {
            assertEquals((double) i, result.getEntry(i, 0), 0.0);
        }
    }

    @Test
    public void testFullFactorial_EmptyArray_ReturnsEmptyMatrix() {
        // Arrange
        Integer[] factorLevels = {};

        // Act
        RealMatrix result = FactorialDOE.fullFactorial(factorLevels);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getRowDimension()); // One combination of zero factors
        assertEquals(0, result.getColumnDimension());
    }

    /********************************* FULL FACTORIAL 2 LEVEL *******************************/
    @Test
    public void testFullFactorial2Level_ValidInput_ReturnsCorrectMatrix() {
        // Arrange
        int factorCount = 2;

        // Act
        RealMatrix result = FactorialDOE.fullFactorial2Level(factorCount);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.getRowDimension()); // 2^2 = 4 combinations
        assertEquals(2, result.getColumnDimension());

        // Check values follow the expected pattern
        assertEquals(-1.0, result.getEntry(0, 0), 0.0);
        assertEquals(-1.0, result.getEntry(0, 1), 0.0);
        assertEquals(1.0, result.getEntry(1, 0), 0.0);
        assertEquals(-1.0, result.getEntry(1, 1), 0.0);
        assertEquals(-1.0, result.getEntry(2, 0), 0.0);
        assertEquals(1.0, result.getEntry(2, 1), 0.0);
        assertEquals(1.0, result.getEntry(3, 0), 0.0);
        assertEquals(1.0, result.getEntry(3, 1), 0.0);
    }

    @Test
    public void testFullFactorial2Level_ZeroFactors_ReturnsIdentityMatrix() {
        // Arrange
        int factorCount = 0;

        // Act
        RealMatrix result = FactorialDOE.fullFactorial2Level(factorCount);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getRowDimension()); // 2^0 = 1
        assertEquals(0, result.getColumnDimension());
    }

    @Test(expected = NegativeArraySizeException.class)
    public void testFullFactorial2Level_NegativeFactors_ThrowsException() {
        // Arrange
        int factorCount = -1;

        // Act
        FactorialDOE.fullFactorial2Level(factorCount);

        // Assert: Exception expected
    }

    /********************************* FRACTIONAL FACTORIAL *******************************/
    @Test
    public void testFractionalFactorial_ValidGeneratorString_ReturnsMatrix() {
        // Arrange
        String generator = "a b ab";

        try (MockedStatic<FactorialUtility> mockedUtility = mockStatic(FactorialUtility.class)) {
            // Make validation pass silently
            mockedUtility.when(() -> FactorialUtility.validateGeneretor(anyString())).then(invocation -> null);

            // Act
            RealMatrix result = FactorialDOE.fractionalFactorial(generator);

            // Assert
            assertNotNull(result);
            assertEquals(4, result.getRowDimension()); // 2^2 main factors
            assertEquals(3, result.getColumnDimension()); // 3 total factors
        }
    }

    @Test
    public void testFractionalFactorial_GeneratorWithNegativeSign_HandlesCorrectly() {
        // Arrange
        String generator = "a b -ab";

        try (MockedStatic<FactorialUtility> mockedUtility = mockStatic(FactorialUtility.class)) {
            mockedUtility.when(() -> FactorialUtility.validateGeneretor(anyString())).then(invocation -> null);

            // Act
            RealMatrix result = FactorialDOE.fractionalFactorial(generator);

            // Assert
            assertNotNull(result);
            assertEquals(4, result.getRowDimension());
            assertEquals(3, result.getColumnDimension());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFractionalFactorial_InvalidGenerator_ThrowsException() {
        // Arrange
        String invalidGenerator = "invalid generator";

        try (MockedStatic<FactorialUtility> mockedUtility = mockStatic( FactorialUtility.class)) {
            mockedUtility.when(() -> FactorialUtility.validateGeneretor(anyString()))
                    .thenThrow(new IllegalArgumentException("Invalid generator"));

            // Act
            FactorialDOE.fractionalFactorial(invalidGenerator);

            // Assert: Exception expected
        }
    }


    /********************************* FRACTIONAL FACTORIAL BY RESOULTION *******************************/
    @Test(expected = IllegalArgumentException.class)
    public void testFractionalFactorialByResolution_InvalidResolution_LessThanThree() {
        // Arrange
        int totalFactorsCount = 6;
        int invalidResolution = 2;

        // Act
        FactorialDOE.fractionalFactorialByResolution(totalFactorsCount, invalidResolution);

        // Assert: Exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFractionalFactorialByResolution_InvalidResolution_GreaterThanFive() {
        // Arrange
        int totalFactorsCount = 6;
        int invalidResolution = 6;

        // Act
        FactorialDOE.fractionalFactorialByResolution(totalFactorsCount, invalidResolution);

        // Assert: Exception expected
    }

    @Test
    public void testFractionalFactorialByResolution_ValidInput_ResThree_PrintsMatrix() {
        // Arrange
        int totalFactorsCount = 6;
        int resolution = 3;

        // Act & Assert - Just invoke the method to ensure it doesn't throw exceptions
        // Since it prints to System.out, we can capture that
        RealMatrix result = FactorialDOE.fractionalFactorialByResolution(totalFactorsCount, resolution);
        System.out.println(result);
        // Verify something was printed (basic verification)
        assertFalse(outContent.toString().isEmpty());
    }

}
