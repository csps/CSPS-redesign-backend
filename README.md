# CSPS Backend API Server

<p align="center">
    <img width="200" height="200" src="https://raw.githubusercontent.com/csps/.github/main/images/CSPS_LOGO.png">
</p>

A modern Spring Boot 3.5.4 REST API backend for UC Main CSP-S student merchandise and event management system. Built with JWT authentication, MapStruct for DTOs, and MySQL for data persistence.

## Quick Start

### Prerequisites

- **Java 24** (or higher)
- **Maven 3.9+**
- **MySQL 8.0+**

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/csps/backend.git
   cd backend
   ```

2. Build and run:
   ```bash
   mvn clean package
   mvn spring-boot:run
   ```

API runs on `http://localhost:8080`

## Environment Configuration

Set these environment variables in `application.properties` or via system environment:

### Database

- `DB_URL`: MySQL connection URL
- `DB_USERNAME`: Database user
- `DB_PASSWORD`: Database password

### Security & JWT

- `JWT_SECRET`: Secret key for signing JWT tokens

### AWS S3 (File Storage)

- `AWS_ACCESS_KEY_ID`: AWS access key
- `AWS_SECRET_ACCESS_KEY`: AWS secret key
- `AWS_REGION`: AWS region (e.g., `us-east-1`)
- `AWS_S3_BUCKET`: S3 bucket name

### CloudFront (CDN)

- `CLOUDFRONT_DOMAIN`: CloudFront distribution domain
- `CLOUDFRONT_KEY_PAIR_ID`: CloudFront key pair ID
- `CLOUDFRONT_PRIVATE_KEY_PATH`: Path to CloudFront private key

### Username & Password Formats

- `USERNAME_FORMAT`: Prefix for generated usernames
- `PASSWORD_FORMAT`: Prefix for generated passwords
- `ADMIN_USERNAME_FORMAT`: Prefix for admin usernames
- `ADMIN_PASSWORD_FORMAT`: Prefix for admin passwords

### Email (SMTP)

- `SMTP_HOST`: SMTP server host (e.g., `smtp.gmail.com`)
- `SMTP_PORT`: SMTP port (e.g., `587`)
- `SMTP_USERNAME`: Email address for sending
- `SMTP_PASSWORD`: Email app password

### MetaGraph API (Integration)

- `METAGRAPH_URL`: MetaGraph API endpoint
- `METAGRAPH_API_KEY`: API key for MetaGraph
- `METAGRAPH_PAGEID`: Page ID for MetaGraph

### Docker Setup

Copy `compose.yml.example` to `compose.yml` and fill in values, then run:

```bash
docker-compose up -d
```

## Contributing

We welcome contributions! Please follow these guidelines:

1. **Branching**: Create feature branches from `main` (`feat/your-feature`)
2. **Commits**: Use clear messages (`feat: add feature`, `fix: bug fix`, `refactor: code improvement`)
3. **Code Style**: Follow Java conventions (camelCase variables, PascalCase classes)
4. **Pull Requests**: Include description and testing details

### Bug Reports & Features

- Report issues with detailed reproduction steps
- Suggest features via GitHub issues

## Development



### Database Migrations

```bash
mvn spring-boot:run (auto-applies via JPA)
```

## License

All rights reserved.

Copyright (c) 2026 **UC Main CSP-S**

This software must **NOT** be modified or distributed without prior written consent of the copyright holders. Unauthorized reproduction or distribution will result in legal action.

## Support

For questions or issues, contact the development team via GitHub issues or reach out to the UC Main CSP-S organization.
