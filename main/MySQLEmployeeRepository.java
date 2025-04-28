import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


public class MySQLEmployeeRepository implements EmployeeRepository {
    private static final String URL = "jdbc:mysql://localhost:3306/employeeData";
    private static final String USER = "root";

    // Change this to your local password for DB
    // Also uses: PASSWORD = "Imraniman2017"
    
    private static final String PASSWORD = "Dillard082993";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void save(Employee employee) {
        String sql = """
            INSERT INTO employee (emp_id, first_name, last_name, ssn, salary, job_title, division, hire_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
              first_name = VALUES(first_name),
              last_name = VALUES(last_name),
              ssn = VALUES(ssn),
              salary = VALUES(salary),
              job_title = VALUES(job_title),
              division = VALUES(division),
              hire_date = VALUES(hire_date)
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employee.getEmpId());
            stmt.setString(2, employee.getName());
            stmt.setString(3, employee.getLastName());
            stmt.setString(4, employee.getSsn());
            stmt.setDouble(5, employee.getSalary());
            stmt.setString(6, employee.getJobTitle());
            stmt.setString(7, employee.getDivision());
            stmt.setDate(8, Date.valueOf(employee.getHireDate()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Employee> findByEmpId(int empId) {
        String sql = "SELECT * FROM employee WHERE emp_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Employee> findByQuery(String query) {
        String sql = "SELECT * FROM employee WHERE emp_id = ? OR first_name LIKE ? OR ssn = ?";
        List<Employee> results = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, query);
            stmt.setString(2, "%" + query + "%");
            stmt.setString(3, query);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    @Override
    public List<Employee> findAll() {
        String sql = "SELECT * FROM employee";
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    @Override
public boolean logMonthlyPay(int empId) {
    Optional<Employee> opt = findByEmpId(empId);
    if (opt.isEmpty()) return false;

    Employee e = opt.get();
    double monthlyPay = e.getSalary() / 12.0;

    String sql = "INSERT INTO pay_statement (emp_id, pay_date, total_pay) VALUES (?, CURDATE(), ?)";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, empId);
        stmt.setDouble(2, monthlyPay);

        stmt.executeUpdate();
        return true;

    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return false;
}



    private Employee mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("emp_id");
        String name = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String ssn = rs.getString("ssn");
        double salary = rs.getDouble("salary");
        String jobTitle = rs.getString("job_title");
        String division = rs.getString("division");
        LocalDate hireDate = rs.getDate("hire_date").toLocalDate();

        return new Employee(id, name, lastName, ssn, salary, jobTitle, division, hireDate);
    }
}
