package com.simpledb.query;

import com.simpledb.table.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database manages multiple tables and provides a unified interface.
 *
 * Data Structure: HashMap for table storage
 * Algorithm: Hash-based table lookup
 */
public class Database {
    private String dataDirectory;
    private Map<String, Table> tables;

    public Database(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.tables = new HashMap<>();
    }

    /**
     * Create a new table.
     * Algorithm: Initialize table structure and storage
     */
    public void createTable(String tableName, List<String> columns) {
        if (tables.containsKey(tableName)) {
            throw new RuntimeException("Table '" + tableName + "' already exists");
        }

        Table table = new Table(tableName, columns, dataDirectory);
        tables.put(tableName, table);
    }

    /**
     * Get a table by name.
     * Time Complexity: O(1) due to HashMap
     */
    public Table getTable(String tableName) {
        return tables.get(tableName);
    }

    /**
     * Close all tables and flush changes.
     */
    public void close() {
        for (Table table : tables.values()) {
            table.close();
        }
    }

    /**
     * List all table names.
     */
    public List<String> getTableNames() {
        return List.copyOf(tables.keySet());
    }
}
