# ATM System

A web-based ATM (Automated Teller Machine) system built with Java Servlets, JSP, and PostgreSQL. This system provides essential banking operations including deposits, withdrawals, transfers, and account management.

## Features

### User Features
- Account balance inquiry
- Cash withdrawal
- Cash deposit
- Fund transfer between accounts
- PIN change
- Transaction history view
- Daily transaction limits

### Admin Features
- Create new user accounts
- View all registered users
- Generate transaction reports
- Monitor system activities

## Prerequisites

- JDK 17 or higher
- Apache Maven 3.8+
- Apache Tomcat 10
- PostgreSQL 14+
- IDE (recommended: IntelliJ IDEA or Eclipse)

## Configuration

1. Update database configuration in `src/main/resources/database.properties`:
   ```properties
   db.url=your_database_url
   db.username=your_username
   db.password=your_password
   ```

2. Configure Tomcat server settings in `src/main/webapp/WEB-INF/web.xml`

## Building the Project

```bash
# Clone the repository
git clone https://github.com/yourusername/ATM-System.git

# Navigate to project directory
cd ATM-System

# Build the project
mvn clean install
```

## Running the Application

1. Deploy the WAR file to Tomcat:
   - Copy `target/ATM-System.war` to Tomcat's `webapps` directory
   - Start Tomcat server

2. Access the application:
   - URL: `http://localhost:8080/ATM-System`
   - Default admin credentials:
     - Username: admin
     - Password: admin123

## Project Structure

```
ATM-System/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/atm/
│   │   │       ├── controller/
│   │   │       ├── dao/
│   │   │       ├── dto/
│   │   │       ├── exception/
│   │   │       ├── model/
│   │   │       ├── service/
│   │   │       └── util/
│   │   ├── resources/
│   │   └── webapp/
│   └── test/
├── pom.xml
└── README.md
```

## Business Rules

- Daily transaction limit: $25,000
- Maximum 5 transactions per day per type
- PIN must be 4 digits
- Card number must be 10 digits
- Transfer requires valid recipient card number
- Withdrawal amount cannot exceed current balance

## Security Features

- Session-based authentication
- Input validation
- SQL injection prevention
- Transaction rollback support
- Error logging

## API Endpoints

### User APIs
- POST `user/auth` - User authentication
- GET `/user/balance` - Get account balance
- POST `/user/transaction` - Process transaction
- GET `/user/history` - Get transaction history
- POST `/user/changePin` - Allow user to change password

### Admin APIs
- POST `admin/auth` - User authentication
- GET `/admin/report` - Generate reports
- GET `/admin/dashboard` - Admin dashboard

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify
```

## Known Issues

- Session timeout not configurable through UI
- Limited browser compatibility testing

## Future Enhancements

- Mobile responsive design
- Bill payment feature
- Enhanced security features
- Enhanced UI/UX

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For support or queries, please contact:
- Email: nngochaial@gmail.com

## Acknowledgments

- Servlet API Documentation
- PostgreSQL Documentation
- Apache Tomcat Documentation
