package controller;

import model.Department;
import model.Employee;
import model.EmployeeBinaryTree;
import model.EmployeeRepository;
import model.ManagerType;
import model.RecursiveBinarySearch;
import model.RecursiveMergeSort;
import view.ConsoleView;
import view.MenuOption;

import java.io.IOException;
import java.util.List;

/**
 * CONTROLLER layer - the only class that knows the order in which things
 * happen.
 *
 * It receives what the user typed from the view, asks the model to do the work
 * (read the file, sort, search, validate, build the tree) and then asks the
 * view to display the result. It never prints and it never parses a file
 * itself, so all three responsibilities stay separated:
 *
 *   MODEL      - data and rules      (Employee, Department, ManagerType,
 *                                     EmployeeRepository, RecursiveMergeSort,
 *                                     RecursiveBinarySearch, EmployeeBinaryTree)
 *   VIEW       - screen and keyboard (ConsoleView, MenuOption)
 *   CONTROLLER - the flow            (this class)
 */
public class StoreController {

    /** The brief asks for the first 20 names of the sorted list. */
    private static final int NAMES_TO_DISPLAY = 20;

    /** The brief asks for a minimum of 20 records in the binary tree. */
    private static final int MINIMUM_TREE_RECORDS = 20;

    private final EmployeeRepository repository;
    private final ConsoleView view;

    public StoreController(EmployeeRepository repository, ConsoleView view) {
        this.repository = repository;
        this.view = view;
    }

    /** Entry point of the application flow. */
    public void run(String fileNameFromArguments) {
        view.showWelcome();

        if (!loadDataFile(fileNameFromArguments)) {
            return;                                          // could not read any file
        }

        boolean running = true;
        while (running) {
            view.showMenu();
            String typed = view.askMenuChoice();

            if (view.isInputExhausted()) {
                break;                                       // scripted input finished
            }

            MenuOption option = MenuOption.fromCode(typed);
            if (option == null) {
                view.showInvalidMenuChoice(typed);
                continue;
            }

            view.showSelected(option);

            switch (option) {
                case SORT:
                    handleSort();
                    break;
                case SEARCH:
                    handleSearch();
                    break;
                case ADD_RECORDS:
                    handleAddRecords();
                    break;
                case CREATE_BINARY_TREE:
                    handleBinaryTree();
                    break;
                case EXIT:
                    running = false;
                    break;
            }
        }

        view.showGoodbye();
        view.close();
    }

    // ------------------------------------------------------------- file input

    /**
     * Asks for a file name and keeps asking until a file is read or the user
     * gives up (three attempts).
     *
     * @return true when records were loaded
     */
    private boolean loadDataFile(String fileNameFromArguments) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            String fileName = (attempt == 1 && fileNameFromArguments != null)
                    ? fileNameFromArguments
                    : view.askFileName();

            try {
                int records = repository.loadFromFile(fileName);
                view.showFileLoaded(records, repository.getLoadedFileName());
                view.showLoadWarnings(repository.getLoadWarnings());

                if (records == 0) {
                    view.showFileError("The file was read but contains no valid employee records.");
                    return false;
                }
                return true;
            } catch (IOException exception) {
                view.showFileError(exception.getMessage());
                if (view.isInputExhausted()) {
                    return false;
                }
            }
        }
        view.showMessage("Too many failed attempts. Closing the application.");
        return false;
    }

    // ------------------------------------------------------------------- sort

    /** CA2 section 1 - recursive merge sort, first 20 names displayed. */
    private void handleSort() {
        List<Employee> sorted = RecursiveMergeSort.sortByName(repository.getAll());
        view.showSortedEmployees(sorted, NAMES_TO_DISPLAY);
    }

    // ----------------------------------------------------------------- search

    /** CA2 section 2 - recursive binary search on the sorted list. */
    private void handleSearch() {
        String name = view.askSearchName();
        if (view.isInputExhausted() && name.isEmpty()) {
            return;
        }

        // Binary search only works on sorted data, so the list is sorted first
        // with the same recursive merge sort used by the SORT option.
        List<Employee> sorted = RecursiveMergeSort.sortByName(repository.getAll());
        List<Employee> matches = RecursiveBinarySearch.findAllByName(sorted, name);
        view.showSearchResults(name, matches);
    }

    // -------------------------------------------------------------- add record

    /**
     * CA2 section 3 - new user input with validation.
     *
     * The controller asks the view for raw text and asks the model whether that
     * text is a real ManagerType / Department. Nothing is added to the list
     * until all three fields are valid, so the data can never be corrupted.
     */
    private void handleAddRecords() {
        boolean addMore = true;

        while (addMore) {
            String firstName = readNonEmptyName(true);
            if (firstName == null) {
                return;
            }
            String lastName = readNonEmptyName(false);
            if (lastName == null) {
                return;
            }

            ManagerType managerType = readManagerType();
            if (managerType == null) {
                return;
            }

            Department department = readDepartment();
            if (department == null) {
                return;
            }

            Employee employee = new Employee(firstName, lastName, managerType, department);
            repository.add(employee);
            view.showRecordAdded(employee);

            String answer = view.askAddAnother();
            addMore = answer.trim().equalsIgnoreCase("y") || answer.trim().equalsIgnoreCase("yes");
            if (view.isInputExhausted()) {
                addMore = false;
            }
        }

        view.showNewlyAdded(repository.getNewlyAdded(), repository.size());
    }

    /**
     * Validation rule: a name must not be empty and must contain letters only
     * (letters, spaces, apostrophes and hyphens, so names such as O'Brien and
     * Mary-Anne are accepted).
     *
     * @return the valid name, or null when the input stream ended
     */
    private String readNonEmptyName(boolean first) {
        while (true) {
            String value = first ? view.askFirstName() : view.askLastName();
            if (view.isInputExhausted()) {
                return null;
            }
            String trimmed = value.trim();

            if (trimmed.isEmpty()) {
                view.showValidationError("the name cannot be empty.");
                continue;
            }
            if (!trimmed.matches("[A-Za-z][A-Za-z '\\-]*")) {
                view.showValidationError("\"" + trimmed + "\" is not a valid name "
                        + "(letters, spaces, apostrophes and hyphens only).");
                continue;
            }
            return trimmed;
        }
    }

    /**
     * Validation rule: the manager type must already exist in the ManagerType
     * enum. Anything else is rejected and the question is asked again.
     */
    private ManagerType readManagerType() {
        while (true) {
            String typed = view.askManagerType();
            if (view.isInputExhausted()) {
                return null;
            }
            ManagerType managerType = ManagerType.fromInput(typed);
            if (managerType == null) {
                view.showValidationError("\"" + typed.trim()
                        + "\" is not an existing manager type. Choose one from the list above.");
                continue;
            }
            return managerType;
        }
    }

    /**
     * Validation rule: the department must already exist in the Department
     * enum, so no employee can be filed under a department the store does not
     * have.
     */
    private Department readDepartment() {
        while (true) {
            String typed = view.askDepartment();
            if (view.isInputExhausted()) {
                return null;
            }
            Department department = Department.fromInput(typed);
            if (department == null) {
                view.showValidationError("\"" + typed.trim()
                        + "\" is not an existing department. Choose one from the list above.");
                continue;
            }
            return department;
        }
    }

    // ------------------------------------------------------------ binary tree

    /** CA2 section 4 - binary tree built with level-order insertion. */
    private void handleBinaryTree() {
        List<Employee> employees = repository.getAll();

        if (employees.isEmpty()) {
            view.showTreeEmpty();
            return;
        }
        if (employees.size() < MINIMUM_TREE_RECORDS) {
            view.showMessage("Note: only " + employees.size()
                    + " records are loaded, the brief asks for a minimum of "
                    + MINIMUM_TREE_RECORDS + ".");
        }

        EmployeeBinaryTree tree = new EmployeeBinaryTree();
        tree.insertAll(employees);

        view.showTree(tree.levelOrderByLevel(), tree.height(), tree.size());
    }
}
