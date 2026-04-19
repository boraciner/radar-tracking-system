package com.radartracker.trackerservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private KafkaProducerService producerService;

    @Autowired
    private TrackManager trackManager;

    private final CopyOnWriteArrayList<Plot> buffer = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "PlotTopic", groupId = "Group100", containerFactory = "PlotListener")
    public void onPlot(Plot plot) {
        log.debug("Buffered {}", plot);
        buffer.add(plot);
    }

    // Drain buffer every second, run one full tracking cycle
    @Scheduled(fixedRate = 1000)
    public void runTrackingCycle() {
        if (buffer.isEmpty()) return;

        List<Plot> batch = new ArrayList<>(buffer);
        buffer.removeAll(batch);

        long timeStamp = System.currentTimeMillis();
        List<Track> confirmed = trackManager.processMeasurements(batch, timeStamp);

        log.info("Cycle: {} plots in, {} confirmed tracks out, {} active",
                batch.size(), confirmed.size(), trackManager.activeTrackCount());

        confirmed.forEach(producerService::sendMessage);
    }
}
