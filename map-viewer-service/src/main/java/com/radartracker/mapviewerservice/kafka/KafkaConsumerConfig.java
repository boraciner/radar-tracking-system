package com.radartracker.mapviewerservice.kafka;

import com.radartracker.mapviewerservice.model.Plot;
import com.radartracker.mapviewerservice.model.ThreatAssessment;
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

    private static final String BOOTSTRAP       = "localhost:9092";
    public  static final String TRACK_GROUP_ID  = "Group100";
    public  static final String PLOT_GROUP_ID   = "Group101";
    public  static final String THREAT_GROUP_ID = "Group102";

    @Bean
    public ConsumerFactory<String, Track> trackConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, TRACK_GROUP_ID);
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(), new JsonDeserializer<>(Track.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Track> TrackListener() {
        var f = new ConcurrentKafkaListenerContainerFactory<String, Track>();
        f.setConsumerFactory(trackConsumerFactory());
        return f;
    }

    @Bean
    public ConsumerFactory<String, Plot> plotConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, PLOT_GROUP_ID);
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(), new JsonDeserializer<>(Plot.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Plot> PlotListener() {
        var f = new ConcurrentKafkaListenerContainerFactory<String, Plot>();
        f.setConsumerFactory(plotConsumerFactory());
        return f;
    }

    @Bean
    public ConsumerFactory<String, ThreatAssessment> threatConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, THREAT_GROUP_ID);
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(), new JsonDeserializer<>(ThreatAssessment.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ThreatAssessment> ThreatListener() {
        var f = new ConcurrentKafkaListenerContainerFactory<String, ThreatAssessment>();
        f.setConsumerFactory(threatConsumerFactory());
        return f;
    }
}
