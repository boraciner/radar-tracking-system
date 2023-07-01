package com.radartracker.mapviewerservice.kafka;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.radartracker.mapviewerservice.model.Track;

@JsonComponent
public class TrackConverter {

	public static class Serialize extends JsonSerializer<Track>{

		@Override
		public void serialize(Track value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			
			
		}
		
	}
}
