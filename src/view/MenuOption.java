package view;

/**
 * VIEW layer - the menu itself (CA2 section 6, 10 marks).
 *
 * Every option the user can choose is a constant of this enum, carrying the
 * number that is typed and the text that is printed. Three consequences:
 *
 *  - The menu is printed by iterating MenuOption.values(), so adding an option
 *    means adding one line here and nothing else in the view.
 *  - The controller switches on MenuOption, not on an int, so the compiler can
 *    see every case and there are no magic numbers anywhere in the program.
 *  - An invalid choice becomes a null from fromCode(), handled in one place.
 */
public enum MenuOption {

    SORT(1, "SORT", "Sort the employees alphabetically and show the first 20"),
    SEARCH(2, "SEARCH", "Search for an employee and show manager type and department"),
    ADD_RECORDS(3, "ADD RECORDS", "Add a new employee (name, manager type, department)"),
    CREATE_BINARY_TREE(4, "CREATE A BINARY TREE", "Build and display the employee hierarchy"),
    EXIT(5, "EXIT", "Close the application");

    private final int code;
    private final String title;
    private final String description;

    MenuOption(int code, String title, String description) {
        this.code = code;
        this.title = title;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Converts what the user typed into a menu option.
     * Accepts the number (1..5) or the title (SORT, search, add records...).
     *
     * @return the option, or null when the input matches nothing
     */
    public static MenuOption fromCode(String rawInput) {
        if (rawInput == null) {
            return null;
        }
        String input = rawInput.trim();
        if (input.isEmpty()) {
            return null;
        }

        for (MenuOption option : values()) {
            if (input.equals(String.valueOf(option.code))) {
                return option;
            }
            if (input.equalsIgnoreCase(option.title) || input.equalsIgnoreCase(option.name())) {
                return option;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return title;
    }
}
