import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;

public class EmployeeGUI extends JFrame {
    private final EmployeeService service;
    private JTextArea outputArea;

    public EmployeeGUI(EmployeeService service) {
        this.service = service;
        initUI();
    }

    private void initUI() {
        setTitle("Employee Management System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Font customFont = new Font("Courier New", Font.PLAIN, 14);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(customFont);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));

        JButton addBtn = new JButton("Add Employee");
        JButton searchBtn = new JButton("Search Employee");
        JButton updateBtn = new JButton("Update Employee Data");
        JButton salaryRaiseBtn = new JButton("Raise Salary by Range");
        JButton totalPayBtn = new JButton("Total Pay by Job Title");
        JButton viewAllBtn = new JButton("View All Employees");
        JButton logPayBtn = new JButton("Log Monthly Pay");

        addBtn.setFont(customFont);
        searchBtn.setFont(customFont);
        updateBtn.setFont(customFont);
        salaryRaiseBtn.setFont(customFont);
        totalPayBtn.setFont(customFont);
        viewAllBtn.setFont(customFont);
        logPayBtn.setFont(customFont);

        addBtn.addActionListener(e -> addEmployee());
        searchBtn.addActionListener(e -> searchEmployee());
        updateBtn.addActionListener(e -> updateEmployeeData());
        salaryRaiseBtn.addActionListener(e -> raiseSalaryByRange());
        totalPayBtn.addActionListener(e -> showTotalPay());
        viewAllBtn.addActionListener(e -> viewAllEmployees());
        logPayBtn.addActionListener(e -> logMonthlyPay());

        buttonPanel.add(addBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(salaryRaiseBtn);
        buttonPanel.add(totalPayBtn);
        buttonPanel.add(viewAllBtn);
        buttonPanel.add(logPayBtn);

        add(buttonPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void viewAllEmployees() {
        try {
            var repoField = service.getClass().getDeclaredField("repository");
            repoField.setAccessible(true);
            EmployeeRepository repo = (EmployeeRepository) repoField.get(service);

            List<Employee> all = repo.findAll();
            outputArea.append("All Employees:\n");
            for (Employee e : all) {
                outputArea.append(formatEmployee(e) + "\n");
            }
        } catch (Exception ex) {
            showError("Unable to access repository: " + ex.getMessage());
        }
    }

    private void addEmployee() {
        try {
            int id;
            while (true) {
                String idStr = JOptionPane.showInputDialog("Enter Employee ID:");
                if (idStr == null) return;
                try {
                    id = Integer.parseInt(idStr);
                    break;
                } catch (NumberFormatException ex) {
                    showError("Please enter a valid integer for ID.");
                }
            }

            String name;
            do {
                name = JOptionPane.showInputDialog("Enter First Name:");
                if (name == null) return;
            } while (name.trim().isEmpty());

            String lastName;
            do {
                lastName = JOptionPane.showInputDialog("Enter Last Name:");
                if (lastName == null) return;
            } while (lastName.trim().isEmpty());

            String ssn;
            while (true) {
                ssn = JOptionPane.showInputDialog("Enter SSN (9 digits, no dashes):");
                if (ssn == null) return;
                if (isValidSSN(ssn)) break;
                showError("SSN must be exactly 9 digits (no dashes).");
            }

            double salary;
            while (true) {
                String salStr = JOptionPane.showInputDialog("Enter Salary:");
                if (salStr == null) return;
                try {
                    salary = Double.parseDouble(salStr);
                    break;
                } catch (NumberFormatException ex) {
                    showError("Please enter a valid number for salary.");
                }
            }

            String jobTitle;
            do {
                jobTitle = JOptionPane.showInputDialog("Enter Job Title:");
                if (jobTitle == null) return;
            } while (jobTitle.trim().isEmpty());

            String division;
            do {
                division = JOptionPane.showInputDialog("Enter Division:");
                if (division == null) return;
            } while (division.trim().isEmpty());

            LocalDate hireDate;
            while (true) {
                String dateStr = JOptionPane.showInputDialog("Enter Hire Date (YYYY-MM-DD):");
                if (dateStr == null) return;
                try {
                    hireDate = LocalDate.parse(dateStr);
                    break;
                } catch (DateTimeParseException ex) {
                    showError("Invalid date format. Please use YYYY-MM-DD.");
                }
            }

            Employee e = new Employee(id, name, lastName, ssn, salary, jobTitle, division, hireDate);

            var repoField = service.getClass().getDeclaredField("repository");
            repoField.setAccessible(true);
            EmployeeRepository repo = (EmployeeRepository) repoField.get(service);
            repo.save(e);

            outputArea.append("Added employee: " + name + " " + lastName + "\n");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    private void logMonthlyPay() {
        try {
            String idStr = JOptionPane.showInputDialog("Enter Employee ID:");
            if (idStr == null) return;
            int empId = Integer.parseInt(idStr);
    
            boolean success = service.logMonthlyPay(empId);
            if (success) {
                outputArea.append("✅ Logged monthly pay for Employee ID " + empId + "\n");
            } else {
                showError("Employee not found or failed to log pay.");
            }
    
        } catch (NumberFormatException e) {
            showError("Invalid ID format.");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }
    

    private boolean isValidSSN(String ssn) {
        return ssn != null && ssn.matches("\\d{9}");
    }

    private void searchEmployee() {
        String query = JOptionPane.showInputDialog("Enter name, ID or SSN to search:");
        List<Employee> results = service.searchEmployee(query);
        outputArea.append("Search Results:\n");
        for (Employee e : results) {
            outputArea.append(formatEmployee(e) + "\n");
        }
    }

    private void updateEmployeeData() {
        outputArea.append("⚠️ Update feature has not been modified to support new fields yet.\n");
    }

    private void raiseSalaryByRange() {
        try {
            String minStr = JOptionPane.showInputDialog("Minimum Salary:");
            if (minStr == null) return;

            String maxStr = JOptionPane.showInputDialog("Maximum Salary:");
            if (maxStr == null) return;

            String percentStr = JOptionPane.showInputDialog("Increase Percentage:");
            if (percentStr == null) return;

            double min = Double.parseDouble(minStr);
            double max = Double.parseDouble(maxStr);
            double percent = Double.parseDouble(percentStr);

            service.updateEmployeeSalaryRange(min, max, percent);
            outputArea.append("Salaries updated for range " + min + " - " + max + "\n");

        } catch (NumberFormatException ex) {
            showError("Please enter valid numeric values.");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    private void showTotalPay() {
        String title = JOptionPane.showInputDialog("Enter Job Title:");
        double total = service.totalPayByJobTitle(title);
        outputArea.append("Total pay for " + title + ": " + total + "\n");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String formatEmployee(Employee e) {
        String formattedSalary = String.format("%.2f", e.getSalary());
        return e.getEmpId() + " - " + e.getName() + " " + e.getLastName() + ", $" + formattedSalary + ", " +
               e.getJobTitle() + " - " + e.getDivision() + " | Hired: " + e.getHireDate();
    }

    private String formatSSN(String ssn) {
        return ssn.length() == 9
            ? ssn.substring(0, 3) + "-" + ssn.substring(3, 5) + "-" + ssn.substring(5)
            : ssn;
    }

    public static void main(String[] args) {
        try {
            MySQLEmployeeRepository repo = new MySQLEmployeeRepository();
            EmployeeService service = new EmployeeService(repo);
            SwingUtilities.invokeLater(() -> new EmployeeGUI(service).setVisible(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
