import java.awt.*;
import java.util.List;
import javax.swing.*;

public class EmployeeGUI extends JFrame {
    private final EmployeeService service;
    private JTextArea outputArea;

    public EmployeeGUI(EmployeeService service) {
        this.service = service;
        initUI();
    }

    // Initializes the GUI for the Employee Management System
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

        addBtn.setFont(customFont);
        searchBtn.setFont(customFont);
        updateBtn.setFont(customFont);
        salaryRaiseBtn.setFont(customFont);
        totalPayBtn.setFont(customFont);
        viewAllBtn.setFont(customFont);
        
        // Event listeners for buttons
        addBtn.addActionListener(e -> addEmployee());
        searchBtn.addActionListener(e -> searchEmployee());
        updateBtn.addActionListener(e -> updateEmployeeData());
        salaryRaiseBtn.addActionListener(e -> raiseSalaryByRange());
        totalPayBtn.addActionListener(e -> showTotalPay());
        viewAllBtn.addActionListener(e -> viewAllEmployees());

        // Button panel
        buttonPanel.add(addBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(salaryRaiseBtn);
        buttonPanel.add(totalPayBtn);
        buttonPanel.add(viewAllBtn);

        add(buttonPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
    }

    /* 
    * Method to view all employees in the repository.
    * Uses reflection to access the private repository field from the EmployeeService.
    */
    private void viewAllEmployees() {
        try {
            var repoField = service.getClass().getDeclaredField("repository");
            repoField.setAccessible(true);
            MockEmployeeRepository repo = (MockEmployeeRepository) repoField.get(service);
    
            List<Employee> all = repo.findAll();
            outputArea.append("All Employees:\n");
            for (Employee e : all) {
                outputArea.append(formatEmployee(e) + "\n");
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            showError("Unable to access repository: " + ex.getMessage());
        }
    }
    
    
    /* 
    * Method to add a new employee to the repository.
    * Prompts the user for ID, name, SSN, salary, job title, and division.
    * Validates input and allows retry on error.
    */
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
                name = JOptionPane.showInputDialog("Enter Name:");
                if (name == null) return;
            } while (name.trim().isEmpty());
    
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
    
            Employee e = new Employee(id, name, ssn, salary, jobTitle, division);
            // Save to repository
            ((MockEmployeeRepository) service.getClass().getDeclaredField("repository").get(service)).save(e);
    
            outputArea.append("Added employee: " + name + "\n");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }
    

    /* 
    * Checks if the SSN is valid (must be 9 digits, no dashes).
    * Returns true if valid, false otherwise.
    */
    private boolean isValidSSN(String ssn) {
        return ssn != null && ssn.matches("\\d{9}");
    }

    /* 
    * Method to search for employees based on name, ID, or SSN.
    * Prompts the user for a query and displays any matching employees.
    */
    private void searchEmployee() {
        String query = JOptionPane.showInputDialog("Enter name, ID or SSN to search:");
        List<Employee> results = service.searchEmployee(query);
        outputArea.append("Search Results:\n");
        for (Employee e : results) {
            outputArea.append(formatEmployee(e) + "\n");
        }
    }

    /**
     * Method to update an employee's data.
     * User is prompted to update name and SSN, with the option to skip each field.
     */
    private void updateEmployeeData() {
        try {
            
            String idStr = JOptionPane.showInputDialog("Enter Employee ID to update:");
            if (idStr == null) return; // User hits cancel
            int id = Integer.parseInt(idStr);

            List<Employee> results = service.searchEmployee(String.valueOf(id));

            if (results.isEmpty()) {
                outputArea.append("Employee not found.\n");
                return;
            }

            Employee e = results.get(0);

            // Name update
            int nameChoice = JOptionPane.showOptionDialog(
                this,
                "Do you want to update the name? Current: " + e.getName(),
                "Update Name",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Update", "Skip"},
                "Skip"
            );

            if (nameChoice == JOptionPane.YES_OPTION) {
                String newName = JOptionPane.showInputDialog("Enter New Name:");
                if (newName != null && !newName.trim().isEmpty()) {
                    e.setName(newName.trim());
                }
            }

            // SSN update
            int ssnChoice = JOptionPane.showOptionDialog(
                this,
                "Do you want to update the SSN? Current: " + formatSSN(e.getSsn()),
                "Update SSN",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Update", "Skip"},
                "Skip"
            );

            if (ssnChoice == JOptionPane.YES_OPTION) {
                while (true) {
                    String newSsn = JOptionPane.showInputDialog("Enter New SSN (9 digits, no dashes):");
                    if (newSsn == null || newSsn.trim().isEmpty()) break; // user canceled or left it blank
                    if (isValidSSN(newSsn)) {
                        e.setSsn(newSsn.trim());
                        break;
                    } else {
                        showError("Invalid SSN — must be exactly 9 digits.");
                    }
                }
            }

            // Save updated employee
            ((MockEmployeeRepository) service.getClass().getDeclaredField("repository").get(service)).save(e);
            outputArea.append("✅ Employee updated.\n");

        } catch (NumberFormatException ex) {
            showError("Invalid employee ID.");
        } catch (Exception ex) {
            showError("Error updating employee: " + ex.getMessage());
        }
    }


    /* 
    * Method to raise salaries for employees within a specified salary range.
    * Prompts for min salary, max salary, and percentage increase.
    */
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
    

    /* 
    * Calculates and displays total pay for all employees with a given job title.
    * Prompts the user for the job title and displays the total salary.
    */
    private void showTotalPay() {
        String title = JOptionPane.showInputDialog("Enter Job Title:");
        double total = service.totalPayByJobTitle(title);
        outputArea.append("Total pay for " + title + ": " + total + "\n");
    }

    // Shows error dialog when necessary
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Formats employee info for display
    private String formatEmployee(Employee e) {
        String formattedSalary = String.format("%.2f", e.getSalary());
        return e.getEmpId() + " - " + e.getName() + ", $" + formattedSalary + ", " +
               e.getJobTitle() + " - " + e.getDivision();
    }
    
    // Formats SSN to contain dashes for display
    private String formatSSN(String ssn) {
        return ssn.length() == 9
            ? ssn.substring(0, 3) + "-" + ssn.substring(3, 5) + "-" + ssn.substring(5)
            : ssn;
    }

    // Main
    // Uses mock repo for now
    public static void main(String[] args) {
        MockEmployeeRepository mockRepo = new MockEmployeeRepository();
        EmployeeService service = new EmployeeService(mockRepo);

        // Add some sample employees
        mockRepo.save(new Employee(1, "Alice", "123456789", 60000, "Engineer", "IT"));
        mockRepo.save(new Employee(2, "Bob", "987654321", 50000, "Engineer", "IT"));
        mockRepo.save(new Employee(3, "Charlie", "555223333", 120000, "Manager", "Admin"));

        SwingUtilities.invokeLater(() -> new EmployeeGUI(service).setVisible(true));
    }
}
