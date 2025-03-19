#!/bin/bash

echo "Stopping Kafka cluster..."

docker-compose -f ./common.yml \
               -f ./zookeeper.yml \
               -f ./kafka_cluster.yml \
               -f ./init_kafka.yml \
               down

echo "Kafka cluster stopped."
