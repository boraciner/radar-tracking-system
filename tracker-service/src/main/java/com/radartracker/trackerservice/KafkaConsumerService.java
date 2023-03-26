package com.radartracker.trackerservice;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.zone.ZoneOffsetTransitionRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;



@Service
public class KafkaConsumerService
{
	@Autowired
	private KafkaProducerService kafkaProducerService;
	
	@KafkaListener(topics = "PlotTopic", groupId = "Group100",containerFactory = "PlotListener")	
	public void listen(Plot plot)
	{
		System.out.println("Received '" + plot +"' from the PlotTopic." );
		
		Track track = new Track(plot.getX(), plot.getY(), 10, 10, 20, plot.getTimeStamp().toEpochSecond(LocalDate.now(), ZoneOffset.UTC));
		kafkaProducerService.sendMessage(track);
		
		
		System.out.println("Sent '" + track +"' to TrackTopic." );
	}
}