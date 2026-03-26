package com.simpledb.storage;

import java.util.Arrays;

/**
 * Page represents a fixed-size block of data in the database.
 * This is the fundamental unit of storage in most databases.
 *
 * Data Structure Concept: Array-based storage with fixed size
 * Algorithm: Sequential read/write operations
 */
public class Page {
    private static final int PAGE_SIZE = 4096; // 4KB page size (standard in many databases)
    private byte[] data;
    private int pageId;
    private boolean isDirty; // Marks if page has been modified

    public Page(int pageId) {
        this.pageId = pageId;
        this.data = new byte[PAGE_SIZE];
        this.isDirty = false;
    }

    public Page(int pageId, byte[] data) {
        this.pageId = pageId;
        this.data = Arrays.copyOf(data, PAGE_SIZE);
        this.isDirty = false;
    }

    public int getPageId() {
        return pageId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] newData) {
        System.arraycopy(newData, 0, this.data, 0, Math.min(newData.length, PAGE_SIZE));
        this.isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }

    public static int getPageSize() {
        return PAGE_SIZE;
    }

    /**
     * Write data at a specific offset within the page.
     * Algorithm: Direct array manipulation
     */
    public void writeAtOffset(int offset, byte[] data) {
        if (offset + data.length > PAGE_SIZE) {
            throw new IllegalArgumentException("Data exceeds page boundary");
        }
        System.arraycopy(data, 0, this.data, offset, data.length);
        this.isDirty = true;
    }

    /**
     * Read data from a specific offset within the page.
     */
    public byte[] readFromOffset(int offset, int length) {
        if (offset + length > PAGE_SIZE) {
            throw new IllegalArgumentException("Read exceeds page boundary");
        }
        byte[] result = new byte[length];
        System.arraycopy(this.data, offset, result, 0, length);
        return result;
    }
}
