import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    // Save a new employee or update an existing one
    void save(Employee employee);

    // Retrieve employee by empId
    Optional<Employee> findByEmpId(int empId);

    // Find employees by a query (name, ssn, or empId as string)
    List<Employee> findByQuery(String query);

    // Retrieve all employees (for report generation)
    List<Employee> findAll();

    // Log monthly pay for a specific employee
    boolean logMonthlyPay(int empId);
}
