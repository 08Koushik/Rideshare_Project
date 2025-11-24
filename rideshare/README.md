# Rideshare Application

A comprehensive rideshare platform built with Spring Boot and modern web technologies.

## 🚀 Features

- **User Management**: Passenger and Driver registration with role-based access
- **Ride Booking**: Search, post, and book rides with real-time notifications
- **Payment Integration**: Secure payment processing for completed rides
- **Admin Dashboard**: User management, onboarding, and system monitoring
- **Email Notifications**: Automated notifications for booking confirmations and ride updates
- **Code Quality**: Integrated with SonarQube for continuous code quality monitoring

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.5.6
- **Database**: MySQL
- **Security**: Spring Security with role-based authentication
- **Build Tool**: Maven
- **Testing**: JUnit, Mockito, JaCoCo for coverage
- **Code Quality**: SonarQube
- **Frontend**: HTML, CSS, JavaScript

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0+
- Maven 3.6+
- Git

## ⚙️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/08Koushik/Rideshare_Project.git
cd Rideshare_Project/rideshare
```

### 2. Configure Database
Create a MySQL database and update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Configure Email (Optional)
For email notifications, update the following in `application.properties`:

```properties
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### 4. Build the Application
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn clean verify

# View coverage report
open target/site/jacoco/index.html
```

## 📊 SonarQube Analysis

### Prerequisites
- SonarQube Server running on `http://localhost:9000`
- Default credentials: admin/admin

### Run Analysis
```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=Rideshare_Project \
  -Dsonar.projectName='Rideshare_Project' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=your_generated_token
```

## 🔑 Default User Roles

The application supports three user roles:
- **Admin**: System administration and user management
- **Driver**: Post rides, view ride requests, manage bookings
- **Passenger**: Search and book rides, view booking history

## 📁 Project Structure

```
rideshare/
├── src/
│   ├── main/
│   │   ├── java/com/rideshare/
│   │   │   ├── config/          # Security & configuration
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Data repositories
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       ├── static/          # Frontend files
│   │       └── application.properties
│   └── test/                    # Unit tests
├── pom.xml
└── README.md
```

## 🌐 API Endpoints

### User Management
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - User login
- `GET /api/users` - List all users (Admin)

### Rides
- `POST /api/rides` - Post a new ride (Driver)
- `GET /api/rides/search` - Search available rides
- `GET /api/rides/{id}` - Get ride details

### Bookings
- `POST /api/bookings` - Book a ride
- `PUT /api/bookings/{id}/status` - Update booking status
- `GET /api/bookings/user/{userId}` - Get user bookings

### Payments
- `POST /api/payments` - Process payment
- `GET /api/payments/booking/{bookingId}` - Get payment details

## 🔧 Configuration Files

- `pom.xml` - Maven dependencies and build configuration
- `application.properties` - Application configuration
- `sonar-project.properties` - SonarQube configuration
- `.github/workflows/sonarqube.yml` - CI/CD pipeline

## 🐛 Troubleshooting

### Database Connection Issues
- Ensure MySQL is running
- Verify database credentials in `application.properties`
- Check if the database exists

### Email Not Sending
- Verify SMTP settings
- Use App Password for Gmail (not regular password)
- Check firewall settings

### Build Failures
- Ensure Java 17+ is installed: `java -version`
- Clear Maven cache: `mvn clean`
- Update dependencies: `mvn clean install -U`

## 📚 Reference Documentation

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [Maven Documentation](https://maven.apache.org/guides/index.html)
- [SonarQube Documentation](https://docs.sonarqube.org/latest/)

## 👥 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is open source and available under the MIT License.

## 📧 Contact

For questions or support, please open an issue in the GitHub repository.

---

**Note**: This is a development version. Update security configurations and credentials before deploying to production.
