package ru.yandex.practicum.analyzer.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;


@Configuration
@ConfigurationProperties(prefix = "aggregator.kafka")
@Getter
@Setter
public class KafkaConfig {

    private String bootstrapServers;
    private KafkaConfig.Producer producer;
    private KafkaConfig.Consumer consumer;

    @Getter
    @Setter
    public static class Producer {
        private String keySerializer;
        private String valueSerializer;
        private Integer retries = 0;
    }

    @Getter
    @Setter
    public static class Consumer {
        private String groupId;
        private String keyDeserializer;
        private String valueDeserializer;
        private String autoOffsetReset;
        private Boolean enableAutoCommit;
    }

    @Bean
    public org.apache.kafka.clients.consumer.Consumer<String, SpecificRecordBase> kafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumer.getValueDeserializer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumer.getGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumer.getAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumer.getEnableAutoCommit());

        return new KafkaConsumer<>(props);
    }

    @Bean
    public org.apache.kafka.clients.producer.Producer<String, SpecificRecordBase> kafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producer.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producer.getValueSerializer());
        props.put(ProducerConfig.RETRIES_CONFIG, producer.getRetries());

        return new KafkaProducer<>(props);
    }
}