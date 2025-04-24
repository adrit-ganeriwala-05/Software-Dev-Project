import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javafx.scene.text.Font;

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

    public void start(Stage primaryStage) {
        Font.loadFont(new File("main/fonts/PixelfySans.ttf").toURI().toString(), 12);

        try {
            MySQLEmployeeRepository repo = new MySQLEmployeeRepository();
            service = new EmployeeService(repo);
        } catch (Exception e) {
            showError("Could not connect to database: " + e.getMessage());
            return;
        }

        primaryStage.setTitle("Employee Management System");
        BorderPane root = new BorderPane();

        VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new Insets(10));

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(600);
        outputArea.setPrefWidth(800);

        ScrollPane scrollPane = new ScrollPane(outputArea);

        Button addBtn = new Button("Add Employee");
        Button searchBtn = new Button("Search Employee");
        Button updateBtn = new Button("Update Employee Data");
        Button salaryRaiseBtn = new Button("Raise Salary by Range");
        Button totalPayBtn = new Button("Total Pay by Job Title");
        Button viewAllBtn = new Button("View All Employees");
        Button logPayBtn = new Button("Log Monthly Pay");

        // 🔥 Assign style classes
        addBtn.getStyleClass().add("btn-pink");
        searchBtn.getStyleClass().add("btn-green");
        updateBtn.getStyleClass().add("btn-yellow");
        salaryRaiseBtn.getStyleClass().add("btn-blue");
        totalPayBtn.getStyleClass().add("btn-purple");
        viewAllBtn.getStyleClass().add("btn-peach");
        logPayBtn.getStyleClass().add("btn-mint");

        addBtn.setOnAction(e -> addEmployee());
        searchBtn.setOnAction(e -> searchEmployee());
        updateBtn.setOnAction(e -> updateEmployeeData());
        salaryRaiseBtn.setOnAction(e -> raiseSalaryByRange());
        totalPayBtn.setOnAction(e -> showTotalPay());
        viewAllBtn.setOnAction(e -> viewAllEmployees());
        logPayBtn.setOnAction(e -> logMonthlyPay());

        buttonBox.getChildren().addAll(addBtn, searchBtn, updateBtn, salaryRaiseBtn, totalPayBtn, viewAllBtn, logPayBtn);

        root.setLeft(buttonBox);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 800, 500);

        // Debug, print to confirm the stylesheet loads
        System.out.println(new File("main/style.css").toURI().toString());

        scene.getStylesheets().add(new File("main/style.css").toURI().toString());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

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

            String name = promptForText("Enter First Name:");
            if (name == null) return;

            String lastName = promptForText("Enter Last Name:");
            if (lastName == null) return;

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

            LocalDate hireDate;
            while (true) {
                String dateStr = promptForText("Enter Hire Date (YYYY-MM-DD):");
                if (dateStr == null) return;
                try {
                    hireDate = LocalDate.parse(dateStr);
                    break;
                } catch (DateTimeParseException ex) {
                    showError("Invalid date format. Use YYYY-MM-DD.");
                }
            }

            Employee e = new Employee(id, name, lastName, ssn, salary, jobTitle, division, hireDate);
            Field repoField = service.getClass().getDeclaredField("repository");
            repoField.setAccessible(true);
            EmployeeRepository repo = (EmployeeRepository) repoField.get(service);
            repo.save(e);

            outputArea.appendText("Employee " + name + " " + lastName + " added/updated.\n");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    private void viewAllEmployees() {
        try {
            Field repoField = service.getClass().getDeclaredField("repository");
            repoField.setAccessible(true);
            EmployeeRepository repo = (EmployeeRepository) repoField.get(service);

            List<Employee> all = repo.findAll();
            outputArea.appendText("All Employees:\n");
            for (Employee e : all) {
                outputArea.appendText(formatEmployee(e) + "\n");
            }
        } catch (Exception ex) {
            showError("Unable to access repository: " + ex.getMessage());
        }
    }

    private void searchEmployee() {
        String query = promptForText("Enter name, ID or SSN to search:");
        if (query == null) return;
        List<Employee> results = service.searchEmployee(query);
        outputArea.appendText("Search Results:\n");
        for (Employee e : results) {
            outputArea.appendText(formatEmployee(e) + "\n");
        }
    }

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

    private void showTotalPay() {
        String title = promptForText("Enter Job Title:");
        if (title == null) return;
        double total = service.totalPayByJobTitle(title);
        outputArea.appendText("Total pay for " + title + ": " + total + "\n");
    }

    private void logMonthlyPay() {
        try {
            int empId = promptForInt("Enter Employee ID to log monthly pay:");
            if (empId == -1) return;

            boolean success = service.logMonthlyPay(empId);
            if (success) {
                outputArea.appendText("Logged monthly pay for Employee ID " + empId + "\n");
            } else {
                showError("Employee not found or failed to log pay.");
            }

        } catch (Exception ex) {
            showError("Error logging pay: " + ex.getMessage());
        }
    }

    private boolean isValidSSN(String ssn) {
        return ssn != null && ssn.matches("\\d{9}");
    }

    private String formatEmployee(Employee e) {
        String formattedSalary = String.format("%.2f", e.getSalary());
        return e.getEmpId() + " - " + e.getName() + " " + e.getLastName() + ", $" + formattedSalary + ", " +
               e.getJobTitle() + " - " + e.getDivision() + " | Hired: " + e.getHireDate();
    }

    private String promptForText(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Required");
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        return dialog.showAndWait().orElse(null);
    }

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

    private boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(btn -> btn == ButtonType.OK).isPresent();
    }

    private void updateEmployeeData() {
        outputArea.appendText("⚠️ Update feature not yet implemented for FX UI.\n");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
