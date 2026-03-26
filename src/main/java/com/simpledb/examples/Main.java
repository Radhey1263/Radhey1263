package com.simpledb.examples;

import com.simpledb.query.Database;
import com.simpledb.query.SimpleQueryParser;
import com.simpledb.table.Record;
import com.simpledb.table.Table;
import com.simpledb.index.BTree;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Main class demonstrating the SimpleDB database system.
 *
 * This educational database implementation demonstrates:
 * 1. B-Tree indexing for fast lookups
 * 2. Page-based storage management
 * 3. Basic SQL query parsing
 * 4. Transaction concepts (simplified)
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("     SimpleDB - Educational Database System      ");
        System.out.println("   Learning Data Structures & Algorithms in Java ");
        System.out.println("=================================================\n");

        // Create a database instance
        Database db = new Database("./db_data");
        SimpleQueryParser parser = new SimpleQueryParser("./db_data");

        // Run examples
        runBasicExamples(db, parser);

        // Interactive mode
        System.out.println("\n--- Interactive Mode ---");
        System.out.println("Enter SQL queries (or 'exit' to quit):");
        runInteractiveMode(db, parser);

        // Cleanup
        db.close();
        System.out.println("\nDatabase closed successfully.");
    }

    /**
     * Run basic examples to demonstrate functionality.
     */
    private static void runBasicExamples(Database db, SimpleQueryParser parser) {
        System.out.println("=== Example 1: B-Tree Index Demo ===");
        demonstrateBTree();

        System.out.println("\n=== Example 2: Database Operations ===");

        // Create a table
        System.out.println("\n1. Creating 'users' table...");
        String result = parser.execute("CREATE TABLE users (id, name, email, age)", db);
        System.out.println(result);

        // Insert some records
        System.out.println("\n2. Inserting records...");
        String[] insertQueries = {
            "INSERT INTO users VALUES (1, Alice, alice@email.com, 25)",
            "INSERT INTO users VALUES (2, Bob, bob@email.com, 30)",
            "INSERT INTO users VALUES (3, Charlie, charlie@email.com, 35)",
            "INSERT INTO users VALUES (4, Diana, diana@email.com, 28)"
        };

        for (String query : insertQueries) {
            result = parser.execute(query, db);
            System.out.println("  " + result);
        }

        // Select all records
        System.out.println("\n3. Selecting all records...");
        result = parser.execute("SELECT * FROM users", db);
        System.out.println(result);

        // Select by ID (using B-Tree index)
        System.out.println("\n4. Selecting by ID (using B-Tree index)...");
        result = parser.execute("SELECT * FROM users WHERE id = 2", db);
        System.out.println(result);

        // Create another table
        System.out.println("\n5. Creating 'products' table...");
        result = parser.execute("CREATE TABLE products (id, name, price, category)", db);
        System.out.println(result);

        // Insert product records
        System.out.println("\n6. Inserting product records...");
        String[] productQueries = {
            "INSERT INTO products VALUES (1, Laptop, 999.99, Electronics)",
            "INSERT INTO products VALUES (2, Book, 19.99, Books)",
            "INSERT INTO products VALUES (3, Phone, 599.99, Electronics)"
        };

        for (String query : productQueries) {
            result = parser.execute(query, db);
            System.out.println("  " + result);
        }

        // Select products
        System.out.println("\n7. Selecting all products...");
        result = parser.execute("SELECT * FROM products", db);
        System.out.println(result);
    }

    /**
     * Demonstrate B-Tree indexing structure.
     */
    private static void demonstrateBTree() {
        System.out.println("Creating a B-Tree and inserting keys: 10, 20, 5, 15, 30, 25, 40");

        BTree btree = new BTree();

        // Insert keys
        int[] keys = {10, 20, 5, 15, 30, 25, 40};
        for (int key : keys) {
            btree.insert(key, "Value for " + key);
            System.out.println("Inserted: " + key);
        }

        System.out.println("\nB-Tree structure:");
        btree.traverse();

        System.out.println("\nSearching for key 15:");
        Object value = btree.search(15);
        System.out.println("Found: " + value);

        System.out.println("\nSearching for key 100 (doesn't exist):");
        value = btree.search(100);
        System.out.println("Found: " + value);
    }

    /**
     * Interactive query mode.
     */
    private static void runInteractiveMode(Database db, SimpleQueryParser parser) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nSimpleDB> ");
            String query = scanner.nextLine().trim();

            if (query.equalsIgnoreCase("exit") || query.equalsIgnoreCase("quit")) {
                break;
            }

            if (query.isEmpty()) {
                continue;
            }

            String result = parser.execute(query, db);
            System.out.println(result);
        }

        scanner.close();
    }
}
