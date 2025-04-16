package main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.List;

public class EmployeeGUIFX extends Application {
    private EmployeeService service;
    private TextArea outputArea;

    /**
     * Initializes the primary stage and GUI layout with mock data.
     */
    @Override
    public void start(Stage primaryStage) {
        // Setup mock data
        MockEmployeeRepository repo = new MockEmployeeRepository();
        service = new EmployeeService(repo);
        repo.save(new Employee(1, "Alice", "123456789", 60000, "Engineer", "IT"));
        repo.save(new Employee(2, "Bob", "987654321", 50000, "Engineer", "IT"));
        repo.save(new Employee(3, "Charlie", "555223333", 120000, "Manager", "Admin"));

        // Layout setup
        primaryStage.setTitle("Employee Management System (JavaFX)");
        BorderPane root = new BorderPane();

        VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new Insets(10));

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(600);
        outputArea.setPrefWidth(800);
        outputArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");

        ScrollPane scrollPane = new ScrollPane(outputArea);

        Button addBtn = new Button("Add Employee");
        Button searchBtn = new Button("Search Employee");
        Button updateBtn = new Button("Update Employee Data");
        Button salaryRaiseBtn = new Button("Raise Salary by Range");
        Button totalPayBtn = new Button("Total Pay by Job Title");
        Button viewAllBtn = new Button("View All Employees");

        addBtn.setOnAction(e -> addEmployee());
        searchBtn.setOnAction(e -> searchEmployee());
        updateBtn.setOnAction(e -> updateEmployeeData());
        salaryRaiseBtn.setOnAction(e -> raiseSalaryByRange());
        totalPayBtn.setOnAction(e -> showTotalPay());
        viewAllBtn.setOnAction(e -> viewAllEmployees());

        buttonBox.getChildren().addAll(addBtn, searchBtn, updateBtn, salaryRaiseBtn, totalPayBtn, viewAllBtn);

        root.setLeft(buttonBox);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(new java.io.File("main/style.css").toURI().toString());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Displays all employees from the repository in the output area.
     */
    private void viewAllEmployees() {
        try {
            Field repoField = service.getClass().getDeclaredField("repository");
            repoField.setAccessible(true);
            MockEmployeeRepository repo = (MockEmployeeRepository) repoField.get(service);

            List<Employee> all = repo.findAll();
            outputArea.appendText("All Employees:\n");
            for (Employee e : all) {
                outputArea.appendText(formatEmployee(e) + "\n");
            }
        } catch (Exception ex) {
            showError("Unable to access repository: " + ex.getMessage());
        }
    }

    /**
     * Formats SSN string into xxx-xx-xxxx format.
     */
    private String formatSSN(String ssn) {
        return ssn.length() == 9
            ? ssn.substring(0, 3) + "-" + ssn.substring(3, 5) + "-" + ssn.substring(5)
            : ssn;
    }


    /**
     * Prompts the user for all required employee fields and adds a new employee.
     * Handles overwrite confirmation if employee ID already exists.
     */
    private void addEmployee() {
        try {
            int id = promptForInt("Enter Employee ID:");
            if (id == -1) return;

            List<Employee> existing = service.searchEmployee(String.valueOf(id));
            if (!existing.isEmpty()) {
                boolean overwrite = showConfirmation("An employee with ID " + id + " already exists. Overwrite?");
                if (!overwrite) {
                    outputArea.appendText("Canceled.\n");
                    return;
                }
            }

            String name = promptForText("Enter Name:");
            if (name == null) return;

            String ssn;
            while (true) {
                ssn = promptForText("Enter SSN (9 digits, no dashes):");
                if (ssn == null) return;
                if (isValidSSN(ssn)) break;
                showError("SSN must be exactly 9 digits.");
            }

            double salary = promptForDouble("Enter Salary:");
            if (salary == -1) return;

            String jobTitle = promptForText("Enter Job Title:");
            if (jobTitle == null) return;

            String division = promptForText("Enter Division:");
            if (division == null) return;

            Employee e = new Employee(id, name, ssn, salary, jobTitle, division);
            Field repoField = service.getClass().getDeclaredField("repository");
            repoField.setAccessible(true);
            ((MockEmployeeRepository) repoField.get(service)).save(e);

            outputArea.appendText("Employee " + name + " added/updated.\n");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    /**
     * Prompts user for a search and displays matching employees.
     */
    private void searchEmployee() {
        String query = promptForText("Enter name, ID or SSN to search:");
        if (query == null) return;
        List<Employee> results = service.searchEmployee(query);
        outputArea.appendText("Search Results:\n");
        for (Employee e : results) {
            outputArea.appendText(formatEmployee(e) + "\n");
        }
    }

    /**
     * Prompts user for salary range and percentage, and updates employee salaries.
     */
    private void raiseSalaryByRange() {
        try {
            double min = promptForDouble("Minimum Salary:");
            if (min == -1) return;
            double max = promptForDouble("Maximum Salary:");
            if (max == -1) return;
            double percent = promptForDouble("Increase Percentage:");
            if (percent == -1) return;

            service.updateEmployeeSalaryRange(min, max, percent);
            outputArea.appendText("Salaries updated for range " + min + " - " + max + "\n");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    /**
     * Prompts user for a job title and displays total salary for that title.
     */
    private void showTotalPay() {
        String title = promptForText("Enter Job Title:");
        if (title == null) return;
        double total = service.totalPayByJobTitle(title);
        outputArea.appendText("Total pay for " + title + ": " + total + "\n");
    }

    /**
     * Validates if a given SSN is exactly 9 digits.
     */
    private boolean isValidSSN(String ssn) {
        return ssn != null && ssn.matches("\\d{9}");
    }

    /**
     * Formats an employee object into a readable string for display.
     */
    private String formatEmployee(Employee e) {
        String formattedSalary = String.format("%.2f", e.getSalary());
        return e.getEmpId() + " - " + e.getName() + ", $" + formattedSalary + ", " +
               e.getJobTitle() + " - " + e.getDivision();
    }

    /**
     * Shows an error alert with the given message.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Prompts the user with a text input dialog and returns their response.
     */
    private String promptForText(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Required");
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        return dialog.showAndWait().orElse(null);
    }

    /**
     * Prompts the user for an integer input, returns -1 if cancelled or invalid.
     */
    private int promptForInt(String prompt) {
        String input = promptForText(prompt);
        if (input == null) return -1;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            showError("Please enter a valid integer.");
            return -1;
        }
    }

    /**
     * Prompts the user for a double input, returns -1 if cancelled or invalid.
     */
    private double promptForDouble(String prompt) {
        String input = promptForText(prompt);
        if (input == null) return -1;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number.");
            return -1;
        }
    }

    /**
     * Shows a confirmation dialog and returns true if user confirms.
     */
    private boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(btn -> btn == ButtonType.OK).isPresent();
    }

    /**
     * Updates an employee's data with ability to skip fields. Prompts until valid input.
     */
    private void updateEmployeeData() {
        try {
            int id = promptForInt("Enter Employee ID to update:");
            if (id == -1) return;

            List<Employee> results = service.searchEmployee(String.valueOf(id));
            if (results.isEmpty()) {
                outputArea.appendText("Employee not found.\n");
                return;
            }

            Employee e = results.get(0);

            // Update name
            boolean updateName = showConfirmation("Update name? Current: " + e.getName());
            if (updateName) {
                while (true) {
                    String newName = promptForText("Enter New Name:");
                    if (newName == null || newName.trim().isEmpty()) {
                        showError("Name cannot be empty.");
                    } else {
                        e.setName(newName.trim());
                        break;
                    }
                }
            }

            // Update SSN
            boolean updateSSN = showConfirmation("Update SSN? Current: " + formatSSN(e.getSsn()));
            if (updateSSN) {
                while (true) {
                    String newSsn = promptForText("Enter New SSN (9 digits, no dashes):");
                    if (newSsn == null) return;
                    if (isValidSSN(newSsn)) {
                        e.setSsn(newSsn.trim());
                        break;
                    } else {
                        showError("Invalid SSN. Enter exactly 9 digits.");
                    }
                }
            }

            // Update Salary
            boolean updateSalary = showConfirmation("Update salary? Current: $" + String.format("%.2f", e.getSalary()));
            if (updateSalary) {
                while (true) {
                    String salaryStr = promptForText("Enter New Salary:");
                    try {
                        if (salaryStr == null) return;
                        double salary = Double.parseDouble(salaryStr);
                        e.setSalary(salary);
                        break;
                    } catch (NumberFormatException ex) {
                        showError("Please enter a valid number for salary.");
                    }
                }
            }

            // Update Job Title
            boolean updateTitle = showConfirmation("Update job title? Current: " + e.getJobTitle());
            if (updateTitle) {
                while (true) {
                    String title = promptForText("Enter New Job Title:");
                    if (title == null || title.trim().isEmpty()) {
                        showError("Job title cannot be empty.");
                    } else {
                        e.setJobTitle(title.trim());
                        break;
                    }
                }
            }

            // Update Division
            boolean updateDivision = showConfirmation("Update division? Current: " + e.getDivision());
            if (updateDivision) {
                while (true) {
                    String div = promptForText("Enter New Division:");
                    if (div == null || div.trim().isEmpty()) {
                        showError("Division cannot be empty.");
                    } else {
                        e.setDivision(div.trim());
                        break;
                    }
                }
            }

            Field repoField = service.getClass().getDeclaredField("repository");
            repoField.setAccessible(true);
            ((MockEmployeeRepository) repoField.get(service)).save(e);
            outputArea.appendText("Employee updated.\n");

        } catch (Exception ex) {
            showError("Error updating employee: " + ex.getMessage());
        }
    }

    /**
     * Launches the JavaFX application.
     */
    public static void main(String[] args) {
        launch();
    }
}
