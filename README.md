# Library Management System (Early Stage)

**Status: Work in Progress - Early Development**

This project is a basic library management system built with Spring Boot. It is currently under development and only implements a minimal set of features.

## Features (Implemented)
### User management (`UserController` & `UserService`):
- Add users
- Delete users
- View all users / individual user info
- Manage user credit
### Authentication & Authorization:
- JWT-based login system (`LoginController` & `LoginService`)
- Role-based access control (`LIBRARIAN`, `READER`)
- Security configuration (`SecurityConfig`)
- Password hashing with BCrypt (`PasswordEncoder`)
### JWT token management:
- Token generation (`JwtTokenService`)
- Token verification filter (`JWTTokenFilter`)
### Exception handling:
- Custom exceptions for different `entities` and `operations`
- Global Exception Handler/Controller

## Notes
This is just the beginning, and it will be gradually expanded with more features and improvements.
