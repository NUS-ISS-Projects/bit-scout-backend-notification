#!/bin/bash

CONTAINER_NAME="bit-scout-backend-notification-kafka-1"
TOPIC="price-updates"
BROKER="kafka:9092"

# Function to display help
show_help() {
  echo "Usage: $0 [-t TOPIC] [-b BROKER] [-c CONTAINER_NAME] TOKEN PRICE"
  echo "Options:"
  echo "  -t TOPIC            Kafka topic to send the message (default: price-updates)"
  echo "  -b BROKER           Kafka broker (default: kafka:9092)"
  echo "  -c CONTAINER_NAME   Docker container name (default: bit-scout-backend-notification-kafka-1)"
  echo "Arguments:"
  echo "  TOKEN        The token name (e.g., BTC, ETH)"
  echo "  PRICE        The price value (e.g., 60000)"
}

# Parse options
while getopts ":t:b:c:h" opt; do
  case $opt in
    t) TOPIC=$OPTARG ;;
    b) BROKER=$OPTARG ;;
    c) CONTAINER_NAME=$OPTARG ;;
    h) show_help; exit 0 ;;
    \?) echo "Invalid option -$OPTARG"; show_help; exit 1 ;;
  esac
done
shift $((OPTIND - 1))

# Check if TOKEN and PRICE are provided
if [ $# -ne 2 ]; then
  echo "Error: TOKEN and PRICE are required."
  show_help
  exit 1
fi

TOKEN=$1
PRICE=$2

# Function to send a message inside Docker container
send_mock_price_update() {
  local token=$1
  local price=$2
  local message="{\"token\": \"$token\", \"price\": $price}"
  
  # Exec into the Docker container and run Kafka producer command
  docker exec -it $CONTAINER_NAME bash -c "echo '$message' | kafka-console-producer.sh --broker-list $BROKER --topic $TOPIC"
}

# Send price update
send_mock_price_update "$TOKEN" "$PRICE"
