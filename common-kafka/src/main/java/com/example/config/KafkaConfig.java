package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
@Configuration
public class KafkaConfig {

    @Bean
    public ByteArrayJsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        ByteArrayJsonMessageConverter converter = new ByteArrayJsonMessageConverter(objectMapper);
        return converter;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, byte[]> cf,
            ByteArrayJsonMessageConverter converter) {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, byte[]>();
        factory.setConsumerFactory(cf);
        factory.setRecordMessageConverter(converter);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}