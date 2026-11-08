package model;

/**
 * MODEL layer - domain rule.
 *
 * The departments that exist inside the Grafton and Hughes Department Store.
 * Using an enum (instead of free text) means an invalid department simply
 * cannot be created: validating user input becomes a lookup, not a list of
 * if-statements that has to be kept in sync by hand.
 */
public enum Department {

    MENSWEAR("Menswear"),
    WOMENSWEAR("Womenswear"),
    ELECTRONICS("Electronics"),
    HOME_AND_FURNITURE("Home & Furniture"),
    BEAUTY("Beauty"),
    GROCERIES("Groceries"),
    TOYS("Toys"),
    CUSTOMER_SERVICE("Customer Service"),
    HUMAN_RESOURCES("Human Resources"),
    FINANCE("Finance"),
    MARKETING("Marketing"),
    WAREHOUSE("Warehouse"),
    SECURITY("Security");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Accepts the number shown in the menu (1..13), the enum name
     * (HOME_AND_FURNITURE) or the display name in any reasonable spelling.
     *
     * @return the matching department, or null when the input does not exist.
     */
    public static Department fromInput(String rawInput) {
        if (rawInput == null) {
            return null;
        }
        String input = rawInput.trim();
        if (input.isEmpty()) {
            return null;
        }

        // 1) the user typed the position shown on screen
        if (input.matches("\\d+")) {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < values().length) {
                return values()[index];
            }
            return null;
        }

        // 2) the user typed the name
        String normalisedInput = normalise(input);
        for (Department department : values()) {
            if (normalise(department.displayName).equals(normalisedInput)
                    || normalise(department.name()).equals(normalisedInput)) {
                return department;
            }
        }
        return null;
    }

    /** Home & Furniture, home and furniture and HOME_AND_FURNITURE all become homeandfurniture. */
    private static String normalise(String text) {
        return text.toLowerCase()
                .replace("&", "and")
                .replaceAll("[^a-z0-9]", "");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
