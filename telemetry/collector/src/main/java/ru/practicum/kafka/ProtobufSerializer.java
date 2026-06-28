package ru.practicum.kafka;

import com.google.protobuf.MessageLite;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ProtobufSerializer<T extends MessageLite> implements Serializer<T> {

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            return null;
        }
        try {
            return (byte[]) data.getClass().getMethod("toByteArray").invoke(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing Protobuf message", e);
        }
    }
}