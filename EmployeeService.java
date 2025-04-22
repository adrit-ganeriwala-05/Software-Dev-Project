import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeService {
    private List<Employee> employeeList;

    public EmployeeService() {
        this.employeeList = new ArrayList<>();
    }

    public void addEmployee(Employee employee) {
        employeeList.add(employee);
        System.out.println("Employee added successfully!");
    }

    public void searchEmployeeById(int id) {
        employeeList.stream()
                .filter(emp -> emp.getEmpId() == id)
                .forEach(System.out::println);
    }

    public void searchEmployeeByName(String name) {
        employeeList.stream()
                .filter(emp -> emp.getName().equalsIgnoreCase(name))
                .forEach(System.out::println);
    }

    public void searchEmployeeBySsn(String ssn) {
        employeeList.stream()
                .filter(emp -> emp.getSsn().equals(ssn))
                .forEach(System.out::println);
    }

    public boolean updateEmployee(int id, String name, String division, String jobTitle, Double salary) {
        Optional<Employee> optionalEmployee = employeeList.stream()
                .filter(emp -> emp.getEmpId() == id)
                .findFirst();

        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            if (name != null) employee.setName(name);
            if (division != null) employee.setDivision(division);
            if (jobTitle != null) employee.setJobTitle(jobTitle);
            if (salary != null) employee.setSalary(salary);
            return true;
        }
        return false;
    }

    public void updateSalaries(double minSalary, double maxSalary, double percentage) {
        employeeList.stream()
                .filter(emp -> emp.getSalary() >= minSalary && emp.getSalary() < maxSalary)
                .forEach(emp -> emp.setSalary(emp.getSalary() * (1 + percentage / 100)));
        System.out.println("Salaries updated successfully!");
    }
}