# Library Management System API

A RESTful API for managing a library system with books, authors, members, and borrowed books.

## Table of Contents

- [Setup](#setup)
  - [Environment Configuration](#environment-configuration)
  - [Available Environment Variables](#available-environment-variables)
- [API Security](#api-security)
  - [API Key Authentication](#api-key-authentication)
  - [Available API Keys](#available-api-keys)
  - [Customizing API Keys](#customizing-api-keys)
- [API Documentation](#api-documentation)
- [Running the Application](#running-the-application)
  - [Using Docker](#using-docker)
  - [Using Docker Compose](#using-docker-compose)
- [API Examples](#api-examples)
  - [Books API](#books-api)
  - [Authors API](#authors-api)
  - [Borrowed Books API](#borrowed-books-api)

## Setup

### Environment Configuration

This application uses environment variables for configuration. An example environment file is provided (`env.example`).

To set up your environment:

1. Copy the example environment file:

```bash
cp env.example .env
```

2. Edit the `.env` file to customize your configuration:

```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/library_db
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password

# API Key Configuration
API_KEY_HEADER_NAME=X-API-KEY
API_KEY_ADMIN=your-custom-admin-key
API_KEY_BOOKS=your-custom-books-key
API_KEY_AUTHORS=your-custom-authors-key
API_KEY_BORROWED_BOOKS=your-custom-borrowed-books-key
```

### Available Environment Variables

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `DB_URL` | JDBC URL for database connection | `jdbc:postgresql://postgres:5432/librarydb` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |
| `DB_NAME` | Database name | `librarydb` |
| `API_KEY_HEADER_NAME` | HTTP header name for API key | `X-API-KEY` |
| `API_KEY_ADMIN` | API key for admin access | `admin-api-key-123` |
| `API_KEY_BOOKS` | API key for books endpoints | `books-api-key-456` |
| `API_KEY_AUTHORS` | API key for authors endpoints | `authors-api-key-789` |
| `API_KEY_BORROWED_BOOKS` | API key for borrowed books endpoints | `borrowed-books-api-key-101` |
| `SERVER_PORT` | Port for the server to listen on | `8080` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate DDL auto strategy | `update` |
| `SPRING_JPA_SHOW_SQL` | Whether to show SQL in logs | `true` |
| `SPRING_JPA_FORMAT_SQL` | Whether to format SQL in logs | `true` |
| `CORS_ALLOWED_ORIGINS` | Allowed origins for CORS | `http://localhost:3000` |
| `CORS_ALLOWED_METHODS` | Allowed methods for CORS | `GET,POST,PUT,DELETE,OPTIONS` |
| `LOGGING_LEVEL_ROOT` | Root logging level | `INFO` |
| `LOGGING_LEVEL_COM_LIBRARY` | Application logging level | `DEBUG` |

## API Security

The API is secured using API key authentication. All endpoints (except public ones like Swagger UI) require a valid API key to be sent in the header of the request.

### API Key Authentication

For all API requests, include the API key in the request header:

```
X-API-KEY: your-api-key-here
```

The header name can be customized using the `API_KEY_HEADER_NAME` environment variable.

### Available API Keys

The system uses the following default API keys:

| API Key | Role | Access |
|---------|------|--------|
| `admin-api-key-123` | ADMIN | All endpoints |
| `books-api-key-456` | BOOKS | Book-related endpoints (/books/**) |
| `authors-api-key-789` | AUTHORS | Author-related endpoints (/authors/**) |
| `borrowed-books-api-key-101` | BORROWED_BOOKS | Borrowed books endpoints (/borrowed-books/**) |

### Customizing API Keys

You can customize the API keys using one of these methods:

1. **Using .env File**:
   - Edit the `.env` file to set your custom API keys
   - Restart the application to apply changes

2. **Using Environment Variables**:
   ```bash
   export API_KEY_ADMIN=custom-admin-key
   export API_KEY_BOOKS=custom-books-key
   export API_KEY_AUTHORS=custom-authors-key
   export API_KEY_BORROWED_BOOKS=custom-borrowed-books-key
   ```

3. **Using Docker Compose Environment Variables**:
   - Set environment variables in docker-compose.yml or .env file at project root

### Access Control

The following endpoints have specific access control requirements:

- `/books/**` - Requires ADMIN or BOOKS role
- `/authors/**` - Requires ADMIN or AUTHORS role
- `/borrowed-books/**` - Requires ADMIN or BORROWED_BOOKS role
- `/api-auth-test` - Test endpoint to verify authentication
- `/`, `/swagger-ui/**`, `/api-docs/**` - Public endpoints, no authentication required

### Testing Authentication

You can test authentication using the `/api-auth-test` endpoint:

```bash
# Test without API key (should fail)
curl -i http://localhost:8080/api-auth-test

# Test with valid API key
curl -i -H "X-API-KEY: admin-api-key-123" http://localhost:8080/api-auth-test
```

## API Documentation

The API documentation is available at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

The Swagger UI allows you to:
1. View all available endpoints
2. Test endpoints directly in the browser
3. Authenticate using your API key (click the "Authorize" button)

## Running the Application

### Using Docker

To run the application using Docker:

```bash
# Build the Docker image
docker build -t library-backend .

# Run the container with environment variables from .env file
docker run -p 8080:8080 --env-file .env library-backend
```

### Using Docker Compose

To run the entire stack (backend, frontend, and database) using Docker Compose:

1. Make sure you have set up your `.env` file as described above.

2. Start the application stack:
   ```bash
   docker-compose up -d
   ```

This will start all services with the configuration from your `.env` file.

#### Customizing Docker Compose

You can override any environment variable in the `docker-compose.yml` file by setting it in your `.env` file. For example:

```bash
# .env
SERVER_PORT=9090
API_KEY_ADMIN=my-special-admin-key
```

## API Examples

### Books API

#### Get all books
```bash
curl -X 'GET' \
  'http://localhost:8080/books?page=0&size=10&sortBy=title&sortDir=asc' \
  -H 'accept: */*' \
  -H 'X-API-KEY: books-api-key-456'
```

#### Get book by ID
```bash
curl -X 'GET' \
  'http://localhost:8080/books/1' \
  -H 'accept: */*' \
  -H 'X-API-KEY: books-api-key-456'
```

#### Create a new book
```bash
curl -X 'POST' \
  'http://localhost:8080/books' \
  -H 'accept: */*' \
  -H 'X-API-KEY: books-api-key-456' \
  -H 'Content-Type: application/json' \
  -d '{
  "title": "The Great Gatsby",
  "isbn": "978-0-7432-7356-5",
  "category": "Fiction",
  "publishingYear": 2020,
  "description": "A classic American novel about the Jazz Age",
  "author": {
    "id": 1
  },
  "totalCopies": 3,
  "availableCopies": 3
}'
```

#### Update a book
```bash
curl -X 'PUT' \
  'http://localhost:8080/books/1' \
  -H 'accept: */*' \
  -H 'X-API-KEY: books-api-key-456' \
  -H 'Content-Type: application/json' \
  -d '{
  "title": "The Great Gatsby (Updated)",
  "isbn": "978-0-7432-7356-5",
  "category": "Fiction",
  "publishingYear": 2020,
  "description": "Updated description",
  "author": {
    "id": 1
  },
  "totalCopies": 3,
  "availableCopies": 3
}'
```

#### Delete a book
```bash
curl -X 'DELETE' \
  'http://localhost:8080/books/1' \
  -H 'accept: */*' \
  -H 'X-API-KEY: books-api-key-456'
```

### Authors API

#### Get all authors
```bash
curl -X 'GET' \
  'http://localhost:8080/authors?page=0&size=10&sortBy=name&sortDir=asc' \
  -H 'accept: */*' \
  -H 'X-API-KEY: authors-api-key-789'
```

#### Get author by ID
```bash
curl -X 'GET' \
  'http://localhost:8080/authors/1' \
  -H 'accept: */*' \
  -H 'X-API-KEY: authors-api-key-789'
```

#### Create a new author
```bash
curl -X 'POST' \
  'http://localhost:8080/authors' \
  -H 'accept: */*' \
  -H 'X-API-KEY: authors-api-key-789' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "F. Scott Fitzgerald",
  "biography": "American novelist and short story writer",
  "nationality": "American",
  "birthYear": 1896
}'
```

#### Update an author
```bash
curl -X 'PUT' \
  'http://localhost:8080/authors/1' \
  -H 'accept: */*' \
  -H 'X-API-KEY: authors-api-key-789' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "F. Scott Fitzgerald",
  "biography": "Updated biography",
  "nationality": "American",
  "birthYear": 1896
}'
```

#### Delete an author
```bash
curl -X 'DELETE' \
  'http://localhost:8080/authors/1' \
  -H 'accept: */*' \
  -H 'X-API-KEY: authors-api-key-789'
```

### Borrowed Books API

#### Get all borrowed books
```bash
curl -X 'GET' \
  'http://localhost:8080/borrowed-books' \
  -H 'accept: */*' \
  -H 'X-API-KEY: borrowed-books-api-key-101'
```

#### Get borrowed book by ID
```bash
curl -X 'GET' \
  'http://localhost:8080/borrowed-books/1' \
  -H 'accept: */*' \
  -H 'X-API-KEY: borrowed-books-api-key-101'
```

#### Borrow a book
```bash
curl -X 'POST' \
  'http://localhost:8080/borrowed-books' \
  -H 'accept: */*' \
  -H 'X-API-KEY: borrowed-books-api-key-101' \
  -H 'Content-Type: application/json' \
  -d '{
  "member": {
    "id": 1
  },
  "book": {
    "id": 1
  },
  "borrowDate": "2023-06-10",
  "dueDate": "2023-06-24",
  "notes": "First borrow"
}'
```

#### Return a book
```bash
curl -X 'PUT' \
  'http://localhost:8080/borrowed-books/1/return?returnDate=2023-06-20' \
  -H 'accept: */*' \
  -H 'X-API-KEY: borrowed-books-api-key-101'
```

#### Get overdue books
```bash
curl -X 'GET' \
  'http://localhost:8080/borrowed-books/overdue' \
  -H 'accept: */*' \
  -H 'X-API-KEY: borrowed-books-api-key-101'
``` 