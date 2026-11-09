package model;

import java.util.ArrayList;
import java.util.List;

/**
 * MODEL layer - recursive searching algorithm (CA2 section 2, 15 marks).
 *
 * WHY RECURSIVE BINARY SEARCH WAS CHOSEN (justification, not a definition)
 * -----------------------------------------------------------------------
 * 1. The precondition is already paid for. Binary search needs a sorted list,
 *    and section 1 of this assignment already produces one with merge sort. The
 *    sorted list is therefore free at this point, so binary search costs
 *    O(log n) per lookup instead of the O(n) a linear scan would cost. On the
 *    47 records loaded here that is at most 6 comparisons rather than 47, and
 *    the gap widens as the store grows.
 *
 * 2. It fits the recursion requirement honestly. The brief asks for a RECURSIVE
 *    search. Binary search is defined by reducing the problem to one half of
 *    itself, so each call genuinely solves a smaller instance of the same
 *    problem. A recursive linear search would be recursion for its own sake.
 *
 * 3. No extra structure to keep in sync. A HashMap would give O(1) lookups,
 *    but it would be a second copy of the data that has to be rebuilt or
 *    updated every time the ADD RECORDS option inserts a new employee, and it
 *    cannot answer the "show the first 20 names in order" requirement at all.
 *    Searching the one sorted list the program already maintains keeps a single
 *    source of truth.
 *
 * 4. Duplicates are handled. Binary search alone only guarantees SOME match.
 *    The data set deliberately contains two employees called John Anderson, so
 *    once a hit is found this class expands left and right around it to collect
 *    every employee with that name, and the view reports all of them.
 *
 * Rejected alternatives in one line each: linear search (O(n), wastes the sort
 * that was already done), HashMap (O(1) but duplicates the data, must be
 * rebuilt on every insert and cannot produce ordered output), interpolation
 * search (assumes evenly distributed keys - surnames are not evenly
 * distributed, so it can be worse than binary search).
 */
public final class RecursiveBinarySearch {

    /** Utility class - never instantiated. */
    private RecursiveBinarySearch() {
    }

    /**
     * Finds every employee whose full name matches the search term.
     *
     * @param sortedEmployees list already sorted by full name (case insensitive)
     * @param nameToFind      the name typed by the user
     * @return all matching employees, or an empty list when the name is not found
     */
    public static List<Employee> findAllByName(List<Employee> sortedEmployees, String nameToFind) {
        List<Employee> matches = new ArrayList<>();
        if (sortedEmployees == null || sortedEmployees.isEmpty() || nameToFind == null) {
            return matches;
        }

        String target = nameToFind.trim();
        int hit = binarySearch(sortedEmployees, target, 0, sortedEmployees.size() - 1);

        if (hit < 0) {
            return matches;                                  // not found - empty list
        }

        // Walk outwards from the hit so that duplicated names are all reported.
        int first = hit;
        while (first > 0 && sameName(sortedEmployees.get(first - 1), target)) {
            first--;
        }
        int last = hit;
        while (last < sortedEmployees.size() - 1 && sameName(sortedEmployees.get(last + 1), target)) {
            last++;
        }
        for (int i = first; i <= last; i++) {
            matches.add(sortedEmployees.get(i));
        }
        return matches;
    }

    /**
     * The recursive step.
     *
     * Base case 1: low > high  -> the name is not in the list, return -1.
     * Base case 2: the middle element matches -> return its index.
     * Otherwise the method calls itself on the half that can still contain the
     * name, which halves the problem on every call.
     *
     * @return the index of a matching employee, or -1 when there is none
     */
    private static int binarySearch(List<Employee> sortedEmployees, String target, int low, int high) {
        if (low > high) {
            return -1;                                       // base case - name not present
        }

        int middle = low + (high - low) / 2;                 // avoids integer overflow
        int comparison = sortedEmployees.get(middle).getFullName().compareToIgnoreCase(target);

        if (comparison == 0) {
            return middle;                                   // base case - found
        }
        if (comparison > 0) {
            return binarySearch(sortedEmployees, target, low, middle - 1);   // search the left half
        }
        return binarySearch(sortedEmployees, target, middle + 1, high);      // search the right half
    }

    private static boolean sameName(Employee employee, String target) {
        return employee.getFullName().equalsIgnoreCase(target);
    }
}
