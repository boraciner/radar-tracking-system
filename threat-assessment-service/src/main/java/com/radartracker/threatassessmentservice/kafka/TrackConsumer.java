package com.radartracker.threatassessmentservice.kafka;

import com.radartracker.threatassessmentservice.model.Track;
import com.radartracker.threatassessmentservice.model.ThreatAssessment;
import com.radartracker.threatassessmentservice.service.ThreatAssessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TrackConsumer {

    private static final Logger log = LoggerFactory.getLogger(TrackConsumer.class);

    @Autowired private ThreatAssessor threatAssessor;
    @Autowired private ThreatProducerService producer;

    @KafkaListener(topics = "TrackTopic", groupId = "threat-group", containerFactory = "TrackListener")
    public void onTrack(Track track) {
        ThreatAssessment assessment = threatAssessor.assess(track);
        log.info("Track {} → {} score={} dist={} closing={}",
                track.getTrackId(), assessment.getLevel(),
                String.format("%.2f", assessment.getThreatScore()),
                String.format("%.2f", assessment.getDistanceToAsset()),
                String.format("%.2f", assessment.getClosingSpeed()));
        producer.send(assessment);
    }
}
