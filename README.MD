# User Demo Application

## Project Structure
Below is a breakdown of the directory structure:

```
/Users/ismail/cedar/user-demo/src/main/kotlin/org/example/userdemo
├── UserDemoApplication.kt
├── auditlog
|   ├── annotations
|   |   └── Audit.kt
|   ├── model
|   |   └── AuditLog.kt
|   ├── repositories
|   |   └── AuditLogDAO.kt
|   └── service
|       └── AuditLogService.kt
├── shared
|   ├── config
|   |   ├── JpaConfig.kt
|   |   └── ModelMapperConfig.kt
|   ├── dao
|   |   └── AbstractDAO.kt
|   ├── exception
|   |   └── GlobalExceptionHandler.kt
|   ├── interfaces
|   |   └── GenericDao.kt
|   ├── listeners
|   |   └── AuditEntityListener.kt
|   └── response
|       └── ApiResponse.kt
└── user
    ├── controller
    |   └── UserController.kt
    ├── dtos
    |   ├── CreateUserDTO.kt
    |   ├── ReadUserDTO.kt
    |   └── UpdateUserDTO.kt
    ├── enums
    |   └── UserStatus.kt
    ├── exceptions
    |   └── UserAlreadyExistsException.kt
    ├── helpers
    |   └── UserMapper.kt
    ├── jobs
    |   └── UpdateUserStatusJob.kt
    ├── model
    |   └── User.kt
    ├── repositories
    |   └── UserDAO.kt
    ├── service
    |   └── UserService.kt
    └── specifications
        └── UserSpecifications.kt
```

## Modules Description

### Main Application
- **UserDemoApplication.kt**: The entry point for the Spring Boot application.

### Audit Log
- **annotations/Audit.kt**: Custom annotation to mark methods or classes for auditing.
- **model/AuditLog.kt**: Data model for audit logs.
- **repositories/AuditLogDAO.kt**: Data Access Object for `AuditLog`.
- **service/AuditLogService.kt**: Service layer for managing audit logs.

### Shared Components
- **config/JpaConfig.kt**: JPA configuration.
- **config/ModelMapperConfig.kt**: Model mapper configuration for object mapping.
- **dao/AbstractDAO.kt**: Base DAO implementation.
- **exception/GlobalExceptionHandler.kt**: Global exception handler to manage application exceptions.
- **interfaces/GenericDao.kt**: Generic Data Access Object interface.
- **listeners/AuditEntityListener.kt**: Entity listener for audit operations.
- **response/ApiResponse.kt**: Standard API response structure.

### User Management
- **controller/UserController.kt**: REST controller for user-related operations.
- **dtos**: Data Transfer Objects for user creation, reading, and updating.
    - `CreateUserDTO.kt`
    - `ReadUserDTO.kt`
    - `UpdateUserDTO.kt`
- **enums/UserStatus.kt**: Enum for user status management.
- **exceptions/UserAlreadyExistsException.kt**: Custom exception for handling existing user scenarios.
- **helpers/UserMapper.kt**: Helper to map between DTOs and entity models.
- **jobs/UpdateUserStatusJob.kt**: Scheduled job to update user statuses.
- **model/User.kt**: User entity model.
- **repositories/UserDAO.kt**: Data Access Object for `User`.
- **service/UserService.kt**: Service layer for user operations.
- **specifications/UserSpecifications.kt**: Specification for querying `User` entities.

## Querying with Dynamic Specifications
The `UserSpecifications` class allows for dynamic querying of users. Filter parameters can be provided to create complex queries on the User entity.

### Supported Operations
The dynamic query builder supports various operations, which are determined by splitting the filter key into an attribute and its operation. Below is a table explaining the supported operations:

| Operation | Description                          | Example Filter Key |
|-----------|--------------------------------------|--------------------|
| `gt`      | Greater than                         | `balance_gt`           |
| `lt`      | Less than                            | `balance_lt`           |
| `gte`     | Greater than or equal to             | `balance_gte`          |
| `lte`     | Less than or equal to                | `balance_lte`          |
| `eq`      | Equals                               | `firstName_eq`      |
| `neq`     | Not equals                           | `firstName_neq`     |
| `like`    | Like (used for partial text matching)| `firstName_like`    |

Unsupported operations or incorrectly formatted keys will result in an `IllegalArgumentException`.



