// telemetry/collector/src/main/java/ru/practicum/kafka/KafkaClientConfig.java
package ru.practicum.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class KafkaClientConfig {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Bean
    public KafkaClient getClient() {
        return new KafkaClient() {

            private Consumer<String, byte[]> consumer;
            private Producer<String, byte[]> producer;

            @Override
            public Consumer<String, byte[]> getConsumer() {
                if (consumer == null) {
                    initConsumer();
                }
                return consumer;
            }

            private void initConsumer() {
                Properties config = new Properties();
                config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
                config.put(ConsumerConfig.GROUP_ID_CONFIG, "grpc-consumer-" + counter.getAndIncrement());
                consumer = new KafkaConsumer<>(config);
            }

            @Override
            public Producer<String, byte[]> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
            }

            private void initProducer() {
                Properties config = new Properties();
                config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
                producer = new KafkaProducer<>(config);
            }

            @Override
            public void send(String topic, String key, byte[] value) {
                getProducer().send(new ProducerRecord<>(topic, key, value));
            }

            @Override
            public void stop() {
                if (consumer != null) {
                    consumer.close();
                }
                if (producer != null) {
                    producer.close();
                }
            }
        };
    }
}