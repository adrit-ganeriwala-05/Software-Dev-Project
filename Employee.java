public class Employee {
    private int empId;
    private String name;
    private String ssn;
    private String division;
    private String jobTitle;
    private double salary;

    public Employee(int empId, String name, String ssn, String division, String jobTitle, double salary) {
        this.empId = empId;
        this.name = name;
        this.ssn = ssn;
        this.division = division;
        this.jobTitle = jobTitle;
        this.salary = salary;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %d, Name: %s, SSN: %s, Division: %s, Job Title: %s, Salary: %.2f",
                empId, name, ssn, division, jobTitle, salary);
    }
}