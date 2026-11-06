package model;

/**
 * MODEL layer - the central entity of the system.
 *
 * One member of staff of the department store. The object is immutable: once an
 * employee has been validated and created it cannot be put into an invalid
 * state afterwards, which removes a whole class of bugs from the controller.
 */
public class Employee {

    private final String firstName;
    private final String lastName;
    private final String gender;
    private final String email;
    private final double salary;
    private final Department department;
    private final String seniority;
    private final ManagerType managerType;
    private final String company;

    /** Full constructor - used when a record is read from the data file. */
    public Employee(String firstName,
                    String lastName,
                    String gender,
                    String email,
                    double salary,
                    Department department,
                    String seniority,
                    ManagerType managerType,
                    String company) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.salary = salary;
        this.department = department;
        this.seniority = seniority;
        this.managerType = managerType;
        this.company = company;
    }

    /**
     * Short constructor - used by the ADD RECORDS option, where the brief only
     * asks the user for Name, Manager Type and Department.
     */
    public Employee(String firstName, String lastName, ManagerType managerType, Department department) {
        this(firstName, lastName, "Not provided", buildEmail(firstName, lastName),
                0.0, department, "new entry", managerType, "Grafton & Hughes");
    }

    private static String buildEmail(String firstName, String lastName) {
        String initial = (firstName == null || firstName.isEmpty())
                ? "x"
                : firstName.substring(0, 1).toLowerCase();
        String surname = (lastName == null) ? "" : lastName.toLowerCase().replaceAll("[^a-z]", "");
        return initial + surname + "@graftonhughes.ie";
    }

    /** The key used by both the sorting and the searching algorithms. */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public double getSalary() {
        return salary;
    }

    public Department getDepartment() {
        return department;
    }

    public String getSeniority() {
        return seniority;
    }

    public ManagerType getManagerType() {
        return managerType;
    }

    public String getCompany() {
        return company;
    }

    @Override
    public String toString() {
        return String.format("%-22s | %-19s | %s",
                getFullName(), managerType.getDisplayName(), department.getDisplayName());
    }
}
