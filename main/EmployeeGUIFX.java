import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.lang.reflect.Field;
import java.util.List;

import javafx.scene.text.Font;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class EmployeeGUIFX extends Application {
    private EmployeeService service;
    private TextArea outputArea;

    @Override
    public void start(Stage primaryStage) {
        // load custom font
        Font.loadFont(new File("main/fonts/PixelfySans.ttf").toURI().toString(), 12);

        // init service
        try {
            MySQLEmployeeRepository repo = new MySQLEmployeeRepository();
            service = new EmployeeService(repo);
        } catch (Exception e) {
            showError("Could not connect to database: " + e.getMessage());
            return;
        }

        primaryStage.setTitle("Employee Management System");
        BorderPane root = new BorderPane();

        // left button column
        VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new Insets(10));

        Button addBtn         = new Button("Add Employee");
        Button searchBtn      = new Button("Search Employee");
        Button updateBtn      = new Button("Update Employee Data");
        Button salaryRaiseBtn = new Button("Raise Salary by Range");
        Button totalPayBtn    = new Button("Total Pay by Job Title");
        Button viewAllBtn     = new Button("View All Employees");
        Button logPayBtn      = new Button("Log Monthly Pay");

        // optional style classes
        addBtn.getStyleClass().add("btn-pink");
        searchBtn.getStyleClass().add("btn-green");
        updateBtn.getStyleClass().add("btn-yellow");
        salaryRaiseBtn.getStyleClass().add("btn-blue");
        totalPayBtn.getStyleClass().add("btn-purple");
        viewAllBtn.getStyleClass().add("btn-peach");
        logPayBtn.getStyleClass().add("btn-mint");

        // hook up actions
        addBtn.setOnAction(e -> addEmployee());
        searchBtn.setOnAction(e -> searchEmployee());
        updateBtn.setOnAction(e -> updateEmployeeData());
        salaryRaiseBtn.setOnAction(e -> raiseSalaryByRange());
        totalPayBtn.setOnAction(e -> showTotalPay());
        viewAllBtn.setOnAction(e -> viewAllEmployees());
        logPayBtn.setOnAction(e -> logMonthlyPay());

        buttonBox.getChildren().addAll(
            addBtn, searchBtn, updateBtn,
            salaryRaiseBtn, totalPayBtn,
            viewAllBtn, logPayBtn
        );

        // center text area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefSize(800, 600);

        root.setLeft(buttonBox);
        root.setCenter(new ScrollPane(outputArea));

        Scene scene = new Scene(root, 800, 500);

        // print to confirm the stylesheet loads
        System.out.println(new File("main/style.css").toURI().toString());

        // load your CSS
        scene.getStylesheets().add(
          new File("main/style.css").toURI().toString()
        );

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addEmployee() {
        try {
            int id = promptForInt("Enter Employee ID:");
            if (id == -1) return;

            List<Employee> existing = service.searchEmployee(String.valueOf(id));
            if (!existing.isEmpty()) {
                if (!showConfirmation("An employee with ID " + id + " already exists. Overwrite?")) {
                    outputArea.appendText("Add canceled.\n");
                    return;
                }
            }

            String name      = promptForText("Enter first name:");
            if (name == null) return;
            
            String lastName  = promptForText("Enter last name:");
            if (lastName == null) return;

            String ssn;
            while (true) {
                ssn = promptForText("Enter SSN (9 digits, no dashes):");
                if (ssn == null) return;
                if (isValidSSN(ssn)) break;
                showError("SSN must be exactly 9 digits.");
            }

            double salary    = promptForDouble("Enter salary:");
            if (salary < 0) return;
            String jobTitle  = promptForText("Enter job title:");
            if (jobTitle == null) return;
            String division  = promptForText("Enter division:");
            if (division == null) return;

            LocalDate hireDate;
            while (true) {
                String ds = promptForText("Enter hire date (YYYY-MM-DD):");
                if (ds == null) return;
                try {
                    hireDate = LocalDate.parse(ds);
                    break;
                } catch (DateTimeParseException ex) {
                    showError("Invalid date format. Use YYYY-MM-DD.");
                }
            }

            Employee e = new Employee(
                id, name, lastName, ssn, salary,
                jobTitle, division, hireDate
            );
            // reflectively save
            Field f = service.getClass().getDeclaredField("repository");
            f.setAccessible(true);
            ((EmployeeRepository)f.get(service)).save(e);

            outputArea.appendText("Added/Updated: " + formatEmployee(e) + "\n");
        } catch (Exception ex) {
            showError("Add error: " + ex.getMessage());
        }
    }

    private void viewAllEmployees() {
        try {
            Field f = service.getClass().getDeclaredField("repository");
            f.setAccessible(true);
            EmployeeRepository repo = (EmployeeRepository)f.get(service);

            outputArea.appendText("All Employees:\n");
            for (Employee e : repo.findAll()) {
                outputArea.appendText(formatEmployee(e) + "\n");
            }
        } catch (Exception ex) {
            showError("View error: " + ex.getMessage());
        }
    }

    private void searchEmployee() {
        String q = promptForText("Search by name/ID/SSN:");
        if (q == null) return;
        outputArea.appendText("Search Results:\n");
        for (Employee e : service.searchEmployee(q)) {
            outputArea.appendText(formatEmployee(e) + "\n");
        }
    }

    private void raiseSalaryByRange() {
        try {
            double min = promptForDouble("Min. Salary:");
            if (min < 0) return;
            double max = promptForDouble("Max. Salary:");
            if (max < 0) return;
            double pct = promptForDouble("Increase %:");
            if (pct < 0) return;
            service.updateEmployeeSalaryRange(min, max, pct);
            outputArea.appendText(
              "Raised salaries in range ["+min+"-"+max+"] by "+pct+"%\n"
            );
        } catch (Exception ex) {
            showError("Raise error: " + ex.getMessage());
        }
    }

    private void showTotalPay() {
        String title = promptForText("Enter a job title to calculate total pay:");
        if (title == null) return;
        double total = service.totalPayByJobTitle(title);
        outputArea.appendText(
          "Total pay for \""+title+"\": "+total+"\n"
        );
    }

    private void logMonthlyPay() {
        try {
            int id = promptForInt("Employee ID to log monthly pay:");
            if (id == -1) return;
            boolean ok = service.logMonthlyPay(id);
            if (ok) outputArea.appendText(
                "Logged monthly pay for ID "+id+"\n"
            );
            else     showError("Log failed or no such employee.");
        } catch (Exception ex) {
            showError("Log error: " + ex.getMessage());
        }
    }

    private void updateEmployeeData() {
        try {
            int id = promptForInt("Enter Employee ID to update:");
            if (id == -1) return;

            List<Employee> list = service.searchEmployee(String.valueOf(id));
            if (list.isEmpty()) {
                outputArea.appendText("Employee not found.\n");
                return;
            }
            Employee e = list.get(0);

            // first name
            if (showConfirmation("Update First Name? Current: " + e.getName())) {
                String nf;
                while (true) {
                    nf = promptForText("New First Name:");
                    if (nf == null) return;
                    if (nf.trim().isEmpty()) showError("Cannot be empty.");
                    else { e.setName(nf.trim()); break; }
                }
            }

            // last name
            if (showConfirmation("Update Last Name? Current: " + e.getLastName())) {
                String nl;
                while (true) {
                    nl = promptForText("New Last Name:");
                    if (nl == null) return;
                    if (nl.trim().isEmpty()) showError("Cannot be empty.");
                    else { e.setLastName(nl.trim()); break; }
                }
            }

            // SSN
            if (showConfirmation("Update SSN? Current: " + formatSSN(e.getSsn()))) {
                String ns;
                while (true) {
                    ns = promptForText("New SSN (9 digits):");
                    if (ns == null) return;
                    if (isValidSSN(ns)) { e.setSsn(ns); break; }
                    showError("Must be 9 digits.");
                }
            }

            // salary
            if (showConfirmation("Update Salary? Current: $" + String.format("%.2f", e.getSalary()))) {
                double nsal;
                while ((nsal = promptForDouble("New Salary:")) < 0) {
                    showError("Invalid number.");
                }
                e.setSalary(nsal);
            }

            // job title
            if (showConfirmation("Update Job Title? Current: " + e.getJobTitle())) {
                String nj;
                while (true) {
                    nj = promptForText("New Job Title:");
                    if (nj == null) return;
                    if (nj.trim().isEmpty()) showError("Cannot be empty.");
                    else { e.setJobTitle(nj.trim()); break; }
                }
            }

            // division
            if (showConfirmation("Update Division? Current: " + e.getDivision())) {
                String nd;
                while (true) {
                    nd = promptForText("New Division:");
                    if (nd == null) return;
                    if (nd.trim().isEmpty()) showError("Cannot be empty.");
                    else { e.setDivision(nd.trim()); break; }
                }
            }

            // hire date
            if (showConfirmation("Update Hire Date? Current: " + e.getHireDate())) {
                LocalDate nd;
                while (true) {
                    String ds = promptForText("New Hire Date (YYYY-MM-DD):");
                    if (ds == null) return;
                    try {
                        nd = LocalDate.parse(ds);
                        e.setHireDate(nd);
                        break;
                    } catch (DateTimeParseException ex) {
                        showError("Invalid date.");
                    }
                }
            }

            // finally save
            Field f = service.getClass().getDeclaredField("repository");
            f.setAccessible(true);
            ((EmployeeRepository)f.get(service)).save(e);
            outputArea.appendText("Employee updated: " + formatEmployee(e) + "\n");
        } catch (Exception ex) {
            showError("Update error: " + ex.getMessage());
        }
    }

    // helpers

    private String formatEmployee(Employee e) {
        return String.format(
          "%d – %s %s, $%.2f, %s, %s, Hired: %s",
          e.getEmpId(), e.getName(), e.getLastName(),
          e.getSalary(), e.getJobTitle(),
          e.getDivision(), e.getHireDate()
        );
    }

    private String formatSSN(String ssn) {
        return ssn.length() == 9
          ? ssn.substring(0,3) + "-" + ssn.substring(3,5) + "-" + ssn.substring(5)
          : ssn;
    }

    private boolean isValidSSN(String ssn) {
        return ssn != null && ssn.matches("\\d{9}");
    }

    private String promptForText(String prompt) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Input Required");
        d.setHeaderText(null);
        d.setContentText(prompt);
        return d.showAndWait().orElse(null);
    }

    private int promptForInt(String prompt) {
        String s = promptForText(prompt);
        if (s == null) return -1;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            showError("Enter a valid integer.");
            return -1;
        }
    }

    private double promptForDouble(String prompt) {
        String s = promptForText(prompt);
        if (s == null) return -1;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            showError("Enter a valid number.");
            return -1;
        }
    }

    private boolean showConfirmation(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        return a.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Error");
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
