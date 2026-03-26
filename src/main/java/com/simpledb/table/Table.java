package com.simpledb.table;

import com.simpledb.storage.Page;
import com.simpledb.storage.PageManager;
import com.simpledb.index.BTree;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Table represents a database table with records and indexes.
 *
 * Data Structure: Combination of:
 * - PageManager for data storage
 * - BTree for indexing
 * - ArrayList for schema management
 *
 * Algorithm: Heap file structure with B-Tree indexing
 */
public class Table {
    private String tableName;
    private List<String> columns;
    private PageManager pageManager;
    private BTree primaryIndex;
    private int nextRecordId;
    private String dataDirectory;

    public Table(String tableName, List<String> columns, String dataDirectory) {
        this.tableName = tableName;
        this.columns = new ArrayList<>(columns);
        this.dataDirectory = dataDirectory;
        this.pageManager = new PageManager(dataDirectory + "/" + tableName + ".dat");
        this.primaryIndex = new BTree();
        this.nextRecordId = 0;

        // Create directory if it doesn't exist
        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        loadMetadata();
    }

    /**
     * Insert a record into the table.
     * Algorithm: Sequential insertion with B-Tree indexing
     * Time Complexity: O(log n) due to B-Tree insertion
     */
    public void insert(Record record) {
        if (record.getId() == 0) {
            record = new Record(nextRecordId++);
        } else {
            nextRecordId = Math.max(nextRecordId, record.getId() + 1);
        }

        // Serialize record
        byte[] recordData = record.toBytes();

        // Find or allocate a page with enough space
        Page page = findPageWithSpace(recordData.length);

        // Write record to page
        int offset = findFreeOffset(page, recordData.length);
        page.writeAtOffset(offset, recordData);

        // Update index
        primaryIndex.insert(record.getId(), page.getPageId() + ":" + offset);

        // Write page back to disk
        pageManager.writePage(page);

        saveMetadata();
    }

    /**
     * Select a record by ID.
     * Algorithm: B-Tree lookup followed by page read
     * Time Complexity: O(log n)
     */
    public Record selectById(int id) {
        Object location = primaryIndex.search(id);

        if (location == null) {
            return null;
        }

        // Parse location (format: "pageId:offset")
        String[] parts = location.toString().split(":");
        int pageId = Integer.parseInt(parts[0]);
        int offset = Integer.parseInt(parts[1]);

        // Load page and extract record
        Page page = pageManager.getPage(pageId);

        // Read record size first (assuming it's stored as first 4 bytes)
        byte[] sizeBytes = page.readFromOffset(offset, 4);
        int recordSize = bytesToInt(sizeBytes);

        // Read full record
        byte[] recordData = page.readFromOffset(offset + 4, recordSize);
        return Record.fromBytes(recordData);
    }

    /**
     * Select all records (full table scan).
     * Algorithm: Sequential page scan
     * Time Complexity: O(n)
     */
    public List<Record> selectAll() {
        List<Record> records = new ArrayList<>();

        // Scan all pages
        for (int pageId = 0; pageId < pageManager.getNextPageId(); pageId++) {
            Page page = pageManager.getPage(pageId);

            // Try to read records from this page
            int offset = 0;
            while (offset < Page.getPageSize() - 4) {
                try {
                    byte[] sizeBytes = page.readFromOffset(offset, 4);
                    int recordSize = bytesToInt(sizeBytes);

                    if (recordSize <= 0 || recordSize > Page.getPageSize()) {
                        break; // No more records in this page
                    }

                    byte[] recordData = page.readFromOffset(offset + 4, recordSize);
                    Record record = Record.fromBytes(recordData);
                    records.add(record);

                    offset += 4 + recordSize;
                } catch (Exception e) {
                    break; // Error reading record, move to next page
                }
            }
        }

        return records;
    }

    /**
     * Find a page with enough free space.
     */
    private Page findPageWithSpace(int requiredSpace) {
        // Simple implementation: always allocate a new page
        // A real database would track free space per page
        return pageManager.allocatePage();
    }

    /**
     * Find free offset in a page.
     */
    private int findFreeOffset(Page page, int requiredSpace) {
        // Simple implementation: start at beginning
        // A real database would maintain a free space map
        return 0;
    }

    /**
     * Save table metadata.
     */
    private void saveMetadata() {
        try (PrintWriter writer = new PrintWriter(dataDirectory + "/" + tableName + ".meta")) {
            writer.println(nextRecordId);
            writer.println(String.join(",", columns));
        } catch (IOException e) {
            // Metadata save failed, not critical
        }
    }

    /**
     * Load table metadata.
     */
    private void loadMetadata() {
        File metaFile = new File(dataDirectory + "/" + tableName + ".meta");
        if (!metaFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(metaFile))) {
            nextRecordId = Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            // Metadata load failed, start fresh
            nextRecordId = 0;
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return new ArrayList<>(columns);
    }

    /**
     * Utility method to convert 4 bytes to int.
     */
    private int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8) |
               (bytes[3] & 0xFF);
    }

    /**
     * Close the table and flush all changes.
     */
    public void close() {
        pageManager.flushAll();
        saveMetadata();
    }
}
