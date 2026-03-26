package com.simpledb.query;

import com.simpledb.table.Record;
import com.simpledb.table.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SimpleQueryParser parses and executes basic SQL-like queries.
 *
 * Data Structure: String parsing with tokenization
 * Algorithm: Recursive descent parser (simplified)
 *
 * Supported queries:
 * - CREATE TABLE table_name (col1, col2, col3)
 * - INSERT INTO table_name VALUES (val1, val2, val3)
 * - SELECT * FROM table_name
 * - SELECT * FROM table_name WHERE id = value
 */
public class SimpleQueryParser {
    private String dataDirectory;

    public SimpleQueryParser(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    /**
     * Parse and execute a query.
     * Algorithm: Tokenization and pattern matching
     */
    public String execute(String query, Database database) {
        query = query.trim();

        if (query.toUpperCase().startsWith("CREATE TABLE")) {
            return executeCreateTable(query, database);
        } else if (query.toUpperCase().startsWith("INSERT INTO")) {
            return executeInsert(query, database);
        } else if (query.toUpperCase().startsWith("SELECT")) {
            return executeSelect(query, database);
        } else {
            return "ERROR: Unknown query type";
        }
    }

    /**
     * Execute CREATE TABLE query.
     * Example: CREATE TABLE users (id, name, email)
     */
    private String executeCreateTable(String query, Database database) {
        try {
            // Extract table name
            int tableStart = query.indexOf("TABLE") + 6;
            int tableEnd = query.indexOf("(");
            String tableName = query.substring(tableStart, tableEnd).trim();

            // Extract columns
            int colStart = query.indexOf("(") + 1;
            int colEnd = query.indexOf(")");
            String columnsStr = query.substring(colStart, colEnd);
            String[] columnsArray = columnsStr.split(",");
            List<String> columns = new ArrayList<>();
            for (String col : columnsArray) {
                columns.add(col.trim());
            }

            database.createTable(tableName, columns);
            return "Table '" + tableName + "' created successfully";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Execute INSERT query.
     * Example: INSERT INTO users VALUES (1, John, john@email.com)
     */
    private String executeInsert(String query, Database database) {
        try {
            // Extract table name
            int tableStart = query.indexOf("INTO") + 5;
            int tableEnd = query.indexOf("VALUES");
            String tableName = query.substring(tableStart, tableEnd).trim();

            // Extract values
            int valStart = query.indexOf("(") + 1;
            int valEnd = query.lastIndexOf(")");
            String valuesStr = query.substring(valStart, valEnd);
            String[] valuesArray = valuesStr.split(",");

            Table table = database.getTable(tableName);
            if (table == null) {
                return "ERROR: Table '" + tableName + "' does not exist";
            }

            Record record = new Record(0); // Auto-generate ID

            List<String> columns = table.getColumns();
            for (int i = 0; i < Math.min(columns.size(), valuesArray.length); i++) {
                String value = valuesArray[i].trim();
                record.setField(columns.get(i), value);
            }

            table.insert(record);
            return "Record inserted successfully";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Execute SELECT query.
     * Examples:
     * - SELECT * FROM users
     * - SELECT * FROM users WHERE id = 1
     */
    private String executeSelect(String query, Database database) {
        try {
            // Extract table name
            int fromPos = query.toUpperCase().indexOf("FROM") + 5;
            int wherePos = query.toUpperCase().indexOf("WHERE");

            String tableName;
            if (wherePos == -1) {
                tableName = query.substring(fromPos).trim();
            } else {
                tableName = query.substring(fromPos, wherePos).trim();
            }

            Table table = database.getTable(tableName);
            if (table == null) {
                return "ERROR: Table '" + tableName + "' does not exist";
            }

            List<Record> results;

            if (wherePos != -1) {
                // Parse WHERE clause (simple: id = value)
                String whereClause = query.substring(wherePos + 6).trim();
                String[] parts = whereClause.split("=");
                String column = parts[0].trim();
                String value = parts[1].trim();

                if (column.equals("id")) {
                    int id = Integer.parseInt(value);
                    Record record = table.selectById(id);
                    results = record != null ? Arrays.asList(record) : new ArrayList<>();
                } else {
                    // For non-id columns, do a table scan
                    results = table.selectAll();
                    // Filter results (simplified)
                    List<Record> filtered = new ArrayList<>();
                    for (Record r : results) {
                        Object fieldValue = r.getField(column);
                        if (fieldValue != null && fieldValue.toString().equals(value)) {
                            filtered.add(r);
                        }
                    }
                    results = filtered;
                }
            } else {
                results = table.selectAll();
            }

            // Format results
            StringBuilder sb = new StringBuilder();
            sb.append("Found ").append(results.size()).append(" record(s):\n");
            for (Record record : results) {
                sb.append(record.toString()).append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
