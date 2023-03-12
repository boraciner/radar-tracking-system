package com.radartracker.plotlistenerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService
{
	@Autowired
	private KafkaTemplate<String, Plot> kafkaTemplate;
	
	private final static String TOPIC_NAME = "PlotTopic";

	public void sendMessage(Plot plot)
	{
		kafkaTemplate.send(TOPIC_NAME,plot);
	}
}