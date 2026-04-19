package com.radartracker.mapviewerservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radartracker.mapviewerservice.model.Plot;
import com.radartracker.mapviewerservice.model.ThreatAssessment;
import com.radartracker.mapviewerservice.model.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired private SocketTextHandler socketTextHandler;
    @Autowired private ObjectMapper      objectMapper;

    @KafkaListener(topics = "TrackTopic",  groupId = "Group100", containerFactory = "TrackListener")
    public void onTrack(Track track) {
        broadcast(new WebSocketMessage("TRACK", track));
    }

    @KafkaListener(topics = "PlotTopic",   groupId = "Group101", containerFactory = "PlotListener")
    public void onPlot(Plot plot) {
        broadcast(new WebSocketMessage("PLOT", plot));
    }

    @KafkaListener(topics = "ThreatTopic", groupId = "Group102", containerFactory = "ThreatListener")
    public void onThreat(ThreatAssessment threat) {
        log.debug("Threat received: track={} level={}", threat.getTrackId(), threat.getLevel());
        broadcast(new WebSocketMessage("THREAT", threat));
    }

    private void broadcast(WebSocketMessage message) {
        try {
            socketTextHandler.broadcast(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (Exception e) {
            log.error("Broadcast failed", e);
        }
    }
}
