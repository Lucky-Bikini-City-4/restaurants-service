package com.dayaeyak.restaurants.kafka.producer;

import com.dayaeyak.restaurants.kafka.producer.dtos.ServiceRegisterRequestDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

//    private final String SERVER = "localhost:9092";

    @Value("${KAFKA_BOOTSTRAP_SERVERS}")
    private String SERVER;

    @Bean
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        return getStringObjectMap();
    }

    //----------- ServiceRegisterRequestDto --------------

    @Bean
    public ProducerFactory<String, ServiceRegisterRequestDto> producerFactorySRR() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, ServiceRegisterRequestDto> kafkaTemplateSRR() {
        return new KafkaTemplate<>(producerFactorySRR());
    }

}
