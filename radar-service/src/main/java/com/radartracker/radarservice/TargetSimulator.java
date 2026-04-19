package com.radartracker.radarservice;

import java.time.LocalTime;
import java.util.Random;
import java.util.function.Function;

public class TargetSimulator {

    private final long targetId;
    private final Function<Double, Double> trajectory;
    private final Random noise = new Random();
    private double x;
    private static final double NOISE_SIGMA = 0.15;
    private static final double X_STEP = 0.2;
    private static final double X_MAX = 10.0;

    public TargetSimulator(long targetId, double startX, Function<Double, Double> trajectory) {
        this.targetId = targetId;
        this.x = startX;
        this.trajectory = trajectory;
    }

    public Plot nextPlot() {
        double trueY = trajectory.apply(x);
        double measX = x + noise.nextGaussian() * NOISE_SIGMA;
        double measY = trueY + noise.nextGaussian() * NOISE_SIGMA;

        Plot plot = new Plot(measX, measY, x, trueY, targetId, LocalTime.now().toSecondOfDay());

        x += X_STEP;
        if (x > X_MAX) x = 0.0;

        return plot;
    }

    public long getTargetId() {
        return targetId;
    }
}
