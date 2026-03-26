package com.simpledb.examples;

import com.simpledb.storage.Page;
import com.simpledb.storage.PageManager;
import com.simpledb.index.BTree;

/**
 * DataStructuresDemo demonstrates the core data structures used in the database.
 *
 * This helps understand:
 * 1. How B-Trees organize data for fast searching
 * 2. How pages store data in fixed-size blocks
 * 3. How the page manager handles memory and disk I/O
 */
public class DataStructuresDemo {

    public static void main(String[] args) {
        System.out.println("=== Data Structures & Algorithms Demo ===\n");

        demonstratePageStorage();
        demonstrateBTreeOperations();
        demonstrateComplexityAnalysis();
    }

    /**
     * Demonstrate page-based storage.
     */
    private static void demonstratePageStorage() {
        System.out.println("1. PAGE STORAGE");
        System.out.println("   - Pages are fixed-size blocks (4KB each)");
        System.out.println("   - Similar to how operating systems use pages in virtual memory");
        System.out.println("   - Data Structure: Byte array with fixed size\n");

        Page page = new Page(0);
        System.out.println("   Created page with ID: " + page.getPageId());
        System.out.println("   Page size: " + Page.getPageSize() + " bytes");

        // Write some data
        String data = "Hello, Database!";
        page.writeAtOffset(0, data.getBytes());
        System.out.println("   Written data: " + data);

        // Read data back
        byte[] readData = page.readFromOffset(0, data.length());
        System.out.println("   Read data: " + new String(readData));
        System.out.println("   Is page dirty? " + page.isDirty() + "\n");
    }

    /**
     * Demonstrate B-Tree operations in detail.
     */
    private static void demonstrateBTreeOperations() {
        System.out.println("2. B-TREE INDEX");
        System.out.println("   - B-Trees are balanced tree structures");
        System.out.println("   - Each node can have multiple keys and children");
        System.out.println("   - Minimizes disk I/O by reading many keys at once");
        System.out.println("   - Data Structure: Multi-way search tree\n");

        BTree btree = new BTree();

        System.out.println("   Inserting keys in order: 50, 30, 70, 20, 40, 60, 80");
        int[] keys = {50, 30, 70, 20, 40, 60, 80};

        for (int key : keys) {
            btree.insert(key, "Record-" + key);
            System.out.println("   Inserted key: " + key);
        }

        System.out.println("\n   B-Tree structure after insertions:");
        btree.traverse();

        System.out.println("\n   Search operations:");
        for (int searchKey : new int[]{30, 60, 100}) {
            Object result = btree.search(searchKey);
            System.out.println("   Search for " + searchKey + ": " +
                             (result != null ? result : "Not found"));
        }
        System.out.println();
    }

    /**
     * Demonstrate time complexity analysis.
     */
    private static void demonstrateComplexityAnalysis() {
        System.out.println("3. ALGORITHM COMPLEXITY ANALYSIS");
        System.out.println("   Understanding how different data structures perform:\n");

        System.out.println("   B-Tree Operations:");
        System.out.println("   - Search:  O(log n) - Logarithmic time");
        System.out.println("   - Insert:  O(log n) - Logarithmic time");
        System.out.println("   - Delete:  O(log n) - Logarithmic time");
        System.out.println("   - Reason:  Tree height grows logarithmically\n");

        System.out.println("   Sequential Scan (No Index):");
        System.out.println("   - Search:  O(n) - Linear time");
        System.out.println("   - Reason:  Must check every record\n");

        System.out.println("   Hash Table (for in-memory cache):");
        System.out.println("   - Search:  O(1) - Constant time (average)");
        System.out.println("   - Insert:  O(1) - Constant time (average)");
        System.out.println("   - Reason:  Direct addressing via hash function\n");

        // Practical demonstration
        System.out.println("   PRACTICAL COMPARISON:");
        System.out.println("   For 1,000,000 records:");
        System.out.println("   - Sequential scan: ~1,000,000 comparisons");
        System.out.println("   - B-Tree search:   ~20 comparisons (log₂ 1M ≈ 20)");
        System.out.println("   - Hash lookup:     ~1 comparison\n");

        System.out.println("   This is why databases use indexes!");
    }
}
