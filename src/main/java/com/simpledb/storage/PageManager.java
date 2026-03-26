package com.simpledb.storage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * PageManager handles reading and writing pages to disk.
 *
 * Data Structure: HashMap for in-memory page cache (LRU could be implemented)
 * Algorithm: Buffer pool management with simple caching
 */
public class PageManager {
    private String dbFilePath;
    private Map<Integer, Page> pageCache;
    private int maxCacheSize = 100;
    private int nextPageId = 0;

    public PageManager(String dbFilePath) {
        this.dbFilePath = dbFilePath;
        this.pageCache = new HashMap<>();
        initializeFile();
    }

    private void initializeFile() {
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create database file", e);
            }
        } else {
            // Calculate next page ID based on file size
            nextPageId = (int) (dbFile.length() / Page.getPageSize());
        }
    }

    /**
     * Get a page from cache or disk.
     * Algorithm: Cache lookup with fallback to disk I/O
     */
    public Page getPage(int pageId) {
        // Check cache first
        if (pageCache.containsKey(pageId)) {
            return pageCache.get(pageId);
        }

        // Load from disk
        Page page = loadPageFromDisk(pageId);

        // Add to cache (simple approach without LRU eviction)
        if (pageCache.size() < maxCacheSize) {
            pageCache.put(pageId, page);
        }

        return page;
    }

    /**
     * Allocate a new page.
     * Algorithm: Sequential allocation
     */
    public Page allocatePage() {
        Page page = new Page(nextPageId++);
        pageCache.put(page.getPageId(), page);
        return page;
    }

    /**
     * Write a page to disk.
     * Algorithm: Direct file I/O at calculated offset
     */
    public void writePage(Page page) {
        try (RandomAccessFile file = new RandomAccessFile(dbFilePath, "rw")) {
            long offset = (long) page.getPageId() * Page.getPageSize();
            file.seek(offset);
            file.write(page.getData());
            page.setClean();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write page " + page.getPageId(), e);
        }
    }

    /**
     * Load a page from disk.
     */
    private Page loadPageFromDisk(int pageId) {
        try (RandomAccessFile file = new RandomAccessFile(dbFilePath, "r")) {
            long offset = (long) pageId * Page.getPageSize();

            if (offset >= file.length()) {
                // Page doesn't exist yet, return empty page
                return new Page(pageId);
            }

            file.seek(offset);
            byte[] data = new byte[Page.getPageSize()];
            file.read(data);
            return new Page(pageId, data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load page " + pageId, e);
        }
    }

    /**
     * Flush all dirty pages to disk.
     * Algorithm: Iterate through cache and write dirty pages
     */
    public void flushAll() {
        for (Page page : pageCache.values()) {
            if (page.isDirty()) {
                writePage(page);
            }
        }
    }

    public int getNextPageId() {
        return nextPageId;
    }
}
