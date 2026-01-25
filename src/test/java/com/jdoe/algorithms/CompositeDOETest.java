package com.jdoe.algorithms;

import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Test;
import static org.junit.Assert.*;

public class CompositeDOETest {

    @Test
    public void testCentralCompositeDesign_OrthogonalCircumscribed_ValidOutput() {
        // Arrange
        int numberOfFactors = 2;
        int[] centerPoints = {1, 1};
        String alpha = "orthogonal";
        String face = "circumscribed";

        // Act
        RealMatrix result = CompositeDOE.centralCompositeDesign(numberOfFactors, centerPoints, alpha, face);

        // Assert
        assertNotNull(result);
        // For 2 factors: 2^2 factorial + 2*2 star points + 1+1 center points = 4+4+2 = 10 rows, 2 columns
        assertEquals(10, result.getRowDimension());
        assertEquals(2, result.getColumnDimension());
    }

    @Test
    public void testCentralCompositeDesign_RotatableCircumscribed_ValidOutput() {
        // Arrange
        int numberOfFactors = 2;
        int[] centerPoints = {1, 1};
        String alpha = "rotatable";
        String face = "circumscribed";

        // Act
        RealMatrix result = CompositeDOE.centralCompositeDesign(numberOfFactors, centerPoints, alpha, face);

        // Assert
        assertNotNull(result);
        // For 2 factors: 2^2 factorial + 2*2 star points + 1+1 center points = 4+4+2 = 10 rows, 2 columns
        assertEquals(10, result.getRowDimension());
        assertEquals(2, result.getColumnDimension());
    }

    @Test
    public void testCentralCompositeDesign_OrthogonalInscribed_ValidOutput() {
        // Arrange
        int numberOfFactors = 2;
        int[] centerPoints = {1, 1};
        String alpha = "orthogonal";
        String face = "inscribed";

        // Act
        RealMatrix result = CompositeDOE.centralCompositeDesign(numberOfFactors, centerPoints, alpha, face);

        // Assert
        assertNotNull(result);
        // For 2 factors: 2^2 factorial + 2*2 star points + 1+1 center points = 4+4+2 = 10 rows, 2 columns
        assertEquals(10, result.getRowDimension());
        assertEquals(2, result.getColumnDimension());
    }

    @Test
    public void testCentralCompositeDesign_FacedCCF_ValidOutput() {
        // Arrange
        int numberOfFactors = 2;
        int[] centerPoints = {1, 1};
        String alpha = "orthogonal";
        String face = "faced";

        // Act
        RealMatrix result = CompositeDOE.centralCompositeDesign(numberOfFactors, centerPoints, alpha, face);

        // Assert
        assertNotNull(result);
        // For 2 factors: 2^2 factorial + 2*2 star points + 1+1 center points = 4+4+2 = 10 rows, 2 columns
        assertEquals(10, result.getRowDimension());
        assertEquals(2, result.getColumnDimension());
    }

    @Test
    public void testCentralCompositeDesign_ThreeFactors_ValidOutput() {
        // Arrange
        int numberOfFactors = 3;
        int[] centerPoints = {2, 2};
        String alpha = "rotatable";
        String face = "circumscribed";

        // Act
        RealMatrix result = CompositeDOE.centralCompositeDesign(numberOfFactors, centerPoints, alpha, face);

        // Assert
        assertNotNull(result);
        // For 3 factors: 2^3 factorial + 2*3 star points + 2+2 center points = 8+6+4 = 18 rows, 3 columns
        assertEquals(18, result.getRowDimension());
        assertEquals(3, result.getColumnDimension());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCentralCompositeDesign_InvalidAlpha_ThrowsException() {
        // Arrange
        int numberOfFactors = 2;
        int[] centerPoints = {1, 1};
        String alpha = "invalid";
        String face = "circumscribed";

        // Act
        CompositeDOE.centralCompositeDesign(numberOfFactors, centerPoints, alpha, face);

        // Assert: Exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCentralCompositeDesign_InvalidFace_ThrowsException() {
        // Arrange
        int numberOfFactors = 2;
        int[] centerPoints = {1, 1};
        String alpha = "orthogonal";
        String face = "invalid";

        // Act
        CompositeDOE.centralCompositeDesign(numberOfFactors, centerPoints, alpha, face);

        // Assert: Exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCentralCompositeDesign_InvalidNumberOfFactors_ThrowsException() {
        // Arrange
        int numberOfFactors = 0;
        int[] centerPoints = {1, 1};
        String alpha = "orthogonal";
        String face = "circumscribed";

        // Act
        CompositeDOE.centralCompositeDesign(numberOfFactors, centerPoints, alpha, face);

        // Assert: Exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCentralCompositeDesign_InvalidCenterPointsLength_ThrowsException() {
        // Arrange
        int numberOfFactors = 2;
        int[] centerPoints = {1}; // Only 1 element instead of required 2
        String alpha = "orthogonal";
        String face = "circumscribed";

        // Act
        CompositeDOE.centralCompositeDesign(numberOfFactors, centerPoints, alpha, face);

        // Assert: Exception expected
    }

    @Test
    public void testValidateCompositeDesignParameters_ValidInput_NoException() {
        // Arrange
        int numberOfFactors = 2;
        int[] centerPoints = {1, 1};
        String alpha = "orthogonal";
        String face = "circumscribed";

        // Act & Assert
        // Should not throw any exception
        CompositeDOE.validateCompositeDegisnParameters(numberOfFactors, centerPoints, alpha, face);
    }

    @Test
    public void testValidateExpression_ValidExpression_ReturnsTrue() {
        // Arrange
        String expression = "orthogonal";
        String[] legalExpressions = {"orthogonal", "o", "rotatable", "r"};

        // Act
        Boolean result = CompositeDOE.validateExpression(expression, legalExpressions);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testValidateExpression_InvalidExpression_ReturnsFalse() {
        // Arrange
        String expression = "invalid";
        String[] legalExpressions = {"orthogonal", "o", "rotatable", "r"};

        // Act
        Boolean result = CompositeDOE.validateExpression(expression, legalExpressions);

        // Assert
        assertFalse(result);
    }


}
