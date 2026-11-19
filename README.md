# Grafton & Hughes Department Store — Staff Management System

**CCT College Dublin — BSc (Hons) in Computing — Algorithms & Architecture — CA2**

A console application that manages the staff records of a department store. It
reads employee records from a text file and lets the user sort them with a
recursive algorithm, search them with a recursive algorithm, add new validated
records, and build an employee hierarchy as a binary tree.

---

## How to run

### Option A — double click
Run `run.bat`. It compiles the project and starts the application.

### Option B — command line
```
javac -encoding UTF-8 -d out src/Main.java src/model/*.java src/view/*.java src/controller/*.java
java -cp out Main
```

At the first prompt type `Applicants_Form.txt` (or just press Enter, it is the
default). The data file lives in the `data` folder.

---

## Project structure (MVC)

```
DepartmentStoreCA2/
├── data/
│   ├── Applicants_Form.txt                  47 employee records (department store)
│   └── Applicants_Form_original_sample.txt  the file supplied with the brief
├── src/
│   ├── Main.java                            creates and wires the three layers
│   ├── model/        (MODEL — data and rules, no printing)
│   │   ├── Employee.java                    one member of staff (immutable)
│   │   ├── Department.java                  enum of the 13 store departments
│   │   ├── ManagerType.java                 enum of the 7 management grades
│   │   ├── EmployeeRepository.java          reads the file, holds the list, adds records
│   │   ├── RecursiveMergeSort.java          recursive sorting algorithm
│   │   ├── RecursiveBinarySearch.java       recursive searching algorithm
│   │   └── EmployeeBinaryTree.java          level-order hierarchy + recursive height
│   ├── view/         (VIEW — screen and keyboard, nothing else)
│   │   ├── ConsoleView.java                 the only class with System.out / Scanner
│   │   └── MenuOption.java                  enum that defines the menu
│   └── controller/   (CONTROLLER — the flow)
│       └── StoreController.java             reads input, calls the model, calls the view
├── run.bat
└── README.md
```

---

## The five menu options

| # | Option | What it does |
|---|--------|--------------|
| 1 | SORT | Recursive merge sort by full name, displays the first 20 of 47 records |
| 2 | SEARCH | Recursive binary search, returns Name, Manager Type, Department and Email; reports **every** match when a name is duplicated |
| 3 | ADD RECORDS | Asks for name, manager type and department, validates all three, updates the list and displays every record added in the session |
| 4 | CREATE A BINARY TREE | Inserts all 47 records in level order, prints the level-order traversal, the tree height and the node count |
| 5 | EXIT | Closes the application |

---

## Algorithm choices (short version — full justification in the documentation)

**Sorting — recursive merge sort.** O(n log n) in *every* case, including the
nearly-sorted input a file of form submissions typically is, where quick sort
degrades to O(n²). It is stable, so the two employees called *John Anderson*
keep their file order and the output is reproducible. It is divide-and-conquer
by construction, so the recursion is genuine rather than a loop rewritten.
The O(n) extra memory it costs is irrelevant at this data size.

**Searching — recursive binary search.** The sorted list already exists from
option 1, so the precondition is free and lookups cost O(log n) instead of the
O(n) of a linear scan (≈6 comparisons instead of 47). A HashMap would be O(1)
but duplicates the data, has to be rebuilt on every insert and cannot produce
ordered output. After a hit the algorithm expands left and right so duplicated
names are all returned.

**Hierarchy — binary tree with level-order (breadth-first) insertion.** Fills
the first free left slot, then the first free right slot, then moves on, which
keeps the tree complete and the height at log₂(n): 47 nodes give a height of 6.
The height is computed recursively (1 + the taller subtree).

---

## MVC in one paragraph

`ConsoleView` is the only class in the program that calls `System.out` or reads
the keyboard. The model classes contain no input/output at all. The controller
never parses a file and never prints: it takes the text the view collected,
asks the model whether it is valid, and asks the view to display the result.
Replacing the console with a graphical interface would mean rewriting
`ConsoleView` only — not one line of the model or the controller.

---

## Validation rules

* The data file must have 9 columns; bad lines are reported, not silently dropped.
* A department or manager type that is not in the enum is rejected on load **and** on input.
* Names must be letters, spaces, apostrophes or hyphens, and cannot be empty.
* An unknown menu option is reported and the menu is shown again.
* A missing data file is reported and the user gets three attempts.

---

## Author

Maycon Almeida — Student number 2024354
Video: https://drive.google.com/drive/folders/13h-PacLGsm9ly4mzno5SyIThzKCXDsaU
Lecturer: Taufique Ahmed — Submission: 29 November 2026
