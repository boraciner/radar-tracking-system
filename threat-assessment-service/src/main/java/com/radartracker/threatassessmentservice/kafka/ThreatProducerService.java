package com.radartracker.threatassessmentservice.kafka;

import com.radartracker.threatassessmentservice.model.ThreatAssessment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ThreatProducerService {

    private static final String TOPIC = "ThreatTopic";

    @Autowired
    private KafkaTemplate<String, ThreatAssessment> kafkaTemplate;

    public void send(ThreatAssessment assessment) {
        kafkaTemplate.send(TOPIC, assessment);
    }
}
