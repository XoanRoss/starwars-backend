# Starwars Backend

Backend API to query and process information from the Star Wars universe, consuming data from the public SWAPI API and
exposing REST endpoints with additional business logic (filtering, pagination, caching).

## Main Technologies

- Java 21
- Spring Boot 3.5.4
- Maven
- Docker & Docker Compose

## Installation & Execution

### Requirements

- Java 21
- Maven
- Docker (optional)

### Local Execution

```bash
mvn clean install
mvn spring-boot:run
```

### Docker Execution

```bash
docker build -t starwars-backend .
docker run -p 6969:8080 starwars-backend
```

### Docker Compose Execution

```bash
docker compose up --build
```

## Configuration

Main configuration is in `src/main/resources/application.yml`:

- `api.swapi.base-url`: Base URL for SWAPI API
- `api.swapi.response-timeout`: Timeout for SWAPI API responses (default: 5 s)
- `api.swapi.connect-timeout`: Timeout for connecting to SWAPI API (default: 5 s)
- `cache.caffeine.expire-after-write`: Cache expiration time after write (default: 10 minutes)
- `cache.caffeine.maximum-size`: Maximum cache size (default: 1000)
- `settings.page-size`: Default pagination size (default: 15)

## Main Endpoints

> You can check the OpenAPI documentation at `/swagger-ui.html` once the application is running.

Example endpoints (complete according to implementation):

- `POST /api/people`: List characters with filters and pagination
- `POST /api/planets`: List planets with filters and pagination

## Testing

To run tests:

```bash
mvn test
```

## Project Structure

- `controller/`: REST controllers
- `service/`: Business logic
- `client/`: SWAPI client
- `dto/`: Data transfer objects
- `mapper/`: Entity/DTO mapping
- `exception/`: Error handling
- `config/`: Application configuration

## Additional Notes

- Async procesing and concurrency control are not implemented yet since they are not required for the project and is not
  supposed to receive high number of requests.

## Contribution

Contributions are welcome! Please open an issue or pull request for suggestions or improvements.

## GitHub

You can find the project on GitHub: https://github.com/xoanross/starwars-backend

### Contact

You can contact me at xoanross@gmail.com