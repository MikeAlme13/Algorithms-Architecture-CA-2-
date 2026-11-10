package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * MODEL layer - employee hierarchy as a binary tree (CA2 section 4, 20 marks).
 *
 * Every node stores one employee (Name, Manager Type, Department) and has at
 * most two children. Nodes are inserted in LEVEL ORDER (breadth first): the
 * first free left slot is filled, then the first free right slot, and when both
 * children of a node are taken the next node in level order is used. That keeps
 * the tree complete, which is why the height stays at log2(n) and the structure
 * reads naturally as an organisation chart.
 *
 * The height is calculated RECURSIVELY (a node is 1 + the taller of its two
 * subtrees), which is the natural way to express a tree measurement.
 */
public class EmployeeBinaryTree {

    /** One position in the hierarchy. */
    public static class Node {
        private final Employee employee;
        private Node left;
        private Node right;

        Node(Employee employee) {
            this.employee = employee;
        }

        public Employee getEmployee() {
            return employee;
        }

        public Node getLeft() {
            return left;
        }

        public Node getRight() {
            return right;
        }
    }

    private Node root;
    private int nodeCount;

    /**
     * Inserts one employee using level-order (breadth-first) insertion:
     * the queue visits the nodes level by level and the employee is attached to
     * the first node that still has a free child, left slot first.
     */
    public void insert(Employee employee) {
        Node newNode = new Node(employee);
        nodeCount++;

        if (root == null) {
            root = newNode;
            return;
        }

        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.left == null) {
                current.left = newNode;
                return;
            }
            queue.add(current.left);

            if (current.right == null) {
                current.right = newNode;
                return;
            }
            queue.add(current.right);
        }
    }

    /** Inserts a whole list of employees, keeping the list order. */
    public void insertAll(List<Employee> employees) {
        for (Employee employee : employees) {
            insert(employee);
        }
    }

    /**
     * Level order traversal, returned grouped by level so the view can print
     * one line per level of the hierarchy.
     *
     * @return level 0 is the root, level 1 its two children, and so on
     */
    public List<List<Employee>> levelOrderByLevel() {
        List<List<Employee>> levels = new ArrayList<>();
        if (root == null) {
            return levels;
        }

        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int nodesOnThisLevel = queue.size();
            List<Employee> level = new ArrayList<>(nodesOnThisLevel);

            for (int i = 0; i < nodesOnThisLevel; i++) {
                Node current = queue.poll();
                level.add(current.employee);
                if (current.left != null) {
                    queue.add(current.left);
                }
                if (current.right != null) {
                    queue.add(current.right);
                }
            }
            levels.add(level);
        }
        return levels;
    }

    /** Flat level order traversal (root, then level 1 left to right, and so on). */
    public List<Employee> levelOrder() {
        List<Employee> flat = new ArrayList<>();
        for (List<Employee> level : levelOrderByLevel()) {
            flat.addAll(level);
        }
        return flat;
    }

    /** Number of levels in the tree. An empty tree has height 0. */
    public int height() {
        return height(root);
    }

    private int height(Node node) {
        if (node == null) {
            return 0;                                        // base case
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    /** Total number of employees stored in the tree. */
    public int size() {
        return nodeCount;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public Node getRoot() {
        return root;
    }
}
