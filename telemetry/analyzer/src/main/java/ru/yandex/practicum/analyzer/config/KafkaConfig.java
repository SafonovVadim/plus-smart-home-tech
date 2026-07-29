package ru.yandex.practicum.analyzer.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.analyzer.deserializer.HubEventDeserializer;
import ru.yandex.practicum.analyzer.deserializer.SensorsSnapshotDeserializer;

import java.util.Properties;


@Configuration
@ConfigurationProperties(prefix = "analyzer.kafka")
@Getter
@Setter
public class KafkaConfig {

    private String bootstrapServers;
    private Consumers consumers;

    @Getter
    @Setter
    public static class Consumers {
        private Snapshots snapshots;
        private Hubs hubs;
    }

    @Getter
    @Setter
    public static class Snapshots {
        private String groupId;
        private String clientId;
        private Boolean enableAutoCommit;
    }

    @Getter
    @Setter
    public static class Hubs {
        private String groupId;
        private String clientId;
        private Boolean enableAutoCommit;
    }

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> snapshotConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumers.getSnapshots().getGroupId());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, consumers.getSnapshots().getClientId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumers.getSnapshots().getEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new KafkaConsumer<>(props);
    }

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> hubConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumers.getHubs().getGroupId());
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, consumers.getHubs().getClientId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumers.getHubs().getEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new KafkaConsumer<>(props);
    }
}