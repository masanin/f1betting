# F1 Betting Backend Service

A reactive Spring Boot application for Formula 1 betting, built with WebFlux and R2DBC for high-performance,
non-blocking operations.

## 🏁 Overview

This backend service provides a REST API for Formula 1 betting operations including:

- Viewing F1 events with driver markets and odds
- Placing bets on race outcomes
- Processing race results and calculating payouts
- Managing user balances

## 🚀 Quick Start

### Prerequisites

- Java 24 or higher
- Gradle 8.x+
- Git

### Installation & Running

1. Clone the repository:

```bash
git clone https://github.com/masanin/f1betting.git
cd f1-betting-backend
```

2. Build the project:

```bash
./gradlew clean build
```

3. Run the application:

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Running with docker compose

1. Clone the repository:

```bash
git clone https://github.com/masanin/f1betting.git
cd f1-betting-backend
```

2. Build docker image:

```bash
make build
```

3. Run the application with docker compose:

```bash
 make run
```

The application will start on `http://localhost:8080`

## 📋 API Endpoints

### 1. List F1 Events

**GET** `/api/v1/events`

Query Parameters:

- `sessionType` (optional): Filter by session type
- `year` (optional): Filter by year
- `country` (optional): Filter by country

Example:

```bash
curl "http://localhost:8080/api/v1/events?year=2024&country=Monaco"
```

Response:

```json
[
  {
    "eventId": 1234,
    "eventName": "Monaco Grand Prix",
    "sessionType": "Race",
    "year": 2024,
    "country": "Monaco",
    "driverMarkets": [
      {
        "driverId": 1,
        "driverName": "Max Verstappen",
        "odds": 2.0
      }
    ]
  }
]
```

### 2. Place a Bet

**POST** `/api/v1/bets`

Request Body:

```json
{
  "userId": "user123",
  "eventId": 1234,
  "driverId": 1,
  "amount": 50.00
}
```

Response:

```json
{
  "betId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user123",
  "eventId": 1234,
  "driverId": 1,
  "amount": 50.00,
  "odds": 2.0,
  "status": "PENDING"
}
```

### 3. Get User Bets

**GET** `/api/v1/bets/{userId}`

Example:

```bash
curl "http://localhost:8080/api/v1/bets/user123"
```

### 4. Process Event Outcome

**POST** `/api/v1/webhooks/event-outcome`

Request Body:

```json
{
  "eventId": 1234,
  "winnerDriverId": 1
}
```

Response:

```json
{
  "eventId": 1234,
  "winnerDriverId": 1,
  "totalBetsUpdated": 25,
  "totalPayout": 1500.00
}
```

### 5. Register User

**POST** `/api/v1/users/{userId}`

Creates a new user with a default balance of 100 EUR.

Example:

```bash
curl -X POST "http://localhost:8080/api/v1/users/user123"
```

### 6. Get User Details

**GET** `/api/v1/users/{userId}`

Example:

```bash
curl "http://localhost:8080/api/v1/users/user123"
```

## 🏗️ Architecture

### Design Patterns

- **Hexagonal Architecture**: Domain-driven design with clear separation between domain, application, and infrastructure
  layers
- **Repository Pattern**: Abstract data access layer
- **Use Case Pattern**: Business logic encapsulated in use cases
- **Adapter Pattern**: External API integration abstracted through adapters

### Technology Stack

- **Spring Boot 3.x** - Application framework
- **Spring WebFlux** - Reactive web framework
- **R2DBC** - Reactive database connectivity
- **H2 Database** - In-memory database (development)
- **Project Reactor** - Reactive streams implementation
- **Lombok** - Boilerplate code reduction

### Project Structure

```
src/main/java/com/mateja/f1betting/
├── adapter/
│   ├── web/rest/       # Web Rest
│   │   ├── controller/ # Controllers
│   │   ├── dto/        # Dtos
│   │   ├── exception/  # Exception Handler
│   │   └── mapper/     # Dto-Domain mappers
│   ├── persistence/    # Database repositories
│   │   ├── entity/     # Database entities
│   │   ├── mapper/     # Entity-Domain mappers
│   │   └── r2dbc/      # R2DBC implementations
│   └── external/       # External API adapters
├── domain/ 
│   ├── model/          # Domain models
│   ├── repository/     # Repository interfaces
│   ├── service/        # Business services
│   ├── usecase/        # Use case interfaces
│   ├── integration/    # Integration interfaces
│   └── exception/      # Domain exceptions
└── config/             # Configuration classes
```

## 🔧 Configuration

### Application Properties

```yaml
# Database Configuration
spring:
  r2dbc:
    url: r2dbc:h2:mem:///testdb
    username: sa
    password:

# External API Configuration
f1:
  api:
    base-url: https://api.openf1.org/v1

# Server Configuration
server:
  port: 8080
```

## 🎯 Key Features

### Transaction Management

- Reactive transactions using `TransactionalOperator`
- Proper rollback on failures
- Optimistic concurrency control

### Concurrency Handling

- Database constraints ensure data consistency
- Event outcome processing is idempotent

### Error Handling

- Custom domain exceptions
- Proper error responses with meaningful messages
- Transaction rollback on failures

## 🧪 Testing

Run tests with:

```bash
./gradlew test
```

Run integration tests:

```bash
./gradlew integrationTest
```

Generate test coverage report:

```bash
./gradlew jacocoTestReport
```

## 📝 Business Rules

1. **User Registration**: Each user starts with 100 EUR balance
2. **Betting**:
    - Users can bet on any past F1 event (for simplicity)
    - Bets cannot be placed once an event outcome is being processed
    - Insufficient balance prevents bet placement
3. **Odds**: Randomly assigned between 2, 3, or 4 for each driver
4. **Payout Calculation**: `bet_amount * odds` for winning bets
5. **Event Outcome**: Can only be processed once per event

## 🚨 Important Notes

### Database Schema

The application uses H2 in-memory database with the following tables:

- `users` - User accounts and balances
- `bets` - Bet records
- `event_outcomes` - Race results

### Concurrency Strategy

- Primary key constraints prevent duplicate event outcomes and users
- Transactional operations ensure consistency

### External Dependencies

- Integrates with OpenF1 API for event and driver data
- Designed to be easily extensible for additional F1 data providers

## 🔐 Security Considerations

**Note**: This is a demonstration project. For production use, implement:

- Authentication & Authorization
- API rate limiting
- Input validation and sanitization
- Secure communication (HTTPS)
- Audit logging

## 📊 Monitoring

Recommended monitoring points:

- Transaction success/failure rates
- API response times
- Database connection pool metrics
- External API call performance

## 👨‍💻 Author

Mateja Ašanin

---

**Focus Areas**:

- Clean architecture with proper separation of concerns
- Reactive programming patterns
- Transaction management
- Concurrency handling without blocking