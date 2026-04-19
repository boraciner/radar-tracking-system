package com.radartracker.radarservice;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlotSender {

    private static final Logger log = LoggerFactory.getLogger(PlotSender.class);

    @Autowired
    private PlotProxy plotProxy;

    // Three targets with distinct trajectories, spread across the space
    // Target 1: downward parabola (U-shape), vertex at x=5
    // Target 2: diagonal line going up-right
    // Target 3: sinusoidal wave mid-screen
    private final List<TargetSimulator> targets = List.of(
        new TargetSimulator(1, 0.0, x -> 0.3 * Math.pow(x - 5.0, 2) + 0.5),
        new TargetSimulator(2, 3.0, x -> 0.7 * x + 1.0),
        new TargetSimulator(3, 6.0, x -> 5.0 + 3.5 * Math.sin(x * 0.9))
    );

    @Scheduled(fixedRate = 1000)
    public void sendPlots() {
        for (TargetSimulator target : targets) {
            Plot plot = target.nextPlot();
            log.debug("Sending {}", plot);
            plotProxy.sendPlot(plot);
        }
    }
}
