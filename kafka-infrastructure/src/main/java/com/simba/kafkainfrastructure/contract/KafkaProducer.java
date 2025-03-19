package com.simba.kafkainfrastructure.contract;

import org.springframework.kafka.support.SendResult;

import java.util.function.BiConsumer;

public interface KafkaProducer<K, V> {
    void send(String topicName, K key, V message, BiConsumer<SendResult<K, V>, Throwable> callback);
}
