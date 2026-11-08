package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MODEL layer - the in-memory store of employees and the only class that
 * touches the data file.
 *
 * The controller never reads a file and never parses a line: it asks the
 * repository to load a file name and gets back validated Employee objects.
 * That separation is what makes the program testable and is part of the MVC
 * answer in the documentation.
 */
public class EmployeeRepository {

    /** Every employee currently known to the program, in the order read/added. */
    private final List<Employee> employees = new ArrayList<>();

    /** Only the records typed by the user during this session. */
    private final List<Employee> newlyAdded = new ArrayList<>();

    /** Lines of the file that could not be parsed, reported back to the view. */
    private final List<String> loadWarnings = new ArrayList<>();

    private String loadedFileName = "";

    /**
     * Reads the employee records from a comma separated text file.
     *
     * The file name is resolved in a forgiving way, so the user can type either
     * Applicants_Form.txt or data/Applicants_Form.txt.
     *
     * @param requestedFileName what the user typed at the prompt
     * @return the number of records successfully read
     * @throws IOException when no readable file can be found
     */
    public int loadFromFile(String requestedFileName) throws IOException {
        Path file = resolve(requestedFileName);
        if (file == null) {
            throw new IOException("File not found: " + requestedFileName);
        }

        employees.clear();
        newlyAdded.clear();
        loadWarnings.clear();

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        boolean headerSkipped = false;

        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);

            if (line == null || line.trim().isEmpty()) {
                continue;                                    // blank lines are ignored
            }
            if (!headerSkipped && line.toLowerCase().startsWith("first name")) {
                headerSkipped = true;                         // the CSV header row
                continue;
            }

            Employee employee = parseLine(line, lineNumber);
            if (employee != null) {
                employees.add(employee);
            }
        }

        loadedFileName = file.toString();
        return employees.size();
    }

    /**
     * Turns one line of the file into an Employee.
     * A line with a missing or unknown Department / Manager type is rejected and
     * recorded as a warning rather than silently corrupting the data.
     */
    private Employee parseLine(String line, int lineNumber) {
        String[] columns = line.split(",", -1);
        if (columns.length < 9) {
            loadWarnings.add("Line " + lineNumber + " skipped: expected 9 columns, found " + columns.length);
            return null;
        }

        String firstName = columns[0].trim();
        String lastName = columns[1].trim();
        String gender = columns[2].trim();
        String email = columns[3].trim();
        String salaryText = columns[4].trim();
        String departmentText = columns[5].trim();
        String seniority = columns[6].trim();
        String managerTypeText = columns[7].trim();
        String company = columns[8].trim();

        Department department = Department.fromInput(departmentText);
        if (department == null) {
            loadWarnings.add("Line " + lineNumber + " skipped: unknown department \"" + departmentText + "\"");
            return null;
        }

        ManagerType managerType = ManagerType.fromInput(managerTypeText);
        if (managerType == null) {
            loadWarnings.add("Line " + lineNumber + " skipped: unknown manager type \"" + managerTypeText + "\"");
            return null;
        }

        double salary = 0.0;
        try {
            if (!salaryText.isEmpty()) {
                salary = Double.parseDouble(salaryText);
            }
        } catch (NumberFormatException ignored) {
            loadWarnings.add("Line " + lineNumber + ": salary \"" + salaryText + "\" is not a number, stored as 0");
        }

        if (seniority.isEmpty()) {
            seniority = "not stated";
        }

        return new Employee(firstName, lastName, gender, email, salary,
                department, seniority, managerType, company);
    }

    /**
     * Resolves the file name the user typed. Tries the path as given, then the
     * data folder, then the project folder.
     */
    private Path resolve(String requestedFileName) {
        String name = (requestedFileName == null) ? "" : requestedFileName.trim();
        if (name.isEmpty()) {
            name = "Applicants_Form.txt";                     // default used when the user just presses Enter
        }

        List<Path> candidates = new ArrayList<>();
        candidates.add(Paths.get(name));
        candidates.add(Paths.get("data", name));
        candidates.add(Paths.get("..", "data", name));
        candidates.add(Paths.get(System.getProperty("user.dir"), "data", name));

        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate) && Files.isReadable(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    /** Adds one employee typed by the user. */
    public void add(Employee employee) {
        employees.add(employee);
        newlyAdded.add(employee);
    }

    /** Unmodifiable view of all employees, so no other layer can corrupt the list. */
    public List<Employee> getAll() {
        return Collections.unmodifiableList(employees);
    }

    public List<Employee> getNewlyAdded() {
        return Collections.unmodifiableList(newlyAdded);
    }

    public List<String> getLoadWarnings() {
        return Collections.unmodifiableList(loadWarnings);
    }

    public String getLoadedFileName() {
        return loadedFileName;
    }

    public int size() {
        return employees.size();
    }

    public boolean isEmpty() {
        return employees.isEmpty();
    }
}
