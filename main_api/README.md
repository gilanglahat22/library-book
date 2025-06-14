# Library Management System - Main API

Main API Gateway untuk Library Management System yang menggunakan API Integrator.

## Fitur

- Keamanan berbasis IP Statis (IP Whitelist)
- Pengelolaan API Key untuk akses ke API Integrator
- Circuit Breaker & Retry untuk meningkatkan resiliensi
- Swagger UI untuk dokumentasi API
- Penggunaan API Key yang berbeda untuk setiap endpoint API Integrator

## Struktur Proyek

```
main_api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── library/
│   │   │           └── main_api/
│   │   │               ├── config/         # Konfigurasi aplikasi
│   │   │               ├── controller/     # Controller API 
│   │   │               ├── exception/      # Exception handler
│   │   │               ├── service/        # Service
│   │   │               └── MainApiApplication.java
│   │   └── resources/
│   │       └── application.yml             # Konfigurasi aplikasi
│   └── test/                               # Unit test
├── Dockerfile                              # Konfigurasi Docker
├── pom.xml                                 # Dependency Maven
└── env.example                             # Contoh variabel lingkungan
```

## Cara Menjalankan

### Melalui Maven

1. Clone repositori
2. Salin `env.example` ke `.env` dan sesuaikan nilai-nilainya
3. Jalankan perintah:

```bash
mvn spring-boot:run
```

### Melalui Docker

1. Build image:

```bash
docker build -t library-main-api .
```

2. Jalankan container:

```bash
docker run -p 8090:8090 \
  -e SERVER_PORT=8090 \
  -e API_INTEGRATOR_BASE_URL=http://api_integrator:8080 \
  -e ALLOWED_IPS=172.18.0.1 \
  -e FRONTEND_IP=127.0.0.1 \
  -e API_KEY_ADMIN=admin-api-key-123 \
  -e API_KEY_BOOKS=books-api-key-456 \
  -e API_KEY_AUTHORS=authors-api-key-789 \
  -e API_KEY_BORROWED_BOOKS=borrowed-books-api-key-101 \
  --name library-main-api \
  library-main-api
```

## Konfigurasi Keamanan

### IP Statis

Untuk membatasi akses API, konfigurasikan IP yang diizinkan di `.env` atau environment variables:

```
ALLOWED_IPS=172.18.0.1
```

### Frontend IP

Untuk CORS, tentukan IP frontend:

```
FRONTEND_IP=127.0.0.1
```

### API Key

Untuk akses ke API Integrator, konfigurasikan API key:

```
API_KEY_ADMIN=admin-api-key-123
API_KEY_BOOKS=books-api-key-456
API_KEY_AUTHORS=authors-api-key-789
API_KEY_BORROWED_BOOKS=borrowed-books-api-key-101
```

## Dokumentasi API

Swagger UI tersedia di:

```
http://localhost:8090/api/swagger-ui.html
```

API Docs tersedia di:

```
http://localhost:8090/api/docs
```

## Integrasi dengan Frontend

Frontend dapat mengakses API melalui:

```
http://localhost:8090/api/{endpoint}
```

Pastikan IP address frontend terdaftar di konfigurasi `ALLOWED_IPS` dan `FRONTEND_IP`. 