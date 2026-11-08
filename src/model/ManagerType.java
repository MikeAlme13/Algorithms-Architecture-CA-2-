package model;

/**
 * MODEL layer - domain rule.
 *
 * The management grades used by the store, from the top of the structure
 * (Store Manager) down to the shop floor (Sales Assistant).
 *
 * Exactly like {@link Department}, this is an enum so that the rule "the
 * manager type must exist" is guaranteed by the type system instead of being
 * checked with string comparisons spread around the code.
 */
public enum ManagerType {

    STORE_MANAGER("Store Manager"),
    SENIOR_MANAGER("Senior Manager"),
    DEPARTMENT_MANAGER("Department Manager"),
    ASSISTANT_MANAGER("Assistant Manager"),
    TEAM_LEAD("Team Lead"),
    SUPERVISOR("Supervisor"),
    SALES_ASSISTANT("Sales Assistant");

    private final String displayName;

    ManagerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Accepts the number shown in the menu (1..7), the enum name (TEAM_LEAD)
     * or the display name in any reasonable spelling.
     *
     * @return the matching manager type, or null when the input does not exist.
     */
    public static ManagerType fromInput(String rawInput) {
        if (rawInput == null) {
            return null;
        }
        String input = rawInput.trim();
        if (input.isEmpty()) {
            return null;
        }

        if (input.matches("\\d+")) {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < values().length) {
                return values()[index];
            }
            return null;
        }

        String normalisedInput = normalise(input);
        for (ManagerType managerType : values()) {
            if (normalise(managerType.displayName).equals(normalisedInput)
                    || normalise(managerType.name()).equals(normalisedInput)) {
                return managerType;
            }
        }
        return null;
    }

    private static String normalise(String text) {
        return text.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    @Override
    public String toString() {
        return displayName;
    }
}
