package com.simpledb.index;

import java.util.ArrayList;
import java.util.List;

/**
 * BTreeNode represents a node in the B-Tree index structure.
 *
 * Data Structure: B-Tree Node
 * Algorithm: B-Tree operations for efficient searching, insertion, and deletion
 *
 * B-Trees are widely used in databases because:
 * - They minimize disk I/O by storing multiple keys per node
 * - They maintain balance automatically
 * - They support range queries efficiently
 */
public class BTreeNode {
    private static final int ORDER = 4; // Minimum degree (minimum children = ORDER, max = 2*ORDER)

    private List<Integer> keys;
    private List<Object> values;
    private List<BTreeNode> children;
    private boolean isLeaf;
    private BTreeNode parent;

    public BTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
        this.children = new ArrayList<>();
        this.parent = null;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public List<Integer> getKeys() {
        return keys;
    }

    public List<Object> getValues() {
        return values;
    }

    public List<BTreeNode> getChildren() {
        return children;
    }

    public BTreeNode getParent() {
        return parent;
    }

    public void setParent(BTreeNode parent) {
        this.parent = parent;
    }

    /**
     * Search for a key in this node.
     * Algorithm: Binary search for efficiency
     */
    public int search(int key) {
        int low = 0;
        int high = keys.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            int midKey = keys.get(mid);

            if (midKey == key) {
                return mid;
            } else if (midKey < key) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1; // Not found
    }

    /**
     * Find the child index where a key should be inserted or searched.
     */
    public int findChildIndex(int key) {
        int index = 0;
        while (index < keys.size() && key > keys.get(index)) {
            index++;
        }
        return index;
    }

    /**
     * Insert a key-value pair into this node.
     * Assumes the node is not full.
     */
    public void insertNonFull(int key, Object value) {
        int index = keys.size() - 1;

        if (isLeaf) {
            // Insert key in sorted order
            keys.add(null);
            values.add(null);

            while (index >= 0 && keys.get(index) > key) {
                keys.set(index + 1, keys.get(index));
                values.set(index + 1, values.get(index));
                index--;
            }

            keys.set(index + 1, key);
            values.set(index + 1, value);
        } else {
            // Find child to insert into
            while (index >= 0 && keys.get(index) > key) {
                index--;
            }
            index++;

            BTreeNode child = children.get(index);
            if (child.isFull()) {
                splitChild(index);

                if (keys.get(index) < key) {
                    index++;
                }
            }

            children.get(index).insertNonFull(key, value);
        }
    }

    /**
     * Split a full child node.
     * Algorithm: B-Tree node splitting
     */
    public void splitChild(int childIndex) {
        BTreeNode fullChild = children.get(childIndex);
        BTreeNode newChild = new BTreeNode(fullChild.isLeaf);

        int midIndex = ORDER - 1;

        // Move half of the keys to the new child
        for (int i = midIndex + 1; i < fullChild.keys.size(); i++) {
            newChild.keys.add(fullChild.keys.get(i));
            newChild.values.add(fullChild.values.get(i));
        }

        if (!fullChild.isLeaf) {
            for (int i = midIndex + 1; i < fullChild.children.size(); i++) {
                BTreeNode child = fullChild.children.get(i);
                newChild.children.add(child);
                child.setParent(newChild);
            }
        }

        // Insert middle key into parent
        int middleKey = fullChild.keys.get(midIndex);
        Object middleValue = fullChild.values.get(midIndex);

        keys.add(childIndex, middleKey);
        values.add(childIndex, middleValue);
        children.add(childIndex + 1, newChild);
        newChild.setParent(this);

        // Remove moved keys from original child
        fullChild.keys.subList(midIndex, fullChild.keys.size()).clear();
        fullChild.values.subList(midIndex, fullChild.values.size()).clear();

        if (!fullChild.isLeaf) {
            fullChild.children.subList(midIndex + 1, fullChild.children.size()).clear();
        }
    }

    public boolean isFull() {
        return keys.size() >= 2 * ORDER - 1;
    }

    public static int getOrder() {
        return ORDER;
    }
}
