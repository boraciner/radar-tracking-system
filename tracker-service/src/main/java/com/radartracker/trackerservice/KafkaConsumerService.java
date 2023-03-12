package com.radartracker.trackerservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class KafkaConsumerService
{
	@KafkaListener(topics = "PlotTopic", groupId = "Group100",containerFactory = "PlotListener")	
	public void listen(Plot Plot)
	{
		System.out.println("Received '" + Plot +"' from the PlotTopic." );
	}
}