/* Test for Employee.java */

import java.time.LocalDate;

public class EmployeeTest {
    public static void main(String[] args) {
        System.out.println("=== EmployeeTest ===");

        LocalDate hire = LocalDate.of(2020, 1, 1);
        Employee e = new Employee(1, "John", "Doe", "123456789", 50000, "Dev", "IT", hire);

        // Constructor and getters
        assert e.getEmpId() == 1;
        assert "John".equals(e.getName());
        assert "Doe".equals(e.getLastName());
        assert "123456789".equals(e.getSsn());
        assert Math.abs(e.getSalary() - 50000) < 0.0001;
        assert "Dev".equals(e.getJobTitle());
        assert "IT".equals(e.getDivision());
        assert hire.equals(e.getHireDate());

        // Setters
        e.setName("Jane");
        assert "Jane".equals(e.getName());
        e.setLastName("Smith");
        assert "Smith".equals(e.getLastName());
        e.setSsn("987654321");
        assert "987654321".equals(e.getSsn());
        e.setSalary(60000);
        assert Math.abs(e.getSalary() - 60000) < 0.0001;
        e.setJobTitle("Manager");
        assert "Manager".equals(e.getJobTitle());
        e.setDivision("Admin");
        assert "Admin".equals(e.getDivision());
        LocalDate newHire = LocalDate.of(2021, 2, 2);
        e.setHireDate(newHire);
        assert newHire.equals(e.getHireDate());

        System.out.println("EmployeeTest passed!");
    }
}
