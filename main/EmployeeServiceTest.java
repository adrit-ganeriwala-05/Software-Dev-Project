/* Test for EmployeeService.java */

import java.time.LocalDate;
import java.util.*;

public class EmployeeServiceTest {
    // Mock for white-box testing
    static class MockRepo implements EmployeeRepository {
        private final Map<Integer,Employee> map = new HashMap<>();
        @Override public void save(Employee emp)       { map.put(emp.getEmpId(), emp); }
        @Override public Optional<Employee> findByEmpId(int id) { return Optional.ofNullable(map.get(id)); }
        @Override public List<Employee> findByQuery(String q) {
            List<Employee> out = new ArrayList<>();
            for (Employee e: map.values()) {
                if (String.valueOf(e.getEmpId()).equals(q) ||
                    e.getName().equalsIgnoreCase(q) ||
                    e.getSsn().equals(q)) {
                    out.add(e);
                }
            }
            return out;
        }
        @Override public List<Employee> findAll() { return new ArrayList<>(map.values()); }
        @Override public boolean logMonthlyPay(int id) { return false; /* mock doesn’t log */ }
    }

    public static void main(String[] args) {
        System.out.println("=== EmployeeServiceTest ===");
        EmployeeService svc = new EmployeeService(new MockRepo());

        svc.repository.save(new Employee(1,"Alice","Smith","111111111",60000,"Eng","IT",LocalDate.now()));
        svc.repository.save(new Employee(2,"Bob","Jones","222222222",50000,"Eng","IT",LocalDate.now()));
        svc.repository.save(new Employee(3,"Carol","White","333333333",120000,"Mgr","Admin",LocalDate.now()));

        // searchEmployee
        List<Employee> r1 = svc.searchEmployee("Alice");
        assert r1.size()==1 && r1.get(0).getEmpId()==1;
        List<Employee> r2 = svc.searchEmployee("222222222");
        assert r2.size()==1 && r2.get(0).getEmpId()==2;

        // updateEmployeeData
        boolean ok = svc.updateEmployeeData(1,"Alicia","999999999");
        assert ok;
        Employee a = svc.repository.findByEmpId(1).get();
        assert "Alicia".equals(a.getName()) && "999999999".equals(a.getSsn());
        assert !svc.updateEmployeeData(99,"X","X");

        // updateEmployeeSalaryRange
        svc.updateEmployeeSalaryRange(50000,100000,10);
        assert Math.abs(svc.repository.findByEmpId(2).get().getSalary() - 55000) < 0.01;

        // totalPayByJobTitle
        double sum = svc.totalPayByJobTitle("Eng");
        // Alicia: 60000→66000, Bob:50000→55000
        assert Math.abs(sum - (66000+55000)) < 0.01;

        // calculateMonthlyPay
        double m1 = svc.calculateMonthlyPay(1);
        assert Math.abs(m1 - (66000/12.0)) < 0.01;
        assert svc.calculateMonthlyPay(99) < 0;

        // logMonthlyPay on mock to false
        assert !svc.logMonthlyPay(1);

        System.out.println("EmployeeServiceTest passed!");
    }
}
