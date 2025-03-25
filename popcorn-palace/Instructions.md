# Popcorn Palace Movie Ticket Booking System

## Overview
The Popcorn Palace Movie Ticket Booking System is a backend service designed to handle various operations related to movie, showtime, and booking management.

## Jump Start
For your convenience, `compose.yml` includes a PostgreSQL DB, and the app is already pointing to this connection. In addition, you have the schema and data SQL files that can set up your DB schema and initialize data.

## Prerequisite
1.Docker - [Download Docker Desktop](https://www.docker.com/products/docker-desktop/)

## Instructions

### Setting Up

1. Clone the git repository to a place of your choosing.
2. Open the project in your favorite IDE.
3. Open the terminal and change your directory to: `/Popcorn_Palace/popcorn-palace`.
4. Prepare a Docker environment (e.g., Docker Desktop).
5. In the terminal, execute: `docker compose up -d`.

### Starting Up the App

1. Now you can go ahead and start up the app by going to the `PopcornPalaceApplication.java` file, which is located inside: `src/main/java/com/att/tdp/popcorn_palace`.
2. Simply run the application in your IDE (e.g., F5/CTRL+F5 in VS Code).
3. The app is now running, and you can start working with the API (e.g., using Postman).

### Testing

In order to run the test files of the app:
1. Go to the terminal to the same directory you started up the Docker from: `/Popcorn_Palace/popcorn-palace`.
2. Execute in the terminal: `mvn test`.
3. Wait for a few moments, and the test files will run and print their results.

## Conclusion
Follow the above instructions to set up, run, and test the Popcorn Palace Movie Ticket Booking System.
Below is the description of each API and the routes it uses to alter the DB


## Functionality
The system provides the following APIs:

- **Movie API**: Manages movies available on the platform.
- **Showtime API**: Manages movie showtimes in the theaters.
- **Booking API**: Manages movie ticket bookings.

-The system manages live movie screenings across different showtimes and allows users to book tickets. It also automatically removes outdated data, such as showtimes and bookings for movies that are no longer being shown in the cinema.

-To maintain scheduling consistency, the system prevents a movie’s duration from being extended unless all its scheduled showtimes can accommodate the new duration without overlapping with other showtimes.

## Technical Aspects
The system is built using Java Spring Boot, leveraging its robust framework for creating RESTful APIs. Data persistence can be managed using an in-memory database like H2 for simplicity, or a more robust solution like PostgreSQL for production

## APIs

### Movies APIs

| API Description           | Endpoint               | Request Body                          | Response Status | Response Body |
|---------------------------|------------------------|---------------------------------------|-----------------|---------------|
| Get all movies            | GET /movies/all        |                                       | 200 OK          | [ { "id": 12345, "title": "Sample Movie Title 1", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }, { "id": 67890, "title": "Sample Movie Title 2", "genre": "Comedy", "duration": 90, "rating": 7.5, "releaseYear": 2024 } ] |
| Add a movie               | POST /movies           | { "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 } | 200 OK          | { "id": 1, "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 } |
| Update a movie            | POST /movies/update/{movieTitle} | { "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 } | 200 OK          | |
| Delete a movie            | DELETE /movies/{movieTitle} |                                       | 200 OK          | |

### Showtimes APIs

| API Description           | Endpoint               | Request Body                          | Response Status | Response Body |
|---------------------------|------------------------|---------------------------------------|-----------------|---------------|
| Get showtime by ID        | GET /showtimes/{showtimeId} |                                       | 200 OK          | { "id": 1, "price":50.2, "movieId": 1, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" } |
| Add a showtime            | POST /showtimes        | { "movieId": 1, "price":20.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" } | 200 OK          | { "id": 1, "price":50.2, "movieId": 1, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" } |
| Update a showtime         | POST /showtimes/update/{showtimeId} | { "movieId": 1, "price":50.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" } | 200 OK          | |
| Delete a showtime         | DELETE /showtimes/{showtimeId} |                                       | 200 OK          | |

### Bookings APIs

| API Description           | Endpoint               | Request Body                          | Response Status | Response Body |
|---------------------------|------------------------|---------------------------------------|-----------------|---------------|
| Book a ticket             | POST /bookings         | { "showtimeId": 1, "seatNumber": 15, "userId": "84438967-f68f-4fa0-b620-0f08217e76af" } | 200 OK          | { "bookingId": "d1a6423b-4469-4b00-8c5f-e3cfc42eacae" } |


### Next Steps
1.Implement DTO
2.Instead of implementing validations inside the Services use DTO with annotations to validate the data
3.using SQL relations like @ManyToOne, instead of regular enteties and implementing deep logic by myself (e.g-deep delete that I implemented by myself)