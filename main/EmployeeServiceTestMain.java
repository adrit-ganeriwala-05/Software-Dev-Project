import java.time.LocalDate;
import java.util.List;

/**
 * Test for EmployeeService using MockEmployeeRepository
 */
public class EmployeeServiceTestMain {
    public static void main(String[] args) {
        MockEmployeeRepository mockRepo = new MockEmployeeRepository();
        EmployeeService service = new EmployeeService(mockRepo);

        // Use a fixed hire date for testing
        LocalDate hireDate = LocalDate.of(2020, 1, 1);

        // Add test employees with all required fields
        mockRepo.save(new Employee(1, "Alice", "Smith", "123456789", 60000, "Engineer", "IT", hireDate));
        mockRepo.save(new Employee(2, "Bob", "Jones", "987654321", 50000, "Engineer", "IT", hireDate));
        mockRepo.save(new Employee(3, "Charlie", "Brown", "555223333", 120000, "Manager", "Admin", hireDate));

        /* Search Employee
         * Logic: Search for "Alice" by first name
         * Expected result: Exactly one match for searched employee
         */
        System.out.println("~~ Test: Search Employee ~~");
        List<Employee> searchResults = service.searchEmployee("Alice");
        assert searchResults.size() == 1 : "Expected 1 result for 'Alice'";

        /* Update Employee Data
         * Logic: Change name and SSN of employee with ID 1
         * Expected: updateEmployeeData returns true, and repo reflects new name and SSN
         */
        System.out.println("~~ Test: Update Employee Data ~~");
        boolean updated = service.updateEmployeeData(1, "Alicia", "111112222");
        assert updated : "Update should succeed";
        Employee updatedEmp = mockRepo.findByEmpId(1).get();
        assert updatedEmp.getName().equals("Alicia") : "Name not updated";
        assert updatedEmp.getSsn().equals("111112222") : "SSN not updated";

        /*
         * Salary Range Update
         * Logic: Increase salaries by 10% for employees between 50,000 and 100,000
         * Expected: Bob's salary (50,000) increases to 55,000
         */
        System.out.println("~~ Test: Salary Range Update ~~");
        service.updateEmployeeSalaryRange(50000, 100000, 10);
        double newSalary = mockRepo.findByEmpId(2).get().getSalary();
        assert Math.abs(newSalary - 55000) < 0.01 : "Expected salary to increase by 10%";

        /*
         * Total Pay by Job Title
         * Logic: Sum salaries for all Engineers
         * Expected: Sum of updated salaries for Alicia and Bob
         */
        System.out.println("~~ Test: Total Pay by Job Title ~~");
        double totalPay = service.totalPayByJobTitle("Engineer");
        assert Math.abs(totalPay - (66000 + 55000)) < 0.01 : "Incorrect total pay calculation";

        /*
         * Calculate Monthly Pay
         * Logic: Retrieve Alicia salary from repo and calculate monthly amount
         * Expected: salary / 12
         */
        System.out.println("~~ Test: Calculate Monthly Pay ~~");
        double monthlyPay = service.calculateMonthlyPay(1);
        assert Math.abs(monthlyPay - (updatedEmp.getSalary() / 12.0)) < 0.01 : "Incorrect monthly pay calculation";

        /*
         * Log Monthly Pay on Mock Repo
         * Logic: Attempt to log monthly pay using mock repo (which does not support logging)
         * Expected: logMonthlyPay returns false
         */
        System.out.println("~~ Test: Log Monthly Pay on Mock Repo ~~");
        boolean logResult = service.logMonthlyPay(1);
        assert !logResult : "Mock repository should not log pay statements";

        System.out.println("All tests passed!");
    }
}
