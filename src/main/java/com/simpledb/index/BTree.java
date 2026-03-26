package com.simpledb.index;

/**
 * BTree implements a B-Tree index structure for efficient data retrieval.
 *
 * Data Structure: B-Tree
 * Algorithm: B-Tree insert, search, and traversal operations
 *
 * Time Complexity:
 * - Search: O(log n)
 * - Insert: O(log n)
 * - Delete: O(log n)
 *
 * B-Trees are preferred in databases over binary search trees because:
 * 1. They minimize disk I/O by having high fanout (many children per node)
 * 2. They stay balanced automatically
 * 3. They're optimized for systems that read/write large blocks of data
 */
public class BTree {
    private BTreeNode root;

    public BTree() {
        this.root = new BTreeNode(true);
    }

    /**
     * Search for a value by key.
     * Algorithm: Top-down tree traversal with binary search at each node
     */
    public Object search(int key) {
        return searchNode(root, key);
    }

    private Object searchNode(BTreeNode node, int key) {
        int index = node.search(key);

        if (index != -1) {
            // Key found in this node
            return node.getValues().get(index);
        }

        if (node.isLeaf()) {
            // Key not found and we're at a leaf
            return null;
        }

        // Search in the appropriate child
        int childIndex = node.findChildIndex(key);
        return searchNode(node.getChildren().get(childIndex), key);
    }

    /**
     * Insert a key-value pair into the B-Tree.
     * Algorithm: B-Tree insertion with node splitting when necessary
     */
    public void insert(int key, Object value) {
        BTreeNode r = root;

        if (r.isFull()) {
            // Create new root
            BTreeNode newRoot = new BTreeNode(false);
            newRoot.getChildren().add(root);
            root.setParent(newRoot);
            newRoot.splitChild(0);
            root = newRoot;
            newRoot.insertNonFull(key, value);
        } else {
            r.insertNonFull(key, value);
        }
    }

    /**
     * Traverse the B-Tree in order (left to right).
     * Algorithm: In-order tree traversal
     */
    public void traverse() {
        traverseNode(root, 0);
    }

    private void traverseNode(BTreeNode node, int level) {
        if (node == null) {
            return;
        }

        System.out.println("Level " + level + ": " + node.getKeys());

        if (!node.isLeaf()) {
            for (BTreeNode child : node.getChildren()) {
                traverseNode(child, level + 1);
            }
        }
    }

    /**
     * Check if the B-Tree contains a key.
     */
    public boolean contains(int key) {
        return search(key) != null;
    }

    public BTreeNode getRoot() {
        return root;
    }
}
