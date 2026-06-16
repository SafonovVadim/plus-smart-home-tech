package ru.practicum.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
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

            private Consumer<String, SpecificRecordBase> consumer;

            private Producer<String, SpecificRecordBase> producer;

            @Override
            public Consumer<String, SpecificRecordBase> getConsumer() {
                if (consumer == null) {
                    initConsumer();
                }
                return consumer;
            }

            private void initConsumer() {
                Properties config = new Properties();
                config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);
                config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ru.practicum.kafka.GeneralAvroDeserializer.class);
                config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-client-" + counter.getAndIncrement());
                consumer = new KafkaConsumer<>(config);
            }

            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                if (producer == null) {
                    initProducer();
                }
                return producer;
            }

            private void initProducer() {
                Properties config = new Properties();
                config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
                config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ru.practicum.kafka.GeneralAvroSerializer.class);
                producer = new KafkaProducer<>(config);
            }

            @Override
            public void send(String topic, String key, SpecificRecordBase value) {
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
