package com.radartracker.mapviewerservice;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class KafkaConsumerConfig
{
	public static final String TRACK_GROUP_ID = "Group100";
	public static final String PLOT_GROUP_ID = "Group101";

	@Bean
	public ConsumerFactory<String, Track> trackConsumerFactory()
	{
		// Creating a map of string-object type
		Map<String, Object> config = new HashMap<>();
		// Adding the Configuration
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, TRACK_GROUP_ID);
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		// Returning message in JSON format
		
		DefaultKafkaConsumerFactory<String, Track> cf = new DefaultKafkaConsumerFactory<>(config,
		        new StringDeserializer(), new JsonDeserializer<>(Track.class, false));
		
		return cf;
	}
	
	// Creating a Listener
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Track> TrackListener()
	{
		ConcurrentKafkaListenerContainerFactory<String, Track> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(trackConsumerFactory());
		return factory;
	}
	
	@Bean
	public ConsumerFactory<String, Plot> plotConsumerFactory()
	{
		// Creating a map of string-object type
		Map<String, Object> config = new HashMap<>();
		// Adding the Configuration
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, PLOT_GROUP_ID);
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		// Returning message in JSON format
		
		DefaultKafkaConsumerFactory<String, Plot> cf = new DefaultKafkaConsumerFactory<>(config,
		        new StringDeserializer(), new JsonDeserializer<>(Plot.class, false));
		
		return cf;
	}
	
	// Creating a Listener
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Plot> PlotListener()
	{
		ConcurrentKafkaListenerContainerFactory<String, Plot> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(plotConsumerFactory());
		return factory;
	}
}