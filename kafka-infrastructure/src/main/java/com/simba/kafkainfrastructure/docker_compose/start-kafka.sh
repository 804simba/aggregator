#!/bin/bash

echo "Starting Zookeeper and Kafka cluster..."

docker-compose -f ./common.yml \
               -f ./zookeeper.yml \
               -f ./kafka_cluster.yml \
               -f ./init_kafka.yml \
               up -d

echo "Waiting for Kafka brokers to be ready..."
sleep 10

echo "Kafka cluster and topics initialized successfully!"
