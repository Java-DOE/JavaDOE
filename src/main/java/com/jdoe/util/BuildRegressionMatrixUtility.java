package com.jdoe.util;

import java.util.ArrayList;
import java.util.List;

public class BuildRegressionMatrixUtility {

    /**
     * Grep function to find all occurrences of a pattern in an array of strings
     */
    public static List<Integer> grep( String[] dataArray, String pattern ) {
        List<Integer> occurrenceIndexes = new ArrayList<>();
        for (int i = 0; i < dataArray.length; i++) {
            if (dataArray[i].contains(pattern)) {
                occurrenceIndexes.add(i);
            }
        }
        return occurrenceIndexes;
    }

    /**
     * Evaluate a mathematical expression string
     */
    public static double evaluateMathExpression( String expression ) {
        try {
            // Remove whitespace
            expression = expression.replaceAll("\\s+", "");

            // Handle parentheses first
            while (expression.contains("(")) {
                int openParen = expression.lastIndexOf("(");
                int closeParen = expression.indexOf(")", openParen);

                if (closeParen == -1) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }

                String innerExpr = expression.substring(openParen + 1, closeParen);
                double innerResult = evaluateSimpleExpression(innerExpr);

                expression = expression.substring(0, openParen) + innerResult +
                        expression.substring(closeParen + 1);
            }

            // Evaluate the final expression
            return evaluateSimpleExpression(expression);
        } catch (Exception exception) {
            System.err.println("Error in math evaluation: " + expression);
            throw new IllegalArgumentException("Invalid mathematical expression: " + expression, exception);
        }
    }

    /**
     * Evaluate simple expression without parentheses
     */
    public static double evaluateSimpleExpression( String expression ) {
        // Handle exponentiation first (using ^)
        String[] exponentParts = splitByOperator(expression, '^');
        if (exponentParts.length > 1) {
            double result = evaluateSimpleExpression(exponentParts[0]);
            for (int i = 1; i < exponentParts.length; i++) {
                result = Math.pow(result, evaluateSimpleExpression(exponentParts[i]));
            }
            return result;
        }

        // Handle multiplication and division
        String[] mulDivParts = splitByOperators(expression, new char[]{'*', '/'});
        if (mulDivParts.length > 1) {
            double result = evaluateSimpleExpression(mulDivParts[0]);
            int operatorIndex = mulDivParts[0].length();

            for (int i = 1; i < mulDivParts.length; i++) {
                char operator = expression.charAt(operatorIndex);
                double nextValue = evaluateSimpleExpression(mulDivParts[i]);

                if (operator == '*') {
                    result *= nextValue;
                } else if (operator == '/') {
                    if (nextValue == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    result /= nextValue;
                }

                operatorIndex += mulDivParts[i].length() + 1;
            }
            return result;
        }

        // Handle addition and subtraction
        String[] addSubParts = splitByOperators(expression, new char[]{'+', '-'});
        if (addSubParts.length > 1) {
            double result = evaluateSimpleExpression(addSubParts[0]);
            int operatorIndex = addSubParts[0].length();

            // Check if first part might be negative
            if (expression.charAt(0) == '-') {
                result = -result;
                operatorIndex = 1;
            }

            for (int i = 1; i < addSubParts.length; i++) {
                char operator = expression.charAt(operatorIndex);
                double nextValue = evaluateSimpleExpression(addSubParts[i]);

                if (operator == '+') {
                    result += nextValue;
                } else if (operator == '-') {
                    result -= nextValue;
                }

                operatorIndex += addSubParts[i].length() + 1;
            }
            return result;
        }

        // If no operators, parse as a number
        try {
            return Double.parseDouble(expression);
        } catch (NumberFormatException exception) {
            // Check if it's a numeric constant like "1" or "2"
            if (expression.matches("-?\\d+(\\.\\d+)?")) {
                return Double.parseDouble(expression);
            }
            throw new IllegalArgumentException("Invalid number: " + expression);
        }
    }

    /**
     * Split expression by a single operator
     */
    public static String[] splitByOperator( String expression, char operator ) {
        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        int parenthesisDepth = 0;

        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);

            if (currentChar == '(') {
                parenthesisDepth++;
            } else if (currentChar == ')') {
                parenthesisDepth--;
            }

            if (currentChar == operator && parenthesisDepth == 0) {
                parts.add(currentPart.toString());
                currentPart = new StringBuilder();
            } else {
                currentPart.append(currentChar);
            }
        }

        parts.add(currentPart.toString());
        return parts.toArray(new String[0]);
    }

    /**
     * Split expression by multiple operators
     */
    public static String[] splitByOperators( String expression, char[] operators ) {
        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        int parenthesisDepth = 0;

        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);

            if (currentChar == '(') {
                parenthesisDepth++;
            } else if (currentChar == ')') {
                parenthesisDepth--;
            }

            boolean isOperator = false;
            for (char operator : operators) {
                if (currentChar == operator && parenthesisDepth == 0) {
                    isOperator = true;
                    break;
                }
            }

            if (isOperator) {
                parts.add(currentPart.toString());
                currentPart = new StringBuilder();
                // Don't append the operator
            } else {
                currentPart.append(currentChar);
            }
        }

        parts.add(currentPart.toString());
        return parts.toArray(new String[0]);
    }

}
