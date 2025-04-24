import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmployeeService {
    EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> searchEmployee(String query) {
        return repository.findByQuery(query);
    }

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

    public double totalPayByJobTitle(String jobTitle) {
        return repository.findAll().stream()
                .filter(e -> e.getJobTitle().equalsIgnoreCase(jobTitle))
                .mapToDouble(Employee::getSalary)
                .sum();
    }

    // NEW: Calculate monthly pay
    public double calculateMonthlyPay(int empId) {
        Optional<Employee> opt = repository.findByEmpId(empId);
        if (opt.isPresent()) {
            return opt.get().getSalary() / 12.0;
        }
        return -1;
    }

    public boolean logMonthlyPay(int empId) {
        if (repository instanceof MySQLEmployeeRepository sqlRepo) {
            return sqlRepo.logMonthlyPay(empId);
        }
        return false;
    }
    
}