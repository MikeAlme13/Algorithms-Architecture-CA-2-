package view;

import model.Department;
import model.Employee;
import model.ManagerType;

import java.util.List;
import java.util.Scanner;

/**
 * VIEW layer - the ONLY class in the whole program that prints to the screen or
 * reads from the keyboard.
 *
 * Because every System.out and every Scanner call lives here, the model and the
 * controller contain no input/output at all. Swapping this console view for a
 * graphical one would not change a single line of the model or the controller,
 * which is exactly the benefit the MVC pattern is supposed to deliver.
 */
public class ConsoleView {

    private static final String LINE =
            "==============================================================================";
    private static final String THIN =
            "------------------------------------------------------------------------------";

    private final Scanner scanner = new Scanner(System.in);

    /**
     * When the program is fed from a script instead of the keyboard, the typed
     * text does not appear on screen, which makes the saved transcript
     * unreadable. Running the program with -Dca2.echoInput=true prints the
     * answers as if they had been typed. It is off during normal use.
     */
    private final boolean echoInput = Boolean.parseBoolean(System.getProperty("ca2.echoInput", "false"));

    /** True once the input stream has no more lines (end of a scripted run). */
    private boolean inputExhausted = false;

    // ---------------------------------------------------------------- start up

    public void showWelcome() {
        System.out.println();
        System.out.println(LINE);
        System.out.println("   GRAFTON & HUGHES DEPARTMENT STORE - STAFF MANAGEMENT SYSTEM");
        System.out.println("   CCT College Dublin - Algorithms & Architecture - CA2");
        System.out.println(LINE);
    }

    public String askFileName() {
        System.out.println();
        System.out.print("Please enter the filename to read [Applicants_Form.txt]: ");
        return readLine();
    }

    public void showFileLoaded(int records, String fileName) {
        System.out.println("File read successfully");
        System.out.println(records + " employee records loaded from " + fileName);
    }

    public void showLoadWarnings(List<String> warnings) {
        if (warnings == null || warnings.isEmpty()) {
            return;
        }
        System.out.println();
        System.out.println("Data validation notes (" + warnings.size() + "):");
        for (String warning : warnings) {
            System.out.println("  ! " + warning);
        }
    }

    public void showFileError(String message) {
        System.out.println();
        System.out.println("ERROR: " + message);
        System.out.println("Please check the file name and try again.");
    }

    // ------------------------------------------------------------------- menu

    /** Prints the menu by iterating over the MenuOption enum - no hard coded text. */
    public void showMenu() {
        System.out.println();
        System.out.println(LINE);
        System.out.println("Do You wish to SORT or SEARCH:");
        System.out.println(THIN);
        for (MenuOption option : MenuOption.values()) {
            System.out.printf("  %d. %-22s %s%n",
                    option.getCode(), option.getTitle(), option.getDescription());
        }
        System.out.println(LINE);
        System.out.print("Select an option (1-" + MenuOption.values().length + "): ");
    }

    public String askMenuChoice() {
        return readLine();
    }

    public void showSelected(MenuOption option) {
        System.out.println();
        System.out.println(option.getTitle() + " selected");
        System.out.println(THIN);
    }

    public void showInvalidMenuChoice(String typed) {
        System.out.println();
        System.out.println("\"" + typed + "\" is not a valid option. Please choose a number between 1 and "
                + MenuOption.values().length + ".");
    }

    // ------------------------------------------------------------------- sort

    public void showSortedEmployees(List<Employee> sorted, int limit) {
        int shown = Math.min(limit, sorted.size());
        System.out.println("Employees sorted alphabetically with a recursive merge sort.");
        System.out.println("Showing the first " + shown + " of " + sorted.size() + " records.");
        System.out.println();
        System.out.printf("  %-4s %-22s %-19s %s%n", "No.", "NAME", "MANAGER TYPE", "DEPARTMENT");
        System.out.println("  " + THIN);
        for (int i = 0; i < shown; i++) {
            Employee employee = sorted.get(i);
            System.out.printf("  %-4d %-22s %-19s %s%n",
                    i + 1,
                    employee.getFullName(),
                    employee.getManagerType().getDisplayName(),
                    employee.getDepartment().getDisplayName());
        }
    }

    // ----------------------------------------------------------------- search

    public String askSearchName() {
        System.out.print("Enter the full name of the employee to search for: ");
        return readLine();
    }

    public void showSearchResults(String searchedName, List<Employee> matches) {
        System.out.println();
        if (matches.isEmpty()) {
            System.out.println("No employee named \"" + searchedName + "\" was found in the list.");
            System.out.println("Tip: the search expects the full name, for example \"Kate Summer\".");
            return;
        }

        System.out.println(matches.size() + " record(s) found for \"" + searchedName + "\":");
        System.out.println();
        for (Employee employee : matches) {
            System.out.println("  Name         : " + employee.getFullName());
            System.out.println("  Manager Type : " + employee.getManagerType().getDisplayName());
            System.out.println("  Department   : " + employee.getDepartment().getDisplayName());
            System.out.println("  Email        : " + employee.getEmail());
            System.out.println("  " + THIN);
        }
    }

    // -------------------------------------------------------------- add record

    public String askFirstName() {
        System.out.print("First name: ");
        return readLine();
    }

    public String askLastName() {
        System.out.print("Last name : ");
        return readLine();
    }

    /** Shows every valid manager type, straight from the enum, then asks for one. */
    public String askManagerType() {
        System.out.println();
        System.out.println("Valid manager types:");
        ManagerType[] types = ManagerType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, types[i].getDisplayName());
        }
        System.out.print("Manager type (number or name): ");
        return readLine();
    }

    /** Shows every existing department, straight from the enum, then asks for one. */
    public String askDepartment() {
        System.out.println();
        System.out.println("Existing departments:");
        Department[] departments = Department.values();
        for (int i = 0; i < departments.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, departments[i].getDisplayName());
        }
        System.out.print("Department (number or name): ");
        return readLine();
    }

    public void showValidationError(String message) {
        System.out.println("  >> INVALID: " + message);
    }

    public void showRecordAdded(Employee employee) {
        System.out.println();
        System.out.println("Record added successfully:");
        System.out.println("  " + employee.getFullName()
                + " - " + employee.getManagerType().getDisplayName()
                + " - " + employee.getDepartment().getDisplayName());
    }

    public void showNewlyAdded(List<Employee> newlyAdded, int totalRecords) {
        System.out.println();
        System.out.println("All records added during this session (" + newlyAdded.size() + "):");
        System.out.println();
        System.out.printf("  %-4s %-22s %-19s %s%n", "No.", "NAME", "MANAGER TYPE", "DEPARTMENT");
        System.out.println("  " + THIN);
        int number = 1;
        for (Employee employee : newlyAdded) {
            System.out.printf("  %-4d %-22s %-19s %s%n",
                    number++,
                    employee.getFullName(),
                    employee.getManagerType().getDisplayName(),
                    employee.getDepartment().getDisplayName());
        }
        System.out.println();
        System.out.println("The list now holds " + totalRecords + " employee records in memory.");
    }

    public String askAddAnother() {
        System.out.println();
        System.out.print("Add another record? (y/n): ");
        return readLine();
    }

    // ------------------------------------------------------------ binary tree

    public void showTree(List<List<Employee>> levels, int height, int nodeCount) {
        System.out.println("Employee hierarchy built with level-order (breadth-first) insertion.");
        System.out.println("Each node stores Name, Manager Type and Department.");
        System.out.println();
        System.out.println("LEVEL ORDER TRAVERSAL");
        System.out.println(THIN);

        for (int level = 0; level < levels.size(); level++) {
            System.out.println("Level " + level + " (" + levels.get(level).size() + " node(s)):");
            for (Employee employee : levels.get(level)) {
                System.out.println("    " + employee.getFullName()
                        + "  [" + employee.getManagerType().getDisplayName()
                        + " - " + employee.getDepartment().getDisplayName() + "]");
            }
            System.out.println();
        }

        System.out.println(THIN);
        System.out.println("Tree height (number of levels) : " + height);
        System.out.println("Total node count               : " + nodeCount);
        System.out.println(THIN);
    }

    public void showTreeEmpty() {
        System.out.println("The tree is empty because no employee records are loaded.");
    }

    // ---------------------------------------------------------------- general

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showGoodbye() {
        System.out.println();
        System.out.println(LINE);
        System.out.println("   EXIT selected. Thank you for using the Staff Management System.");
        System.out.println(LINE);
        System.out.println();
    }

    /**
     * Reads one line. Returns an empty string instead of crashing when the
     * input stream ends (useful when the program is run with a scripted input
     * file for testing or for the demonstration video).
     */
    private String readLine() {
        if (!scanner.hasNextLine()) {
            inputExhausted = true;
            System.out.println();
            return "";
        }
        String line = scanner.nextLine();
        if (echoInput) {
            System.out.println(line);
        }
        return line;
    }

    /** Lets the controller stop cleanly when a scripted input file runs out. */
    public boolean isInputExhausted() {
        return inputExhausted;
    }

    public void close() {
        scanner.close();
    }
}
