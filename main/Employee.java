import java.time.LocalDate;

public class Employee {
    private int empId;
    private String name;
    private String lastName;
    private String ssn;
    private double salary;
    private String jobTitle;
    private String division;
    private LocalDate hireDate;

    public Employee(int empId, String name, String lastName, String ssn, double salary, String jobTitle, String division, LocalDate hireDate) {
        this.empId = empId;
        this.name = name;
        this.lastName = lastName;
        this.ssn = ssn;
        this.salary = salary;
        this.jobTitle = jobTitle;
        this.division = division;
        this.hireDate = hireDate;
    }

    // Getters
    public int getEmpId() { return empId; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getSsn() { return ssn; }
    public double getSalary() { return salary; }
    public String getJobTitle() { return jobTitle; }
    public String getDivision() { return division; }
    public LocalDate getHireDate() { return hireDate; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setSsn(String ssn) { this.ssn = ssn; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public void setDivision(String division) { this.division = division; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
}
