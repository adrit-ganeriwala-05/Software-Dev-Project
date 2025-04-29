/* Test for SQL */

import java.time.LocalDate;
import java.util.*;

public class MySQLEmployeeRepositoryTest {
    public static void main(String[] args) {
        System.out.println("=== MySQLEmployeeRepositoryTest ===");
        MySQLEmployeeRepository repo = new MySQLEmployeeRepository();

        // Create a temporary ID
        int testId = 9999;
        LocalDate today = LocalDate.now();
        Employee e = new Employee(testId, "Test", "User", "123123123", 45000, "QA", "Ops", today);

        // save
        repo.save(e);

        // findByEmpId
        Optional<Employee> opt = repo.findByEmpId(testId);
        assert opt.isPresent();
        Employee got = opt.get();
        assert got.getEmpId()==testId;
        assert "Test".equals(got.getName());

        // findByQuery by name
        List<Employee> q1 = repo.findByQuery("Test");
        assert q1.stream().anyMatch(x->x.getEmpId()==testId);

        // findAll contains our record
        List<Employee> all = repo.findAll();
        assert all.stream().anyMatch(x->x.getEmpId()==testId);

        // logMonthlyPay
        boolean logged = repo.logMonthlyPay(testId);
        assert logged;

        System.out.println("MySQLEmployeeRepositoryTest passed!");
    }
}
