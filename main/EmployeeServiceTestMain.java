
public class EmployeeServiceTestMain {
    public static void main(String[] args) {
        MockEmployeeRepository mockRepo = new MockEmployeeRepository();
        EmployeeService service = new EmployeeService(mockRepo);

        // Add test employees
        mockRepo.save(new Employee(1, "Alice", "123456789", 60000, "Engineer", "IT"));
        mockRepo.save(new Employee(2, "Bob", "987654321", 50000, "Engineer", "IT"));
        mockRepo.save(new Employee(3, "Charlie", "555223333", 120000, "Manager", "Admin"));

        System.out.println("=== Test: Search Employee ===");
        var searchResults = service.searchEmployee("Alice");
        assert searchResults.size() == 1 : "Expected 1 result for 'Alice'";

        System.out.println("=== Test: Update Employee Data ===");
        boolean updated = service.updateEmployeeData(1, "Alicia", "111112222");
        assert updated : "Update should succeed";
        assert mockRepo.findByEmpId(1).get().getName().equals("Alicia") : "Name not updated";

        System.out.println("=== Test: Salary Range Update ===");
        service.updateEmployeeSalaryRange(50000, 100000, 10);
        double updatedSalary = mockRepo.findByEmpId(2).get().getSalary();
        assert updatedSalary == 55000 : "Expected salary to increase by 10%";

        System.out.println("=== Test: Total Pay by Job Title ===");
        double totalPay = service.totalPayByJobTitle("Engineer");
        assert Math.abs(totalPay - (66000 + 55000)) < 0.01 : "Incorrect total pay calculation";

        System.out.println("All tests passed!");
    }
}
