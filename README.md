# Farm Supervisor Graduation Project

Farm Supervisor is an Android mobile application, complemented by two microservices, designed to assist Palestinian farmers in their agricultural endeavors by providing plant-related recommendations and warnings. Currently, the application focuses exclusively on the avocado plant.

![Farm Supervisor App Logo](https://github.com/ahmaide/Farm-Supervisor-Graduation-Project/assets/87603637/f7bf8f4a-4630-4120-ab55-019c86fde7c7)

## Project Applications

This project comprises three applications: the main mobile application and two microservices.

### Mobile Application

The mobile application is developed using Android Studio with the Java programming language and Gradle for dependency management.

### Spring Boot Authentication Service

This service is built with the Spring Boot framework in Java and utilizes Maven for dependency management. It connects to a PostgreSQL database and manages user data, including lands, crops, and specializations. Additionally, it handles authentication using JWT tokens.

### Django Recommendations Service

Developed with Python's Django framework, this service uses a MongoDB database to store plant-related data (currently focused on avocados). It provides recommendations, warnings, and information on pests, diseases, care methods, cures, and necessary tools. The service also considers the relationship between plants, user locations, and weather conditions.

## Application Demo
[![Farm Supervisor Demo](http://img.youtube.com/vi/BEska5wem78/0.jpg)](http://www.youtube.com/watch?v=BEska5wem78 "Farm Supervisor Demo")

## Repository Additional Files

- **Project Report**: A comprehensive document detailing the project's scope, development process, and outcomes.
- **Plant Related Data CSV Files**: CSV files containing the JSON format of the MongoDB data related to plants (currently focusing on avocados).

