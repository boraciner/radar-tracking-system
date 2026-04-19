package com.radartracker.mapviewerservice.kafka;

import com.radartracker.mapviewerservice.model.Plot;
import com.radartracker.mapviewerservice.model.Track;
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

    public static final String TRACK_GROUP_ID = "Group100";
    public static final String PLOT_GROUP_ID  = "Group101";
    private static final String BOOTSTRAP     = "localhost:9092";

    @Bean
    public ConsumerFactory<String, Track> trackConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, TRACK_GROUP_ID);
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(),
                new JsonDeserializer<>(Track.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Track> TrackListener() {
        ConcurrentKafkaListenerContainerFactory<String, Track> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(trackConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Plot> plotConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, PLOT_GROUP_ID);
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(),
                new JsonDeserializer<>(Plot.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Plot> PlotListener() {
        ConcurrentKafkaListenerContainerFactory<String, Plot> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(plotConsumerFactory());
        return factory;
    }
}
