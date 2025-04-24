import java.util.*;

public class MockEmployeeRepository implements EmployeeRepository {
    private final Map<Integer, Employee> employeeMap = new HashMap<>();
    
    // Simulated pay_statement table
    private final Map<Integer, List<Double>> payStatements = new HashMap<>();

    @Override
    public void save(Employee employee) {
        employeeMap.put(employee.getEmpId(), employee);
    }

    @Override
    public Optional<Employee> findByEmpId(int empId) {
        return Optional.ofNullable(employeeMap.get(empId));
    }

    @Override
    public List<Employee> findByQuery(String query) {
        List<Employee> result = new ArrayList<>();
        for (Employee e : employeeMap.values()) {
            if (String.valueOf(e.getEmpId()).equals(query)
                || e.getName().equalsIgnoreCase(query)
                || e.getLastName().equalsIgnoreCase(query)
                || (e.getName() + " " + e.getLastName()).equalsIgnoreCase(query)
                || e.getSsn().equals(query)) {
                result.add(e);
            }
        }
        return result;
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employeeMap.values());
    }

    @Override
    public boolean logMonthlyPay(int empId) {
        Employee e = employeeMap.get(empId);
        if (e == null) return false;
        double monthlyPay = e.getSalary() / 12.0;
        payStatements.computeIfAbsent(empId, k -> new ArrayList<>()).add(monthlyPay);
        return true;
    }

    /**
     * Retrieve logged pays for testing or reporting
     */
    public List<Double> getPayStatements(int empId) {
        return payStatements.getOrDefault(empId, Collections.emptyList());
    }
}
