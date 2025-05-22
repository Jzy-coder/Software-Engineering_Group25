# Personal Finance Tracker

## Project Overview
A comprehensive personal finance management application built with JavaFX, helping users track income, expenses, budgets, and analyze their financial status.

## Core Features

### 1. User Authentication
- Secure login/registration system
- Encrypted password storage
- Remember password function
- Multi-user support with data isolation

### 2. Income & Expense Management
- Record daily transactions
- Transaction category management
- Multiple transaction type support
- CSV file import functionality
- Date range filtering
- Transaction history (with edit/delete support)
- Real-time balance calculation

### 3. Budget Management
- Create and manage budget goals
- Visual progress tracking
- Budget vs. actual spending comparison
- Budget balance calculation
- Budget item editing/deletion

### 4. Financial Analysis
- Income/Expense distribution pie charts
- Period analysis
- Category statistics details
- Interactive data visualization
- Custom date range selection

### 5. Income/Expense Portfolio
- Income/Expense tracking
- Multiple view types (single/comparative)
- Trend analysis
- Period comparison
- Chart visualization
  
### 6.Personal Financial AI Assistant
- AI chat interface
- Personal choice of different kinds AI styles
- Generating personal financial plans
- Clear the chat interface and start a new one
  
### 7. Dashboard
- Welcome screen with daily summary
- Today's transaction overview
- Current balance display
- Quick access to all features

### 8.Setting Interface

-  Change of password and username
- Change of user’s region
- Change of user’s gender
- Change of user’s occupation
- Switch an account to login
- Log out function

## Technical Details

### Technologies Used
- **Java 17** (Programming Language)
- **JavaFX 17** (UI Framework)
- **Maven** (Project Management and Build)
- **SQLite/JSON** (Data Storage)
- **CSS** (UI Styling)
- **JFreeChart** (Chart Display)
- **OpenCSV** (CSV Operations)
- **SLF4J & Logback** (Logging)
- **GSON** (JSON Processing)
- **Git/GitHub** (Version Control and Code Management)
- **JUnit 5** (Unit Testing Framework)

### Project Structure
---
 Personal Finance Tracker/
 ├── .settings/
 ├── UserInfo/
 ├── src/
 │   ├── main/
 │   │   ├── java/
 │   │   │   └── com.finance/
 │   │   │       ├── app/          # Application entry
 │   │   │       ├── component/    # Date range selector
 │   │   │       ├── controller/   # MVC controllers
 │   │   │       ├── model/       # Data models
 │   │   │       ├── service/     # Business logic
 │   │   │       ├── dao/         # Data access
 │   │   │       ├── event/       # Transaction events handling
 │   │   │       ├── result/      # Import results
 │   │   │       ├── view/        # UI elements
 │   │   │       ├── util/        # Utility classes
 │   │   │       └── gui/         # GUI components
 │   │   └── resources/
 │   │       ├── fxml/         # JavaFX layout files
 │   │       ├── css/          # Stylesheets
 │   │       └── config/       # Configuration files
 │   └── test/
 │       └── java/
 ├── target/
 │   ├── classes/
 │   │   ├── com/
 │   │   ├── config/
 │   │   ├── css/
 │   │   └── fxml/
 │   ├── lib/
 │   └── test-classes/
 │       └── com/
 ├── .classpath
 ├── .project
 ├── dependency-reduced-pom.xml
 ├── pom.xml
 └── settings.xml
---

### Build and Run
```bash
# Build project
mvn clean package

# Run application
java -jar target/finance-manager-1.0-SNAPSHOT.jar
```

## System Requirements
- Java 17 or higher
- Supports Windows/Linux/MacOS
- Recommended minimum 4GB RAM
- Screen resolution: 1024x768 or higher

## Configuration Details
Project Information:
- Group ID: com.finance
- Artifact ID: finance-manager
- Version: 4.0

Build Configuration:
- Java Version: 17 (specified by maven.compiler.release property in pom.xml )
- Source Encoding: UTF-8
- Maven Compiler Plugin Version: 3.10.1
- JavaFX Maven Plugin Version: 0.0.8
- Main Class: com.finance.app.Launcher
- Maven Shade Plugin Version: 3.4.1 (used for creating an executable JAR with dependencies)

Dependencies:
- org.openjfx:javafx-controls (Version 17)
- org.openjfx:javafx-fxml (Version 17)
- org.openjfx:javafx-graphics (Version 17)
- com.toedter:jcalendar (Version 1.4)
- org.junit.jupiter:junit-jupiter-api (Version 5.10.0, test scope)
- org.junit.jupiter:junit-jupiter-params (Version 5.10.0, test scope)
- org.mockito:mockito-core (Version 5.10.0, test scope)
- org.mockito:mockito-junit-jupiter (Version 5.10.0, test scope)
  
Maven Settings:
- Local Repository: D:/apache-maven-3.9.6/repository
- Mirror: Aliyun Maven repository ( https://maven.aliyun.com/repository/public ) for central repository.
- Profile: jdk-11 profile is active by default for JDK 11, setting maven.compiler.source , maven.compiler.target , and maven.compiler.compilerVersion to 11. (Note: This profile seems to conflict with the project's requirement for Java 17 as specified in pom.xml ).

## Data Storage
- Transaction data: Separate JSON files per user
- User credentials: Encrypted storage
- Budget data: Serialized objects
- Application settings: Property files

## Security Features
- Password hashing
- User data isolation
- Session management
- Secure file operations

## Key Features
1. Simple and intuitive user interface
2. Real-time data updates and calculations
3. Multi-dimensional financial analysis
4. Flexible data import/export
5. Secure data storage
6. Complete error handling
7. Multiple currency support
8. Responsive layout design

## Development Notes
1. Maven dependency management
2. MVC architecture pattern
3. Modular design
4. Comprehensive logging
5. Unified exception handling
6. Well-documented source code
7. Unit test coverage

## Deployment Instructions
1. Ensure Java 17 is installed
2. Download release package
3. Extract to target directory
4. Run startup script
5. First run will automatically create necessary configuration files and data directories

## User Guide
1. Start by registering new user or logging in
2. Access all core features from main interface
3. Use income/expense management to record daily transactions
4. Set financial goals in budget management
5. Use analysis tools to view financial status
6. Get financial advice with an AI financial assistant
7. Regularly backup important data

## Future Plans
1. Mobile support
2. Cloud synchronization
3. Multi-language support
4. More data analysis tools
5. Custom report generation
6. API integration support
7. Different languages for different regions
8. UI improvement

## FAQ
1. How to backup data
2. Password reset process
3. Data import format requirements
4. System performance optimization tips
5. Security measures

## Acknowledgments
- JavaFX community
- Maven Central Repository
- Open source contributors

## Change Log
### Version 4.0.0 (2025-05-26)
- Core functionality implementation
- Basic interface design
- Data storage functionality
- Chart implementation
- AI chat function
