import java.util.Scanner;

public class EmployeeManagementSystem {
    public static void main(String[] args) {
        EmployeeService employeeService = new EmployeeService();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\nEmployee Management System");
                System.out.println("1. Add Employee");
                System.out.println("2. Search Employee");
                System.out.println("3. Update Employee Data");
                System.out.println("4. Update Salaries by Percentage");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        System.out.print("Enter Employee ID: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine();

                        System.out.print("Enter SSN (no dashes): ");
                        String ssn = scanner.nextLine();

                        System.out.print("Enter Division: ");
                        String division = scanner.nextLine();

                        System.out.print("Enter Job Title: ");
                        String jobTitle = scanner.nextLine();

                        System.out.print("Enter Salary: ");
                        double salary = scanner.nextDouble();

                        employeeService.addEmployee(new Employee(id, name, ssn, division, jobTitle, salary));
                        break;
                    case 2:
                        System.out.println("\nSearch Employee by: 1. ID, 2. Name, 3. SSN");
                        System.out.print("Choose an option: ");
                        int option = scanner.nextInt();
                        scanner.nextLine();

                        switch (option) {
                            case 1:
                                System.out.print("Enter Employee ID: ");
                                int searchId = scanner.nextInt();
                                employeeService.searchEmployeeById(searchId);
                                break;
                            case 2:
                                System.out.print("Enter Name: ");
                                String searchName = scanner.nextLine();
                                employeeService.searchEmployeeByName(searchName);
                                break;
                            case 3:
                                System.out.print("Enter SSN: ");
                                String searchSsn = scanner.nextLine();
                                employeeService.searchEmployeeBySsn(searchSsn);
                                break;
                            default:
                                System.out.println("Invalid option.");
                        }
                        break;
                    case 3:
                        System.out.print("Enter Employee ID to update: ");
                        int updateId = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Enter New Name (leave blank to keep unchanged): ");
                        String updateName = scanner.nextLine();
                        updateName = updateName.isEmpty() ? null : updateName;

                        System.out.print("Enter New Division (leave blank to keep unchanged): ");
                        String updateDivision = scanner.nextLine();
                        updateDivision = updateDivision.isEmpty() ? null : updateDivision;

                        System.out.print("Enter New Job Title (leave blank to keep unchanged): ");
                        String updateJobTitle = scanner.nextLine();
                        updateJobTitle = updateJobTitle.isEmpty() ? null : updateJobTitle;

                        System.out.print("Enter New Salary (leave blank to keep unchanged): ");
                        String salaryInput = scanner.nextLine();
                        Double updateSalary = salaryInput.isEmpty() ? null : Double.parseDouble(salaryInput);

                        if (employeeService.updateEmployee(updateId, updateName, updateDivision, updateJobTitle, updateSalary)) {
                            System.out.println("Employee updated successfully!");
                        } else {
                            System.out.println("Employee not found.");
                        }
                        break;
                    case 4:
                        System.out.print("Enter minimum salary: ");
                        double minSalary = scanner.nextDouble();

                        System.out.print("Enter maximum salary: ");
                        double maxSalary = scanner.nextDouble();

                        System.out.print("Enter percentage increase: ");
                        double percentage = scanner.nextDouble();

                        employeeService.updateSalaries(minSalary, maxSalary, percentage);
                        break;
                    case 5:
                        System.out.println("Exiting... Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (NumberFormatException e) {
    
            e.printStackTrace();
        }
    }
}
