package com.simba.kafkainfrastructure.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
public class KafkaProducerConfigData {
    private String keySerializerClass;
    private String valueSerializerClass;
    private Boolean autoRegisterSchemas;
    private Boolean useLatestVersion;
    private Boolean latestCompatibilityStrict;
    private String compressionType;
    private String acks;
    private Integer batchSize;
    private Integer lingerMs;
    private Integer requestTimeoutMs;
    private Integer retryCount;
}