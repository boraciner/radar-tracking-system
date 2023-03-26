package com.radartracker.mapviewerservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



@Service
public class KafkaConsumerService
{
	Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
	
	@KafkaListener(topics = "TrackTopic", groupId = "Group100",containerFactory = "TrackListener")	
	public void listen(Track track)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		System.out.println("Received '" + track +"' from the TrackTopic." );
		SocketTextHandler.activeSessions.stream().forEach(t->{
			try {
				objectMapper.writeValueAsString(t);
			} catch (JsonProcessingException e) {
				logger.error(e.toString());
			}
		});
	}
}