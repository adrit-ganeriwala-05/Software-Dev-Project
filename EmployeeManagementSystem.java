
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based UI for the Employee Management System.
 */
public class EmployeeManagementSystem {
    public static void main(String[] args) {
        // Initialize repository and service
        MySQLEmployeeRepository repo = new MySQLEmployeeRepository();
        EmployeeService service = new EmployeeService(repo);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\nEmployee Management System");
                System.out.println("1. Add Employee");
                System.out.println("2. Search Employee");
                System.out.println("3. Update Employee Data");
                System.out.println("4. Update Salaries by Percentage");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");

                String choiceLine = scanner.nextLine();
                int choice;
                try {
                    choice = Integer.parseInt(choiceLine);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid option, please enter a number.");
                    continue;
                }

                switch (choice) {
                    case 1:
                        addEmployee(scanner, service);
                        break;
                    case 2:
                        searchEmployee(scanner, service);
                        break;
                    case 3:
                        updateEmployeeData(scanner, service);
                        break;
                    case 4:
                        updateSalaries(scanner, service);
                        break;
                    case 5:
                        System.out.println("Exiting... Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }

    /*
     * Prompts user to input all fields for a new employee and saves it.
     * Validates SSN and date format.
     */
    private static void addEmployee(Scanner scanner, EmployeeService service) {
        try {
            System.out.print("Enter Employee ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            // Check for existing
            List<Employee> existing = service.searchEmployee(String.valueOf(id));
            if (!existing.isEmpty()) {
                System.out.print("Employee exists. Overwrite? (y/n): ");
                if (!scanner.nextLine().equalsIgnoreCase("y")) {
                    System.out.println("Canceled.");
                    return;
                }
            }

            System.out.print("Enter First Name: ");
            String firstName = scanner.nextLine().trim();
            System.out.print("Enter Last Name: ");
            String lastName = scanner.nextLine().trim();

            String ssn;
            while (true) {
                System.out.print("Enter SSN (9 digits, no dashes): ");
                ssn = scanner.nextLine().trim();
                if (ssn.matches("\\d{9}")) break;
                System.out.println("Invalid SSN. Must be exactly 9 digits.");
            }

            System.out.print("Enter Job Title: ");
            String jobTitle = scanner.nextLine().trim();
            System.out.print("Enter Division: ");
            String division = scanner.nextLine().trim();

            double salary;
            while (true) {
                System.out.print("Enter Salary: ");
                try {
                    salary = Double.parseDouble(scanner.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid salary. Enter a number.");
                }
            }

            LocalDate hireDate;
            while (true) {
                System.out.print("Enter Hire Date (YYYY-MM-DD): ");
                try {
                    hireDate = LocalDate.parse(scanner.nextLine());
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format.");
                }
            }

            Employee emp = new Employee(id, firstName, lastName, ssn, salary, jobTitle, division, hireDate);
            // Save through service
            service.repository.save(emp);
            System.out.println("Employee added/updated: " + firstName + " " + lastName);
        } catch (Exception e) {
            System.out.println("Error adding employee: " + e.getMessage());
        }
    }

    /**
     * Prompts for a search term and displays matching employees.
     */
    private static void searchEmployee(Scanner scanner, EmployeeService service) {
        System.out.print("Enter name, ID or SSN to search: ");
        String term = scanner.nextLine().trim();
        List<Employee> results = service.searchEmployee(term);
        if (results.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            System.out.println("Search Results:");
            for (Employee e : results) {
                System.out.println(formatEmployee(e));
            }
        }
    }

    /**
     * Prompts to update selected fields for an existing employee and saves changes.
     */
    private static void updateEmployeeData(Scanner scanner, EmployeeService service) {
        System.out.print("Enter Employee ID to update: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }
        List<Employee> list = service.searchEmployee(String.valueOf(id));
        if (list.isEmpty()) {
            System.out.println("Employee not found.");
            return;
        }
        Employee e = list.get(0);
        System.out.println("Updating " + formatEmployee(e));

        System.out.print("New First Name (blank to keep): ");
        String firstName = scanner.nextLine().trim();
        if (!firstName.isEmpty()) e.setName(firstName);

        System.out.print("New Last Name (blank to keep): ");
        String lastName = scanner.nextLine().trim();
        if (!lastName.isEmpty()) e.setLastName(lastName);

        System.out.print("New SSN (9 digits, blank to keep): ");
        String ssn = scanner.nextLine().trim();
        if (!ssn.isEmpty() && ssn.matches("\\d{9}")) e.setSsn(ssn);

        System.out.print("New Job Title (blank to keep): ");
        String jobTitle = scanner.nextLine().trim();
        if (!jobTitle.isEmpty()) e.setJobTitle(jobTitle);

        System.out.print("New Division (blank to keep): ");
        String division = scanner.nextLine().trim();
        if (!division.isEmpty()) e.setDivision(division);

        System.out.print("New Salary (blank to keep): ");
        String salInput = scanner.nextLine().trim();
        if (!salInput.isEmpty()) {
            try { e.setSalary(Double.parseDouble(salInput)); }
            catch (NumberFormatException ex) { System.out.println("Invalid salary, skipped."); }
        }

        System.out.print("New Hire Date (YYYY-MM-DD, blank to keep): ");
        String dateInput = scanner.nextLine().trim();
        if (!dateInput.isEmpty()) {
            try { e.setHireDate(LocalDate.parse(dateInput)); }
            catch (DateTimeParseException ex) { System.out.println("Invalid date, skipped."); }
        }

        // Persist changes
        service.repository.save(e);
        System.out.println("Employee updated.");
    }

    /**
     * Prompts for salary range and percentage, then applies increase.
     */
    private static void updateSalaries(Scanner scanner, EmployeeService service) {
        try {
            System.out.print("Minimum Salary: ");
            double min = Double.parseDouble(scanner.nextLine());
            System.out.print("Maximum Salary: ");
            double max = Double.parseDouble(scanner.nextLine());
            System.out.print("Increase Percentage: ");
            double pct = Double.parseDouble(scanner.nextLine());
            service.updateEmployeeSalaryRange(min, max, pct);
            System.out.println("Salaries updated.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric input.");
        }
    }

    /** Utility to format an employee for console display. */
    private static String formatEmployee(Employee e) {
        return String.format("%d - %s %s, %s, $%.2f, %s, Hired: %s",
            e.getEmpId(), e.getName(), e.getLastName(), e.getSsn(),
            e.getSalary(), e.getJobTitle(), e.getHireDate());
    }
}
