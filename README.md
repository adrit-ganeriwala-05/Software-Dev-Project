# Employee Management System (JavaFX)

This is a simple Employee Management System built using JavaFX. It allows users to:

- Add new employees
- Search employees by name, ID, or SSN
- Update employee information (with optional field skipping)
- Raise salary for a range of employees
- View total pay by job title
- View all current employees in the repository

## Prerequisites

- Java JDK 17 or later
- JavaFX SDK 17.0.14 (download from [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/))

> Ensure JavaFX SDK is extracted to a known location, e.g., `~/Desktop/javafx-sdk-17.0.14`.

## Folder Structure

SDD Project/ 
    ├── bin/ # Compiled class files \\
    ├── main/ # Java source files │ \\
        ├── EmployeeGUIFX.java │    \\
        ├── Employee.java │         \\
        ├── EmployeeService.java │  \\
        ├── MockEmployeeRepository.java │ \\
        ├── EmployeeRepository.java │ \\
        └── style.css               \\


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

Run the app with:

```bash
java \
--module-path ~/Desktop/javafx-sdk-17.0.14/lib \
--add-modules javafx.controls,javafx.graphics \
-cp bin \
main.EmployeeGUIFX
```