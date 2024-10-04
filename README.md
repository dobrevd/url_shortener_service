# URL Shortener Service

## Overview

The **URL Shortener Service** is a microservice that allows users to take long referral links from our application and shorten them into compact URLs. This is especially useful for users who want to share links on social media, where long URLs can appear unattractive and inconvenient to use.

## Features

The primary function of the service is to **shorten URLs**, but it also includes several advanced features such as:

- **Caching**: Both local (in-memory) and using Redis for improved performance.
- **Multithreading**: Handles multiple requests concurrently for efficient processing.
- **Database Integration**: Works with both Postgres and Redis databases.
- **REST API**: Exposes a clean, REST-based interface for easy interaction.
- **Resource Reuse Scheduler**: Optimizes resource management by scheduling the reuse of resources.

## Performance Optimization

To further optimize the microservice’s performance, I’ve implemented a **local cache** directly in memory using a **thread-safe data structure**, significantly improving overall efficiency and reducing latency.

## Microservice Interaction

Main service will communicate with the URL Shortener through its **REST API**, enabling real microservice-based interaction. This setup allows us to address the various challenges that typically arise in microservice architectures, such as scalability, reliability, and service discovery.
