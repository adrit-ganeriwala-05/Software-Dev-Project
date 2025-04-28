import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class EmployeeServiceTest {

    // Exactly one public no-arg constructor:
    public EmployeeServiceTest() { }

    private MockEmployeeRepository mockRepo;
    private EmployeeService service;
    private Employee alice, bob, charlie;

    @Before
    public void setUp() {
        mockRepo = new MockEmployeeRepository();
        service = new EmployeeService(mockRepo);
        LocalDate hire = LocalDate.of(2020, 1, 1);

        alice   = new Employee(1, "Alice",   "Smith",  "123456789", 60000, "Engineer", "IT",   hire);
        bob     = new Employee(2, "Bob",     "Jones",  "987654321", 50000, "Engineer", "IT",   hire);
        charlie = new Employee(3, "Charlie", "Brown",  "555223333",120000, "Manager",  "Admin",hire);

        mockRepo.save(alice);
        mockRepo.save(bob);
        mockRepo.save(charlie);
    }

    @Test
    public void searchEmployee_matchesMultipleFields() {
        // by first name
        List<Employee> first = service.searchEmployee("Alice");
        assertEquals(1, first.size());

        // by last name
        List<Employee> last = service.searchEmployee("Jones");
        assertEquals(1, last.get(0).getEmpId());

        // by SSN
        List<Employee> ssn = service.searchEmployee("555223333");
        assertEquals(1, ssn.get(0).getName());
    }

    @Test
    public void updateEmployeeData_successAndFailure() {
        // For a successful update
        boolean ok = service.updateEmployeeData(1, "Alicia", "111112222");
        assertTrue(ok);
        Employee updated = mockRepo.findByEmpId(1).get();
        assertEquals("Alicia", updated.getName());
        assertEquals("111112222", updated.getSsn());

        // Fails when ID not found
        assertFalse(service.updateEmployeeData(99, "X", "000000000"));
    }

    @Test
    public void updateEmployeeSalaryRange_appliesOnlyWithinBounds() {
        // Bob has salary in range [50000,100000)
        service.updateEmployeeSalaryRange(50000, 100000, 10);
        assertEquals(55000, mockRepo.findByEmpId(2).get().getSalary(), 0.001);
        // Alice updated (60000) → 66000
        assertEquals(66000, mockRepo.findByEmpId(1).get().getSalary(), 0.001);
        // Charlie is (120000) unchanged
        assertEquals(120000, mockRepo.findByEmpId(3).get().getSalary(), 0.001);
    }

    @Test
    public void totalPayByJobTitle_caseInsensitive() {
        // After no raises
        double total = service.totalPayByJobTitle("engineer");
        assertEquals(110000, total, 0.001);
    }

    @Test
    public void calculateMonthlyPay_andLogMonthlyPay() {
        // Monthly pay calculation
        double month = service.calculateMonthlyPay(3);
        assertEquals(120000.0/12.0, month, 0.001);

        // Log on mock repo
        assertTrue(service.logMonthlyPay(3));
        List<Double> stmts = mockRepo.getPayStatements(3);
        assertEquals(1, stmts.size());
        assertEquals(month, stmts.get(0), 0.001);

        // For a non‐existent ID returns false & negative monthly pay
        assertFalse(service.logMonthlyPay(99));
        assertEquals(-1, service.calculateMonthlyPay(99), 0.001);
    }
}
