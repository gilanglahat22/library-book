# Library Management System

A full-stack web application to manage a library's books, authors, and members with CRUD operations, search functionality, and responsive UI.

## Features

### 1. Responsive UI
- Mobile, tablet, and desktop compatible layout
- Modern and clean design with Bootstrap/Tailwind CSS

### 2. Data Management with Relationships
- **Books**: Related to Authors
- **Members**: Library member management
- **BorrowedBooks**: Tracks which member borrowed which book
- **Authors**: Author information management
- Pagination support for all data lists

### 3. CRUD Operations
- **Books**: Add, edit, delete, and list books
- **Authors**: Add, edit, delete, and list authors
- **Members**: Add, edit, delete, and list members
- **BorrowedBooks**: Add, edit, delete, and list borrowed books

### 4. Search Functionality
- Search borrowed books by:
  - Book title
  - Member name
  - Borrow date
- Real-time search with pagination

## Tech Stack

### Frontend
- **React 18** with Next.js 14
- **TypeScript** for type safety
- **Tailwind CSS** for styling
- **React Query** for data fetching
- **React Hook Form** for form management

### Backend
- **Java 17** with Spring Boot 3.1
- **Spring Data JPA** for database operations
- **PostgreSQL** as primary database
- **Spring Security** for authentication
- **Maven** for dependency management

### Database
- **PostgreSQL** for relational data
- **H2** for development/testing

## Project Structure

```
library-management/
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   │   └── com/library/
│   │       ├── LibraryApplication.java
│   │       ├── controller/  # REST controllers
│   │       ├── model/       # Entity classes
│   │       ├── repository/  # Data repositories
│   │       ├── service/     # Business logic
│   │       └── dto/         # Data transfer objects
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── schema.sql
│   └── pom.xml
├── frontend/               # React/Next.js application
│   ├── components/         # Reusable components
│   ├── pages/             # Next.js pages
│   ├── styles/            # CSS/Tailwind styles
│   ├── utils/             # Utility functions
│   ├── types/             # TypeScript types
│   └── package.json
├── docker-compose.yml     # Docker configuration
└── README.md
```

## Getting Started

### Prerequisites
- **Java 17+** (for backend development)
- **Node.js 18+** (for frontend development)
- **PostgreSQL 14+** (for database)
- **Docker & Docker Compose** (recommended for easy setup)
- **Maven 3.6+** (for backend build)

### Quick Start with Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd library-management
   ```

2. **Start all services with Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/api/swagger-ui.html

4. **Stop the services**
   ```bash
   docker-compose down
   ```

### Manual Setup (Development)

#### Database Setup
1. Install PostgreSQL and create a database:
   ```sql
   CREATE DATABASE library_db;
   CREATE USER postgres WITH PASSWORD 'password';
   GRANT ALL PRIVILEGES ON DATABASE library_db TO postgres;
   ```

2. Or use Docker for PostgreSQL only:
   ```bash
   docker run --name library-postgres -e POSTGRES_DB=library_db -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:15-alpine
   ```

#### Backend Setup
1. Navigate to backend directory:
   ```bash
   cd backend
   ```

2. Build and run the Spring Boot application:
   ```bash
   # Using Maven wrapper (recommended)
   ./mvnw clean install
   ./mvnw spring-boot:run
   
   # Or using local Maven installation
   mvn clean install
   mvn spring-boot:run
   ```

3. The backend will be available at `http://localhost:8080`
   - API endpoints: `http://localhost:8080/api/`
   - Swagger documentation: `http://localhost:8080/api/swagger-ui.html`

#### Frontend Setup
1. Navigate to frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies and start development server:
   ```bash
   npm install
   npm run dev
   ```

3. The frontend will be available at `http://localhost:3000`

### Configuration

#### Backend Configuration (`backend/src/main/resources/application.yml`)
- Database connection settings
- Server port configuration
- CORS settings
- Logging levels

#### Frontend Configuration
- API base URL in `frontend/next.config.js`
- Environment variables in `.env.local` (create if needed):
  ```
  NEXT_PUBLIC_API_URL=http://localhost:8080/api
  ```

### Sample Data
The application automatically loads sample data on first startup:
- 5 Authors (F. Scott Fitzgerald, George Orwell, etc.)
- 6 Books (The Great Gatsby, 1984, etc.)
- 5 Members (Jack Smith, Emily Johnson, etc.)
- 4 Borrowed book records with different statuses

### Troubleshooting

#### Backend Issues
- **Port 8080 already in use**: Change server port in `application.yml`
- **Database connection failed**: Verify PostgreSQL is running and credentials are correct
- **Build failures**: Ensure Java 17+ is installed and JAVA_HOME is set

#### Frontend Issues
- **Port 3000 already in use**: Next.js will automatically suggest an alternative port
- **API connection failed**: Verify backend is running and API URL is correct
- **Build errors**: Delete `node_modules` and `package-lock.json`, then run `npm install`

#### Docker Issues
- **Port conflicts**: Modify ports in `docker-compose.yml`
- **Build failures**: Run `docker-compose build --no-cache`
- **Permission issues**: On Linux/Mac, ensure Docker has proper permissions

## API Endpoints

### Books
- `GET /api/books` - List all books with pagination
- `GET /api/books/{id}` - Get book by ID
- `POST /api/books` - Create new book
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book

### Authors
- `GET /api/authors` - List all authors
- `POST /api/authors` - Create new author
- `PUT /api/authors/{id}` - Update author
- `DELETE /api/authors/{id}` - Delete author

### Members
- `GET /api/members` - List all members
- `POST /api/members` - Create new member
- `PUT /api/members/{id}` - Update member
- `DELETE /api/members/{id}` - Delete member

### Borrowed Books
- `GET /api/borrowed-books` - List all borrowed books
- `GET /api/borrowed-books/search` - Search borrowed books
- `POST /api/borrowed-books` - Create new borrowed book record
- `PUT /api/borrowed-books/{id}` - Update borrowed book
- `DELETE /api/borrowed-books/{id}` - Delete borrowed book record

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License. 