package com.radartracker.mapviewerservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radartracker.mapviewerservice.model.Plot;
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

    @Autowired
    private SocketTextHandler socketTextHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "TrackTopic", groupId = "Group100", containerFactory = "TrackListener")
    public void onTrack(Track track) {
        log.debug("Track received: {}", track);
        broadcast(new WebSocketMessage("TRACK", track));
    }

    @KafkaListener(topics = "PlotTopic", groupId = "Group101", containerFactory = "PlotListener")
    public void onPlot(Plot plot) {
        log.debug("Plot received: {}", plot);
        broadcast(new WebSocketMessage("PLOT", plot));
    }

    private void broadcast(WebSocketMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            TextMessage wsMsg = new TextMessage(json);
            socketTextHandler.broadcast(wsMsg);
        } catch (Exception e) {
            log.error("Failed to broadcast message", e);
        }
    }
}
