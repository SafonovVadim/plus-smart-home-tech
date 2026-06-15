package ru.practicum.kafka;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.practicum.models.HubEvent;
import ru.practicum.models.SensorEvent;
import ru.practicum.models.Topics;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import static ru.practicum.dto.AvroToDtoConverter.toHubEvent;
import static ru.practicum.dto.AvroToDtoConverter.toSensorEvent;

public class GeneralAvroDeserializer implements Deserializer<Object> {
    private final DecoderFactory decoderFactory = DecoderFactory.get();

    @Override
    public Object deserialize(String topic, byte[] bytes) {
        try {
            if (bytes == null || bytes.length == 0) return null;

            if (Topics.HUB_TOPIC.equals(topic)) {
                return deserializeHubEvent(bytes);
            } else if (Topics.SENSOR_TOPIC.equals(topic)) {
                return deserializeSensorEvent(bytes);
            }
            throw new IllegalArgumentException("Неизвестный топик: " + topic);
        } catch (Exception e) {
            throw new SerializationException("Ошибка десериализации данных из топика [" + topic + "]", e);
        }
    }

    private HubEvent deserializeHubEvent(byte[] bytes) throws Exception {
        DatumReader<HubEventAvro> reader = new SpecificDatumReader<>(HubEventAvro.getClassSchema());
        BinaryDecoder decoder = decoderFactory.binaryDecoder(bytes, null);
        HubEventAvro avro = reader.read(null, decoder);
        return toHubEvent(avro);
    }

    private SensorEvent deserializeSensorEvent(byte[] bytes) throws Exception {
        DatumReader<SensorEventAvro> reader = new SpecificDatumReader<>(SensorEventAvro.getClassSchema());
        BinaryDecoder decoder = decoderFactory.binaryDecoder(bytes, null);
        SensorEventAvro avro = reader.read(null, decoder);
        return toSensorEvent(avro);
    }
}