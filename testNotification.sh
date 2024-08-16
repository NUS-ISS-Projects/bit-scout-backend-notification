#!/bin/bash

TOPIC="price-updates"
BROKER="localhost:9092"

# Function to send a message
send_mock_price_update() {
  local token=$1
  local price=$2
  local message="{\"token\": \"$token\", \"price\": $price}"
  echo "$message" | kafka-console-producer.sh --broker-list $BROKER --topic $TOPIC
}

# Simulate price updates for different tokens
send_mock_price_update "BTC" 60000
send_mock_price_update "ETH" 4000
send_mock_price_update "ADA" 2
