package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * MODEL layer - recursive sorting algorithm (CA2 section 1, 25 marks).
 *
 * WHY MERGE SORT WAS CHOSEN (justification, not a definition)
 * -----------------------------------------------------------
 * 1. Guaranteed performance. Merge sort is O(n log n) in the best, average AND
 *    worst case. Quick sort has the same average cost but degrades to O(n^2)
 *    on the exact input this program is most likely to receive: a file of form
 *    submissions that is already partly in alphabetical order, or a file that
 *    contains long runs of the same surname. The store cannot control the order
 *    in which applications arrive, so an algorithm with no bad case is worth
 *    more here than one that is slightly faster on average.
 *
 * 2. Stability. Merge sort is stable, so two employees with exactly the same
 *    full name (the data file deliberately contains two staff called John
 *    Anderson) keep the order in which they were read from the file. That
 *    matters because the search option reports every match: with a stable sort
 *    the output is reproducible run after run, which is not true of quick sort
 *    or heap sort.
 *
 * 3. It is naturally recursive. The brief requires a RECURSIVE algorithm.
 *    Merge sort is divide and conquer by construction - split, recurse, merge -
 *    so the recursion is the algorithm itself rather than a loop rewritten as
 *    recursion. Insertion sort or bubble sort can be made recursive, but only
 *    artificially, and both are O(n^2).
 *
 * 4. Cost accepted. Merge sort needs O(n) extra memory, unlike heap sort which
 *    sorts in place. With a staff list of tens or a few thousand records that
 *    memory is irrelevant, while the guaranteed worst case and the stability
 *    are not. That is the trade-off this solution deliberately takes.
 *
 * Rejected alternatives in one line each: quick sort (O(n^2) worst case on
 * nearly-sorted input, not stable), heap sort (not stable, not naturally
 * recursive), insertion sort (O(n^2)), Collections.sort (the brief asks the
 * student to implement the algorithm).
 */
public final class RecursiveMergeSort {

    /** Utility class - never instantiated. */
    private RecursiveMergeSort() {
    }

    /**
     * Sorts employees by full name, ignoring case, without modifying the
     * original list (the repository keeps the file order intact).
     */
    public static List<Employee> sortByName(List<Employee> employees) {
        Comparator<Employee> byName =
                Comparator.comparing(Employee::getFullName, String.CASE_INSENSITIVE_ORDER);
        return mergeSort(new ArrayList<>(employees), byName);
    }

    /**
     * The recursive step: a list of 0 or 1 element is already sorted (base
     * case), anything else is split in half, each half is sorted by a
     * recursive call, and the two sorted halves are merged.
     */
    private static List<Employee> mergeSort(List<Employee> list, Comparator<Employee> comparator) {
        if (list.size() <= 1) {
            return list;                                     // base case - stops the recursion
        }

        int middle = list.size() / 2;
        List<Employee> leftHalf = new ArrayList<>(list.subList(0, middle));
        List<Employee> rightHalf = new ArrayList<>(list.subList(middle, list.size()));

        List<Employee> sortedLeft = mergeSort(leftHalf, comparator);    // recursive call 1
        List<Employee> sortedRight = mergeSort(rightHalf, comparator);  // recursive call 2

        return merge(sortedLeft, sortedRight, comparator);
    }

    /**
     * Merges two already sorted lists into one sorted list.
     * Using <= 0 (and not < 0) is what makes this sort STABLE.
     */
    private static List<Employee> merge(List<Employee> left,
                                        List<Employee> right,
                                        Comparator<Employee> comparator) {
        List<Employee> merged = new ArrayList<>(left.size() + right.size());
        int i = 0;
        int j = 0;

        while (i < left.size() && j < right.size()) {
            if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                merged.add(left.get(i));
                i++;
            } else {
                merged.add(right.get(j));
                j++;
            }
        }
        while (i < left.size()) {
            merged.add(left.get(i));
            i++;
        }
        while (j < right.size()) {
            merged.add(right.get(j));
            j++;
        }
        return merged;
    }
}
