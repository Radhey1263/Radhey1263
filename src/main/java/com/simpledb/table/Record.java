package com.simpledb.table;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Record represents a single row in a table.
 *
 * Data Structure: HashMap for flexible column storage
 * Algorithm: Hash-based key-value storage for O(1) field access
 */
public class Record implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Map<String, Object> fields;

    public Record(int id) {
        this.id = id;
        this.fields = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setField(String columnName, Object value) {
        fields.put(columnName, value);
    }

    public Object getField(String columnName) {
        return fields.get(columnName);
    }

    public Map<String, Object> getAllFields() {
        return new HashMap<>(fields);
    }

    /**
     * Serialize record to bytes for storage.
     * Algorithm: Java serialization
     */
    public byte[] toBytes() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(this);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize record", e);
        }
    }

    /**
     * Deserialize record from bytes.
     */
    public static Record fromBytes(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (Record) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize record", e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Record{id=").append(id).append(", fields={");
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
        }
        if (!fields.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove last comma
        }
        sb.append("}}");
        return sb.toString();
    }
}
