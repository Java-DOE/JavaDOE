package com.doe.algorithms;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;

/**
 * Inspired by Taguchi design methodology and orthogonal arrays developed by Genichi Taguchi,
 * this code provides utilities for generating Taguchi arrays, building experimental designs,
 * and computing Signal-to-Noise Ratios, based on orthogonal array libraries.
 * <p>
 * Sources of orthogonal arrays:
 * - University of York, Department of Mathematics:
 *   https://www.york.ac.uk/depts/maths/tables/orthogonal.htm
 * - A Library of Orthogonal Arrays by N. J. A. Sloane:
 *   https://neilsloane.com/oadir/
 * <p>
 * References:
 * - Taguchi G., Chowdhury S., Wu Y. (2005). "Taguchi's Quality Engineering Handbook." Wiley.
 * - Montgomery D. C. (2017). "Design and Analysis of Experiments." Wiley.
 * - [What are Taguchi designs?](https://www.itl.nist.gov/div898/handbook/pri/section5/pri56.htm)
 */
public class DoeTaguchi {

    /**
     * Return a Taguchi orthogonal array by its descriptive name.
     *
     * @param oaName Name of the array, e.g., 'L4(2^3)', 'L8(2^7)', 'L9(3^4)', etc.
     * @return The orthogonal array (zero-indexed factor levels)
     * @throws IllegalArgumentException If the array name is not found
     */
    public static RealMatrix getOrthogonalArray(String oaName) {
        if (!ORTHOGONAL_ARRAYS.containsKey(oaName)) {
            throw new IllegalArgumentException(
                    String.format("Orthogonal array '%s' not found. Available: %s",
                            oaName, ORTHOGONAL_ARRAYS.keySet())
            );
        }
        return ORTHOGONAL_ARRAYS.get(oaName);
    }

    /**
     * List descriptive names of available Taguchi orthogonal arrays.
     *
     * @return List of array names, e.g., ['L4(2^3)', 'L8(2^7)', 'L9(3^4)', ...].
     */
    public static List<String> listOrthogonalArrays() {
        return new ArrayList<>(ORTHOGONAL_ARRAYS.keySet());
    }

    /**
     * Generate a Taguchi design matrix using an orthogonal array and factor levels.
     *
     * @param oaName Name of Taguchi orthogonal array, e.g., 'L4(2^3)', 'L9(3^4)', etc.
     * @param levelsPerFactor Each inner list defines actual levels/settings for each factor.
     *                        Length must match number of columns in the orthogonal array.
     * @return Design matrix with actual factor settings (not coded levels).
     * @throws IllegalArgumentException If number of levels does not match number of factors.
     */
    public static Object[][] taguchiDesign(String oaName, List<List<Object>> levelsPerFactor) {
        RealMatrix array = getOrthogonalArray(oaName);
        int nFactors = array.getColumnDimension();

        if (levelsPerFactor.size() != nFactors) {
            throw new IllegalArgumentException(
                    String.format("Number of factors in array (%d) does not match " +
                                    "number of levels_per_factor provided (%d).",
                            nFactors, levelsPerFactor.size())
            );
        }

        Object[][] designMatrix = new Object[array.getRowDimension()][nFactors];

        for (int i = 0; i < nFactors; i++) {
            List<Object> levels = levelsPerFactor.get(i);
            for (int j = 0; j < array.getRowDimension(); j++) {
                int level = (int) array.getEntry(j, i);
                designMatrix[j][i] = levels.get(level);
            }
        }

        return designMatrix;
    }

    /**
     * Calculate the Signal-to-Noise Ratio (SNR) for Taguchi designs.
     *
     * @param responses Repeated measurements for a single trial (1D array).
     * @param objective Optimization goal, one of: LARGER, SMALLER, NOMINAL.
     * @return SNR value in decibels (dB).
     * @throws IllegalArgumentException If the objective is not recognized.
     */
    public static double computeSnr(double[] responses, TaguchiObjective objective) {
        if (objective == TaguchiObjective.LARGER_IS_BETTER) {
            double sum = 0.0;
            for (double resp : responses) {
                sum += 1.0 / (resp * resp);
            }
            double mean = sum / responses.length;
            return -10 * Math.log10(mean);
        } else if (objective == TaguchiObjective.SMALLER_IS_BETTER) {
            double sum = 0.0;
            for (double resp : responses) {
                sum += resp * resp;
            }
            double mean = sum / responses.length;
            return -10 * Math.log10(mean);
        } else if (objective == TaguchiObjective.NOMINAL_IS_BEST) {
            double meanY = Arrays.stream(responses).average().orElse(0.0);
            double sumSquaredDiffs = Arrays.stream(responses)
                    .map(x -> (x - meanY) * (x - meanY))
                    .sum();
            double stdY = Math.sqrt(sumSquaredDiffs / (responses.length - 1));
            return 10 * Math.log10((meanY * meanY) / (stdY * stdY));
        } else {
            throw new IllegalArgumentException("Invalid objective specified.");
        }
    }

    /**
     * Overloaded method with default objective
     */
    public static double computeSnr(double[] responses) {
        return computeSnr(responses, TaguchiObjective.LARGER_IS_BETTER);
    }

    /**
     * Enumeration for Taguchi optimization objectives when calculating SNR.
     */
    public enum TaguchiObjective {
        LARGER_IS_BETTER("larger is better"),
        SMALLER_IS_BETTER("smaller is better"),
        NOMINAL_IS_BEST("nominal is best");

        private final String description;

        TaguchiObjective(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Mock orthogonal arrays - In a real implementation, these would come from external library
    private static final Map<String, RealMatrix> ORTHOGONAL_ARRAYS = new HashMap<>();

    static {
        // Initialize with some example orthogonal arrays
        ORTHOGONAL_ARRAYS.put("L4(2^3)", new Array2DRowRealMatrix(new double[][]{
                {0, 0, 0},
                {0, 1, 1},
                {1, 0, 1},
                {1, 1, 0}
        }));

        ORTHOGONAL_ARRAYS.put("L8(2^7)", new Array2DRowRealMatrix(new double[][]{
                {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, 1, 1},
                {0, 1, 1, 0, 0, 1, 1},
                {0, 1, 1, 1, 1, 0, 0},
                {1, 0, 1, 0, 1, 0, 1},
                {1, 0, 1, 1, 0, 1, 0},
                {1, 1, 0, 0, 1, 1, 0},
                {1, 1, 0, 1, 0, 0, 1}
        }));

        ORTHOGONAL_ARRAYS.put("L9(3^4)", new Array2DRowRealMatrix(new double[][]{
                {0, 0, 0, 0},
                {0, 1, 2, 1},
                {0, 2, 1, 2},
                {1, 0, 2, 2},
                {1, 1, 1, 0},
                {1, 2, 0, 1},
                {2, 0, 1, 1},
                {2, 1, 0, 2},
                {2, 2, 2, 0}
        }));
    }
}
