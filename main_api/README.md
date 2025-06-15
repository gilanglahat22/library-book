# Main API Gateway

This is the main API gateway for the Library Management System. It serves as a distributor between the API Integrator and the frontend client.

## Features

- Acts as a gateway between the frontend and the API Integrator
- Handles API key authentication with the API Integrator
- Provides a unified API for the frontend
- Includes Swagger UI documentation

## API Endpoints

The Main API provides the following endpoints:

- `/books/**` - Book management endpoints
- `/authors/**` - Author management endpoints
- `/borrowed-books/**` - Borrowed books management endpoints
- `/members/**` - Member management endpoints
- `/health` - Health check endpoint
- `/swagger-ui.html` - Swagger UI documentation

## Configuration

The Main API can be configured using environment variables:

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `SERVER_PORT` | Port for the server to listen on | `8090` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `API_INTEGRATOR_BASE_URL` | Base URL for the API Integrator | `http://localhost:8080` |
| `API_INTEGRATOR_TIMEOUT` | Timeout for API Integrator requests (ms) | `5000` |
| `API_KEY_HEADER_NAME` | HTTP header name for API key | `X-API-KEY` |
| `API_KEY_ADMIN` | API key for admin access | `admin-api-key-123` |
| `API_KEY_BOOKS` | API key for books endpoints | `books-api-key-456` |
| `API_KEY_AUTHORS` | API key for authors endpoints | `authors-api-key-789` |
| `API_KEY_BORROWED_BOOKS` | API key for borrowed books endpoints | `borrowed-books-api-key-101` |

## Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using Docker

```bash
docker build -t library-main-api .
docker run -p 8090:8090 library-main-api
```

### Using Docker Compose

```bash
docker-compose up -d
```

## API Documentation

The API documentation is available at:
- Swagger UI: http://localhost:8090/swagger-ui.html
- OpenAPI JSON: http://localhost:8090/api-docs 