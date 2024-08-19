# Notification Service

## Overview
This project handles real-time notifications based on cryptocurrency price updates. It consumes price updates from a Kafka topic and checks user-defined thresholds. Notifications are sent via WebSocket if a threshold is reached.

## Features
- **Kafka Consumer**: Consumes messages from the `price-updates` topic.
- **Threshold Checker**: Compares cryptocurrency prices to user-defined thresholds.
- **WebSocket Notifications**: Sends real-time notifications to users.

## Prerequisites
- Docker
- Kafka
- Java 17+

## Running the Application

1. **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd notification-service
    ```

2. **Docker compose build and run**:
    ```bash
    docker compose up --build -d
    ```

   The notification-service will be available at `http://localhost:8888`.

## API Endpoints

### Health Check
- **Method**: `GET`
- **URL**: `http://localhost:8888/api/v1/notifications/health`
- **Response**:
    ```json
    "Notification Service is running"
    ```

### Add Notification
- **Method**: `POST`
- **URL**: `http://localhost:8888/api/v1/notifications/{userId}/add`
- **Request Body**:
    ```json
    {
      "token": "BTC",
      "notificationType": "price rise to",
      "notificationValue": 60000,
      "remarks": "BTC has hit $60k"
    }
    ```
- **Response**:
    ```json
    {
      "userId": 1,
      "token": "BTC",
      "notificationType": "price rise to",
      "notificationValue": 60000,
      "remarks": "BTC has hit $60k"
    }
    ```

### Edit Notification
- **Method**: `PUT`
- **URL**: `http://localhost:8888/api/v1/notifications/{userId}/edit/{id}`
- **Request Body**:
    ```json
    {
      "token": "BTC",
      "notificationType": "price rise to",
      "notificationValue": 65000,
      "remarks": "BTC has hit $65k"
    }
    ```
- **Response**:
    ```json
    {
      "userId": 1,
      "token": "BTC",
      "notificationType": "price rise to",
      "notificationValue": 65000,
      "remarks": "BTC has hit $65k"
    }
    ```

### Delete Notification
- **Method**: `DELETE`
- **URL**: `http://localhost:8888/api/v1/notifications/{userId}/delete/{id}`
- **Response**: `204 No Content`

### Get Notifications for User
- **Method**: `GET`
- **URL**: `http://localhost:8888/api/v1/notifications/{userId}`
- **Response**:
    ```json
    [
      {
        "userId": 1,
        "token": "BTC",
        "notificationType": "price rise to",
        "notificationValue": 60000,
        "remarks": "BTC has hit $60k"
      },
      {
        "userId": 1,
        "token": "ETH",
        "notificationType": "price fall to",
        "notificationValue": 4000,
        "remarks": "ETH has dropped to $4000"
      }
    ]
    ```

## WebSocket
- **WebSocket Endpoint**: `/notifications`
- **Subscribe to topic**: `/topic/notifications/{userId}`

## Testing Price Updates
Use the `testNotification.sh` script to test price updates via Kafka.

### Example Usage:
```bash
./testNotification.sh -t price-updates -b kafka:9092 -c bit-scout-backend-notification-kafka-1 BTC 60000
