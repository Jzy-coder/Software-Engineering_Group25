# Software-Engineering_Group25

## Project Introduction
This project is a personal financial management system, aiming to help users manage their personal income and expenditure ledgers efficiently. It enables the import, classification, statistics, and visualization of bills, thereby improving the efficiency of financial management.

## 主要功能
- User registration and login (supports remembering passwords)
- Bill import, supporting CSV import and manual entry
- Bill classification management, supporting multiple classification methods
- Bill statistics and visualization, allowing users to view income, expenditure, and surplus statistics within custom time periods, as well as statistical comparisons across different time periods
- Bill management, supporting the addition, modification, and deletion of bills
- Financial goals, supporting the addition, modification, and deletion of goals, as well as viewing the difference from the daily goal
- Account management, supporting the addition and modification of accounts
- Password management, supporting password modification

## Data Structure
- User information management
- Income and expenditure data management
- Classification statistics and data visualization
- Data security and local storage

## 运行环境
- JDK 17 and above
- JavaFX 17
- Maven 3.6+

## 快速开始
1. Clone this project:
   ```
   git clone <https://github.com/Jzy-coder/Software-Engineering_Group25>
   ```
2. Enter the project directory and build it using Maven:
   ```
   cd "Personal Finance Tracker"
   mvn clean javafx:run
   ```
3. After starting, register a new user or log in with an existing account.

## Directory Structure
```
├── Personal Finance Tracker
│   ├── src
│   │   └── main
│   │       ├── java
│   │       └── resources
│   │           └── fxml
│   ├── pom.xml
│   └── ...
├── UserInfo
├── data
├── README.md
```

## Contribution Instructions
You are welcome to submit issues and pull requests. It is recommended to fork the repository before starting development.

## Contact Information
If you have any questions, please contact the project leader or leave a message in the issue section.