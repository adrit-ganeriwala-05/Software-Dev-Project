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
├── bin/                            # Compiled `.class` files
├── lib/                            # Third-party JARs
│   └── mysql-connector-java.jar    # MySQL JDBC driver
├── main/                           # Java source files & resources
│   ├── Employee.java
│   ├── EmployeeRepository.java
│   ├── EmployeeService.java
│   ├── MockEmployeeRepository.java
│   ├── MySQLEmployeeRepository.java
│   ├── EmployeeGUI.java
│   ├── EmployeeGUIFX.java
│   ├── EmployeeServiceTestMain.java    # Service unit tests
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
 java \
 --module-path ~/Desktop/javafx-sdk-17.0.14/lib \
 --add-modules javafx.controls,javafx.graphics \
 -cp bin \
 main.EmployeeGUIFX

```