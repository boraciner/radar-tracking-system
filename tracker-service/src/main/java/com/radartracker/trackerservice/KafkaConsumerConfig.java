package com.radartracker.trackerservice;
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
	public static final String GROUP_ID = "Group100";

	@Bean
	public ConsumerFactory<String, Plot> consumerFactory()
	{
		// Creating a map of string-object type
		Map<String, Object> config = new HashMap<>();
		// Adding the Configuration
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
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
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
}