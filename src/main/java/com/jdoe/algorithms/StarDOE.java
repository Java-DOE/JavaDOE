package com.jdoe.algorithms;

import com.jdoe.util.GenericDOEUtil;

public class StarDOE {
    /**
     * Create the star points of various design matrices
     *
     * @param n The number of variables in the design
     * @param alpha Available values are 'faced' (default), 'orthogonal', or 'rotatable'
     * @param center A 1-by-2 array of integers indicating the number of center points
     *               assigned in each block of the response surface design. Default is (1, 1).
     * @return A two-element array containing the star-point portion of the design matrix
     *         and the alpha value to scale the star points with.
     */
    public static Object[] star(int n, String alpha, int[] center) {
        double a;
        // Star points at the center of each face of the factorial
        if ("faced".equals(alpha)) {
            a = 1.0;
        } else if ("orthogonal".equals(alpha)) {
            int nc = (int) Math.pow(2, n);  // factorial points
            int nco = center[0];  // center points to factorial
            int na = 2 * n;  // axial points
            int nao = center[1];  // center points to axial design
            // value of alpha in orthogonal design
            a = Math.sqrt(n * (1 + (double) nao / na) / (1 + (double) nco / nc));
        } else if ("rotatable".equals(alpha)) {
            int nc = (int) Math.pow(2, n);  // number of factorial points
            a = Math.pow(nc, 0.25);  // value of alpha in rotatable design
        } else {
            throw new IllegalArgumentException("Invalid value for \"alpha\": " + alpha);
        }
        // Create the actual matrix now.
        double[][] H = new double[2 * n][n];
        for (int i = 0; i < n; i++) {
            H[2 * i][i] = -1.0;
            H[2 * i + 1][i] = 1.0;
        }
        // Multiply matrix by alpha
        for (int i = 0; i < H.length; i++) {
            for (int j = 0; j < H[i].length; j++) {
                H[i][j] *= a;
            }
        }
        return new Object[]{H, a};
    }

    /**
     * Overloaded method with default center value (1, 1)
     */
    public static Object[] star(int n, String alpha) {
        return star(n, alpha, new int[]{1, 1});
    }

    /**
     * Overloaded method with default alpha value "faced" and default center value (1, 1)
     */
    public static Object[] star(int n) {
        return star(n, "faced", new int[]{1, 1});
    }

}
