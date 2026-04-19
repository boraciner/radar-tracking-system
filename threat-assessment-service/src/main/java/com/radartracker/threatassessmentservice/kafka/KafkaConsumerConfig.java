package com.radartracker.threatassessmentservice.kafka;

import com.radartracker.threatassessmentservice.model.Track;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private static final String BOOTSTRAP = "localhost:9092";
    private static final String GROUP_ID  = "threat-group";

    @Bean
    public ConsumerFactory<String, Track> trackConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(),
                new JsonDeserializer<>(Track.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Track> TrackListener() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Track>();
        factory.setConsumerFactory(trackConsumerFactory());
        return factory;
    }
}
