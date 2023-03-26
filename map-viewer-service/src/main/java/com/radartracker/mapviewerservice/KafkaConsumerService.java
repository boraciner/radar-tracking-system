package com.radartracker.mapviewerservice;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;



@Service
public class KafkaConsumerService
{
	Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@KafkaListener(topics = "TrackTopic", groupId = "Group100",containerFactory = "TrackListener")	
	public void listen(Track track)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		System.out.println("Received '" + track +"' from the TrackTopic." );
		SocketTextHandler.activeSessions.stream().forEach(t->{
			try {
				t.sendMessage(new TextMessage(GSON.toJson(track)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}