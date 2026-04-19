package com.radartracker.trackerservice;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class KalmanFilter {

    private RealVector state;   // [x, y, vx, vy]
    private RealMatrix P;       // error covariance
    private final RealMatrix F; // state transition
    private final RealMatrix H; // measurement
    private final RealMatrix Q; // process noise
    private final RealMatrix R; // measurement noise

    private static final double DT      = 1.0;
    private static final double SIGMA_A = 0.5;   // process noise (acceleration std)
    private static final double SIGMA_R = 0.15;  // measurement noise std (matches radar noise)

    public KalmanFilter(double initX, double initY) {
        // State transition: constant velocity model
        F = new Array2DRowRealMatrix(new double[][] {
            {1, 0, DT, 0},
            {0, 1,  0, DT},
            {0, 0,  1,  0},
            {0, 0,  0,  1}
        });

        // Observe position only
        H = new Array2DRowRealMatrix(new double[][] {
            {1, 0, 0, 0},
            {0, 1, 0, 0}
        });

        // Process noise covariance (discrete white noise acceleration model)
        double dt2 = DT * DT;
        double dt3 = dt2 * DT;
        double dt4 = dt3 * DT;
        Q = new Array2DRowRealMatrix(new double[][] {
            {dt4 / 4, 0,       dt3 / 2, 0      },
            {0,       dt4 / 4, 0,       dt3 / 2},
            {dt3 / 2, 0,       dt2,     0      },
            {0,       dt3 / 2, 0,       dt2    }
        }).scalarMultiply(SIGMA_A * SIGMA_A);

        // Measurement noise covariance
        R = new Array2DRowRealMatrix(new double[][] {
            {SIGMA_R * SIGMA_R, 0                },
            {0,                 SIGMA_R * SIGMA_R}
        });

        // Initial state and covariance
        state = new ArrayRealVector(new double[]{initX, initY, 0.0, 0.0});
        P = MatrixUtils.createRealIdentityMatrix(4).scalarMultiply(10.0);
    }

    public void predict() {
        state = F.operate(state);
        P = F.multiply(P).multiply(F.transpose()).add(Q);
    }

    public void update(double measX, double measY) {
        RealVector z = new ArrayRealVector(new double[]{measX, measY});
        RealVector innovation = z.subtract(H.operate(state));
        RealMatrix S = H.multiply(P).multiply(H.transpose()).add(R);
        RealMatrix K = P.multiply(H.transpose()).multiply(MatrixUtils.inverse(S));
        state = state.add(K.operate(innovation));
        P = MatrixUtils.createRealIdentityMatrix(4).subtract(K.multiply(H)).multiply(P);
    }

    public double getX()  { return state.getEntry(0); }
    public double getY()  { return state.getEntry(1); }
    public double getVx() { return state.getEntry(2); }
    public double getVy() { return state.getEntry(3); }
}
