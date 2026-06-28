package ru.practicum.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class ProtobufDeserializer<T extends MessageLite> implements Deserializer<T> {

    private Class<T> protobufClass;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String className = (String) configs.get("protobuf.message.class");
        if (className == null) {
            throw new IllegalArgumentException("Configuration 'protobuf.message.class' is required");
        }
        try {
            protobufClass = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new SerializationException("Class not found: " + className, e);
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return (T) protobufClass.getMethod("parseFrom", byte[].class).invoke(null, data);
        } catch (Exception e) {
            throw new SerializationException("Error parsing Protobuf message", e);
        }
    }
}