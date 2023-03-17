package com.radartracker.trackerservice;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService
{
	@Autowired
	private KafkaTemplate<String, Track> kafkaTemplate;
	
	private final static String TOPIC_NAME = "TrackTopic";

	public void sendMessage(Track track)
	{
		kafkaTemplate.send(TOPIC_NAME,track);
	}
}