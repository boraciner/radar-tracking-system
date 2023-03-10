package com.radartracker.trackerservice;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping ("/api/kafka")
@RestController
public class KafkaController {

	Logger logger = LoggerFactory.getLogger(KafkaController.class);


    @KafkaListener(topics = "transaction-1")
    public void listener(@Payload Plot plt,  ConsumerRecord<String, Plot> cr) {
        logger.info("Topic [transaction-1] Received message "+plt);
    }


}