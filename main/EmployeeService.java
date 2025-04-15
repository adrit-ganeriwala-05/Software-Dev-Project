package main;


// EmployeeService.java
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmployeeService {
    EmployeeRepository repository;
    
    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }
    
    // Search for employees by name, SSN or empId in string format
    public List<Employee> searchEmployee(String query) {
        return repository.findByQuery(query);
    }
    
    // Update employee's general data
    public boolean updateEmployeeData(int empId, String newName, String newSsn) {
        Optional<Employee> opt = repository.findByEmpId(empId);
        if (opt.isPresent()) {
            Employee e = opt.get();
            e.setName(newName);
            e.setSsn(newSsn);
            repository.save(e);
            return true;
        }
        return false;
    }
    
    // Increase salary by a percentage for employees within a given salary range
    public void updateEmployeeSalaryRange(double minSalary, double maxSalary, double percentageIncrease) {
        List<Employee> allEmployees = repository.findAll();
        List<Employee> eligibleEmployees = allEmployees.stream()
            .filter(e -> e.getSalary() >= minSalary && e.getSalary() < maxSalary)
            .collect(Collectors.toList());
        
        for (Employee e : eligibleEmployees) {
            double newSalary = e.getSalary() * (1 + percentageIncrease / 100);
            e.setSalary(newSalary);
            repository.save(e);
        }
    }
    
    // Example report method: total pay by job title
    public double totalPayByJobTitle(String jobTitle) {
        return repository.findAll().stream()
                .filter(e -> e.getJobTitle().equalsIgnoreCase(jobTitle))
                .mapToDouble(Employee::getSalary)
                .sum();
    }
}
