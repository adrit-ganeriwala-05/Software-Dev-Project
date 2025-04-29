# Employee Management System (JavaFX)

Employee Management System with both console and JavaFX UIs.  

Features include:

- Add new employees  
- Search by name, ID or SSN  
- Update employee information (skip fields if desired)  
- Raise salary by a percentage for a specified salary range  
- View total pay by job title  
- Log monthly pay statements (to MySQL or in-memory mock)  

---

## Prerequisites

- **Java JDK 17** or later  
- **JavaFX SDK 17.0.14**  
  - Download from [Gluon](https://gluonhq.com/products/javafx/)  
  - Unzip to e.g. `~/Desktop/javafx-sdk-17.0.14`  
- **MySQL Server**  
  - Database: `employeeData`  
  - Table schemas: see [SQL scripts](db/schema.sql)  
- **MySQL Connector/J** (e.g. `mysql-connector-java-8.0.x.jar`)  

---

## Folder Structure

```text
SDD Project/
├── bin/                          # Compiled `.class` files
├── lib/                          # Third-party JARs
  ├── javafx-sdk-17.0.1           # JavaFX folder
  ├── hamcrest-core-1.3.jar
  ├──  junit-4.13.2.jar           # JUnit for testing
  └── mysql-connector-java.jar    # MySQL JDBC driver

├── main/                         # Java source files & resources
│   ├── Employee.java    
│   ├── EmployeeTest.java         # Test for Employee.java
│   ├── EmployeeGUIFX.java
│   ├── EmployeeRepository.java
│   ├── EmployeeService.java
│   ├── EmployeeServiceTest.java        # Test for EmployeeService.java
│   ├── EmployeeServiceTestMain.java    # Service unit tests
│   ├── MySQLEmployeeRepository.java
│   ├── MySQLEmployeeRepositoryTest.java  # Test for MySQLEmployeeRepository.java
│   ├── MockEmployeeRepository.java
│   └── style.css                       # JavaFX stylesheet
├── README.md
└── EmployeeManagementSystem.java   # Console UI
```
## Compilation Instructions
 
 Make sure you're in the `SDD Project` directory, and run the following in your terminal to compile:
 
 ```bash
 javac \
 --module-path ~/Desktop/javafx-sdk-17.0.14/lib \
 --add-modules javafx.controls,javafx.graphics \
 -d bin \
 main/*.java
 ```
 
 ## Run Instructions
 
 Run app with:
 
 ```bash
export PATH_TO_FX=~/Desktop/javafx-sdk-17.0.14/lib

java \
  --module-path "$PATH_TO_FX" \
  --add-modules javafx.controls,javafx.graphics \
  -cp "bin:lib/mysql-connector-j-9.3.0.jar" \
  EmployeeGUIFX

```
## Testing
Run tests with individual commands in terminal:
```bash
java -ea -cp bin:lib/mysql-connector-j-9.3.0.jar EmployeeTest
java -ea -cp bin EmployeeServiceTest
java -ea -cp bin:lib/mysql-connector-j-9.3.0.jar MySQLEmployeeRepositoryTest
```