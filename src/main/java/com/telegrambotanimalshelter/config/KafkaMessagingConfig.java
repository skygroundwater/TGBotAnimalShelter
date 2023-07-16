package com.telegrambotanimalshelter.config;


import com.telegrambotanimalshelter.models.PetOwner;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaMessagingConfig {

    private static final String BOOTSTRAP_SERVER = "127.0.0.1:9092";

    public static final String PET_OWNERS_TOPIC = "pet_owners_topic";

    @Bean
    public ProducerFactory<String, PetOwner> contactsRequestProducerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVER);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, PetOwner> contactsRequestKafkaTemplate() {
        return new KafkaTemplate<>(contactsRequestProducerFactory());
    }


}
