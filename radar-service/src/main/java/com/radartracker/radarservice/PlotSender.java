package com.radartracker.radarservice;

import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlotSender {

    private static final Logger log         = LoggerFactory.getLogger(PlotSender.class);
    private static final double SIGMA_NORM  = 0.15;
    private static final double SIGMA_BARR  = 0.75;
    private static final double SPOT_LIMIT  = 4.0;  // x < SPOT_LIMIT is jammed

    private final Random random = new Random();

    @Autowired private PlotProxy  plotProxy;
    @Autowired private EcmState   ecmState;

    private final List<TargetSimulator> targets = List.of(
        new TargetSimulator(1, 0.0, x -> 0.3 * Math.pow(x - 5.0, 2) + 0.5),
        new TargetSimulator(2, 3.0, x -> 0.7 * x + 1.0),
        new TargetSimulator(3, 6.0, x -> 5.0 + 3.5 * Math.sin(x * 0.9))
    );

    @Scheduled(fixedRate = 1000)
    public void sendPlots() {
        EcmMode mode  = ecmState.getMode();
        double  sigma = (mode == EcmMode.BARRAGE) ? SIGMA_BARR : SIGMA_NORM;

        for (TargetSimulator target : targets) {
            Plot plot = target.nextPlot(sigma);

            // SPOT jamming: drop all detections in the western sector
            if (mode == EcmMode.SPOT && plot.getTrueX() < SPOT_LIMIT) {
                log.debug("ECM SPOT: dropped target {} at x={}", target.getTargetId(), plot.getTrueX());
                continue;
            }

            plotProxy.sendPlot(plot);

            // DRFM: inject ghost plots near each real detection
            if (mode == EcmMode.DRFM) {
                int ghosts = 1 + random.nextInt(2);
                for (int i = 0; i < ghosts; i++) {
                    plotProxy.sendPlot(ghostOf(plot));
                }
            }
        }

        // BARRAGE: add false alarms anywhere in the space
        if (mode == EcmMode.BARRAGE) {
            int fa = random.nextInt(3);
            for (int i = 0; i < fa; i++) {
                plotProxy.sendPlot(falseAlarm());
            }
        }
    }

    private Plot ghostOf(Plot real) {
        double ox = (random.nextDouble() - 0.5) * 1.2;
        double oy = (random.nextDouble() - 0.5) * 1.2;
        return new Plot(
            real.getX()     + ox,
            real.getY()     + oy,
            real.getTrueX() + ox,
            real.getTrueY() + oy,
            real.getTargetId() + 10L,
            real.getTimeStamp()
        );
    }

    private Plot falseAlarm() {
        double x = random.nextDouble() * 10.0;
        double y = random.nextDouble() * 10.0;
        return new Plot(x, y, x, y, 0L, System.currentTimeMillis());
    }
}
