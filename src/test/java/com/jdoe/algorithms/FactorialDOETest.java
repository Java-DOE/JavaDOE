package com.jdoe.algorithms;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for all methods in {@link FactorialDOE}.
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

    /********************************* FULL FACTORIAL TESTS *******************************/

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

    @Test( expected = IllegalArgumentException.class)
    public void testFullFactorial_EmptyArray_IllegalArgumentException() {
        // Arrange
        Integer[] factorLevels = {};

        // Act
        RealMatrix result = FactorialDOE.fullFactorial(factorLevels);

        // Assert
    }

    /********************************* FULL FACTORIAL 2 LEVEL TESTS *******************************/

    @Test
    public void testFullFactorial2Level_ValidInput_ReturnsCorrectMatrix() {
        // Arrange
        int factorCount = 2;

        // Act
        RealMatrix result = FactorialDOE.fullFactorial2Level(factorCount);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.getRowDimension()); // 2^2 = 4 combinations
        assertEquals(2, result.getColumnDimension()); // 2 factors

        // Check values follow the expected pattern based on bit manipulation
        assertEquals(-1.0, result.getEntry(0, 0), 0.0);
        assertEquals(-1.0, result.getEntry(0, 1), 0.0);
        assertEquals(-1.0, result.getEntry(1, 0), 0.0);
        assertEquals(1.0, result.getEntry(1, 1), 0.0);
        assertEquals(1.0, result.getEntry(2, 0), 0.0);
        assertEquals(-1.0, result.getEntry(2, 1), 0.0);
        assertEquals(1.0, result.getEntry(3, 0), 0.0);
        assertEquals(1.0, result.getEntry(3, 1), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFullFactorial2Level_ZeroFactors_ReturnsIdentityMatrix() {
        // Arrange
        int factorCount = 0;

        // Act
        RealMatrix result = FactorialDOE.fullFactorial2Level(factorCount);

        // Assert
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFullFactorial2Level_NegativeFactors_ThrowsException() {
        // Arrange
        int factorCount = -1;

        // Act
        FactorialDOE.fullFactorial2Level(factorCount);

        // Assert: Exception expected
    }

    /********************************* FRACTIONAL FACTORIAL TESTS *******************************/

    @Test
    public void testFractionalFactorial_ValidGeneratorString_ReturnsMatrix() {
        // Arrange
        String generator = "a b ab";

        // Act
        RealMatrix result = FactorialDOE.fractionalFactorial(generator);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.getRowDimension()); // 2^2 main factors
        assertEquals(3, result.getColumnDimension()); // 3 total factors

        // Verify basic properties of a 2-level design
        for (int i = 0; i < result.getRowDimension(); i++) {
            for (int j = 0; j < result.getColumnDimension(); j++) {
                double value = result.getEntry(i, j);
                assertTrue("Value should be either -1.0 or 1.0",
                        Math.abs(value - 1.0) < 0.001 || Math.abs(value + 1.0) < 0.001);
            }
        }
    }

    @Test
    public void testFractionalFactorial_GeneratorWithNegativeSign_HandlesCorrectly() {
        // Arrange
        String generator = "a b -ab";

        // Act
        RealMatrix result = FactorialDOE.fractionalFactorial(generator);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.getRowDimension());
        assertEquals(3, result.getColumnDimension());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFractionalFactorial_InvalidGenerator_ThrowsException() {
        // Arrange
        String invalidGenerator = "1 2 3"; // Invalid characters

        // Act
        FactorialDOE.fractionalFactorial(invalidGenerator);

        // Assert: Exception expected
    }

    /********************************* FRACTIONAL FACTORIAL BY RESOLUTION TESTS *******************************/

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
    public void testFractionalFactorialByResolution_ValidInput_ResThree_ReturnsMatrix() {
        // Arrange
        int totalFactorsCount = 4;
        int resolution = 3;

        // Act
        RealMatrix result = FactorialDOE.fractionalFactorialByResolution(totalFactorsCount, resolution);

        // Assert
        assertNotNull(result);
        assertTrue(result.getRowDimension() > 0);
        assertEquals(totalFactorsCount, result.getColumnDimension());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFractionalFactorialByResolution_DesignNotPossible_ThrowsException() {
        // Arrange
        int totalFactorsCount = 100; // Too many factors
        int resolution = 3;

        // Act
        FactorialDOE.fractionalFactorialByResolution(totalFactorsCount, resolution);

        // Assert: Exception expected
    }

    /********************************* FRACFACT OPT TESTS *******************************/

    @Test
    public void testFracFactOpt_ValidParameters_ReturnsResults() {
        // Arrange
        int nFactors = 4;
        int nErased = 1;
        int maxAttempts = 0; // Try all combinations

        // Act
        Object[] result = FactorialDOE.fracFactOpt(nFactors, nErased, maxAttempts);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.length);
        assertNotNull(result[0]); // Generator string
        assertNotNull(result[1]); // Alias map
        assertNotNull(result[2]); // Alias vector
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFracFactOpt_TooManyFactors_ThrowsException() {
        // Arrange
        int nFactors = 25; // Exceeds limit of 20
        int nErased = 1;
        int maxAttempts = 0;

        // Act
        FactorialDOE.fracFactOpt(nFactors, nErased, maxAttempts);

        // Assert: Exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFracFactOpt_NegativeErased_ThrowsException() {
        // Arrange
        int nFactors = 4;
        int nErased = -1; // Invalid negative value
        int maxAttempts = 0;

        // Act
        FactorialDOE.fracFactOpt(nFactors, nErased, maxAttempts);

        // Assert: Exception expected
    }

    /********************************* FRACFACT ALIASING TESTS *******************************/

    @Test
    public void testFracFactAliasing_ValidDesign_ReturnsAliasInfo() {
        // Arrange
        RealMatrix design = FactorialDOE.fractionalFactorial("a b ab");

        // Act
        Object[] result = FactorialDOE.fracFactAliasing(design);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.length);
        assertTrue(result[0] instanceof List); // Alias map
        assertTrue(result[1] instanceof double[]); // Alias vector

        @SuppressWarnings("unchecked")
        List<String> aliasMap = (List<String>) result[0];
        double[] aliasVector = (double[]) result[1];

        assertNotNull(aliasMap);
        assertNotNull(aliasVector);
        assertTrue(aliasVector.length > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFracFactAliasing_TooManyFactors_ThrowsException() {
        // Create a mock matrix with too many factors
        RealMatrix tooLargeMatrix = new org.apache.commons.math3.linear.Array2DRowRealMatrix(4, 25);

        // Act
        FactorialDOE.fracFactAliasing(tooLargeMatrix);

        // Assert: Exception expected
    }
}
