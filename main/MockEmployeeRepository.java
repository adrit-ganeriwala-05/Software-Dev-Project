package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MockEmployeeRepository implements EmployeeRepository {
    private Map<Integer, Employee> employeeMap = new HashMap<>();

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
            if (String.valueOf(e.getEmpId()).equals(query) ||
                e.getName().equalsIgnoreCase(query) ||
                e.getSsn().equals(query)) {
                result.add(e);
            }
        }
        return result;
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employeeMap.values());
    }
}
