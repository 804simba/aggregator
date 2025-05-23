package com.simba.kafkainfrastructure.contract;

import java.util.List;

public interface KafkaConsumer<T> {
    void receive(List<T> messages, List<String> keys, List<Integer> partitions, List<Long> offsets);
}
