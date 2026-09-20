package com.example.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.converter.ByteArrayJacksonJsonMessageConverter;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class KafkaConfig {

    @Bean
    public ByteArrayJacksonJsonMessageConverter jsonMessageConverter(JsonMapper jsonMapper) {
        // Jackson 3: JsonMapper вместо ObjectMapper
        return new ByteArrayJacksonJsonMessageConverter(jsonMapper);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, byte[]> cf,
            ByteArrayJacksonJsonMessageConverter converter) {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, byte[]>();
        factory.setConsumerFactory(cf);
        factory.setRecordMessageConverter(converter);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}