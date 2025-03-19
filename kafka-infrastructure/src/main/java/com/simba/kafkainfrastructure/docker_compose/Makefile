# Variables
COMPOSE_FILES = -f common.yml -f zookeeper.yml -f kafka_cluster.yml -f init_kafka.yml
COMPOSE_CMD = docker-compose $(COMPOSE_FILES)

# Targets

.PHONY: up down restart logs ps

up:
	@echo "Starting Kafka Infrastructure..."
	$(COMPOSE_CMD) up -d
	@echo "Waiting for Kafka brokers to be ready..."
	sleep 10
	@echo "Kafka cluster started successfully!"

down:
	@echo "Stopping Kafka Infrastructure..."
	$(COMPOSE_CMD) down
	@echo "Kafka cluster stopped."

restart: down up

logs:
	@$(COMPOSE_CMD) logs -f

ps:
	@$(COMPOSE_CMD) ps
