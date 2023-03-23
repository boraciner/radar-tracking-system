package com.radartracker.mapviewerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;



@Service
public class KafkaConsumerService
{
	@KafkaListener(topics = "TrackTopic", groupId = "Group100",containerFactory = "TrackListener")	
	public void listen(Track Track)
	{
		System.out.println("Received '" + Track +"' from the TrackTopic." );
		
		
	}
}