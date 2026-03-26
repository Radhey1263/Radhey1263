# Learning Guide: Building a Database with Data Structures & Algorithms

This guide will help you understand how the SimpleDB project demonstrates core computer science concepts.

## Table of Contents
1. [Introduction to Databases](#introduction-to-databases)
2. [Data Structures Deep Dive](#data-structures-deep-dive)
3. [Algorithms Explained](#algorithms-explained)
4. [Step-by-Step Code Walkthrough](#step-by-step-code-walkthrough)
5. [Exercises and Challenges](#exercises-and-challenges)

---

## Introduction to Databases

### What is a Database?

A database is an organized collection of structured data. At its core, a database system needs to:
1. **Store** data persistently (on disk)
2. **Retrieve** data efficiently (using indexes)
3. **Organize** data logically (tables, schemas)
4. **Process** queries (SQL parsing and execution)

### Database Architecture Layers

```
┌─────────────────────────────────┐
│   Query Layer (SQL Interface)   │  ← SimpleQueryParser.java
├─────────────────────────────────┤
│   Table Management Layer        │  ← Table.java, Record.java
├─────────────────────────────────┤
│   Index Layer (B-Trees)         │  ← BTree.java, BTreeNode.java
├─────────────────────────────────┤
│   Storage Layer (Pages)         │  ← Page.java, PageManager.java
├─────────────────────────────────┤
│   Disk (Physical Storage)       │  ← .dat files
└─────────────────────────────────┘
```

---

## Data Structures Deep Dive

### 1. B-Tree (Balanced Tree)

**File**: `src/main/java/com/simpledb/index/BTree.java`

#### What is a B-Tree?

A B-Tree is a self-balancing tree data structure that maintains sorted data and allows searches, sequential access, insertions, and deletions in logarithmic time.

#### Why B-Trees for Databases?

```
Binary Search Tree:          B-Tree (order=4):
     50                           [25, 50, 75]
    /  \                        /    |    |    \
   25  75                    [10] [30,40] [60] [80,90]
  /  \
10   30

Disk I/O: 3 reads             Disk I/O: 2 reads
Height: 3                     Height: 2
```

**Key Advantages:**
1. **Fewer disk reads**: Each node stores multiple keys
2. **Better cache locality**: Reading one node gives you many keys
3. **Always balanced**: Automatically maintains balance
4. **Range queries**: Efficient for `WHERE age BETWEEN 20 AND 30`

#### B-Tree Properties

For a B-Tree of order `m`:
- Each node has at most `2m - 1` keys
- Each node has at least `m - 1` keys (except root)
- Each internal node has one more child than keys
- All leaves are at the same level

#### Example: Inserting into a B-Tree

```java
BTree tree = new BTree();

// Insert keys: 10, 20, 5, 15, 30
tree.insert(10, "data10");  // Root: [10]
tree.insert(20, "data20");  // Root: [10, 20]
tree.insert(5, "data5");    // Root: [5, 10, 20]
tree.insert(15, "data15");  // Root: [5, 10, 15, 20] - Now FULL!

// Insert 30 causes a split:
//          [15]
//         /    \
//    [5, 10]  [20, 30]
```

**Time Complexity Analysis:**
- Search: O(log_m n) where m is the order
- Insert: O(log_m n)
- Delete: O(log_m n)

For 1 million records with order 100:
- log₁₀₀(1,000,000) ≈ 3 disk reads

### 2. Page (Fixed-Size Block)

**File**: `src/main/java/com/simpledb/storage/Page.java`

#### What is a Page?

A page is a fixed-size block of memory (typically 4KB - 16KB) that serves as the fundamental unit of I/O.

#### Why Pages?

```
Without Pages:                With Pages:
Read 1 record = 1 disk I/O   Read 1 page = Many records

Record 1 ─→ 1 I/O            ┌─────────────┐
Record 2 ─→ 1 I/O            │  Record 1   │
Record 3 ─→ 1 I/O            │  Record 2   │  ← 1 I/O gets all!
Record 4 ─→ 1 I/O            │  Record 3   │
                             │  Record 4   │
Total: 4 I/O                 └─────────────┘
                             Total: 1 I/O
```

#### Page Structure

```
Page (4096 bytes)
├── Byte 0-3:    Record size
├── Byte 4-N:    Record data
├── Byte N+1:    Next record size
├── ...
└── Byte 4096:   End of page
```

**Key Concepts:**
1. **Fixed size**: Makes address calculation easy
2. **Aligned to disk blocks**: Minimizes I/O overhead
3. **Unit of caching**: Entire pages are cached in memory

### 3. HashMap (for Caching)

**Used in**: `PageManager.java` and `Database.java`

#### What is a HashMap?

A HashMap uses a hash function to compute an index into an array of buckets.

```
Hash Function: key → hash(key) % array_size → index

Key: "user_123"
Hash: 42
Index: 42 % 16 = 10

Array:
[0] → null
[1] → null
...
[10] → (user_123, <page data>)  ← O(1) access!
...
[15] → null
```

**Why HashMap for Page Cache?**
- **Fast lookup**: O(1) average time
- **Fast insert**: O(1) average time
- **Perfect for cache**: Quick check if page is in memory

#### Cache Example

```java
Map<Integer, Page> pageCache = new HashMap<>();

// Check if page is cached (O(1))
if (pageCache.containsKey(pageId)) {
    return pageCache.get(pageId);  // Fast!
}

// Load from disk (slow)
Page page = loadFromDisk(pageId);
pageCache.put(pageId, page);  // Cache it (O(1))
```

### 4. Arrays (for Page Data)

**Used in**: `Page.java`

#### Why Arrays?

Arrays provide:
1. **Constant-time access**: `array[index]` is O(1)
2. **Contiguous memory**: Better cache performance
3. **Fixed size**: Predictable memory usage

```java
byte[] data = new byte[4096];  // 4KB page

// Write data at offset (O(1))
System.arraycopy(source, 0, data, offset, length);

// Read data at offset (O(1))
System.arraycopy(data, offset, dest, 0, length);
```

---

## Algorithms Explained

### 1. B-Tree Search Algorithm

```java
public Object search(int key) {
    return searchNode(root, key);
}

private Object searchNode(BTreeNode node, int key) {
    // Binary search within node
    int index = node.search(key);  // O(log m) within node

    if (index != -1) {
        return node.getValues().get(index);  // Found!
    }

    if (node.isLeaf()) {
        return null;  // Not found
    }

    // Recurse to appropriate child
    int childIndex = node.findChildIndex(key);
    return searchNode(node.getChildren().get(childIndex), key);
}
```

**Complexity**: O(log_m n) where m = order, n = total keys

### 2. B-Tree Insertion Algorithm

Steps:
1. **Find leaf** where key should go (recursive search)
2. **Insert in leaf** if there's space
3. **Split node** if full:
   - Take middle key
   - Promote to parent
   - Split children

```java
public void insert(int key, Object value) {
    if (root.isFull()) {
        // Split root - tree grows in height
        BTreeNode newRoot = new BTreeNode(false);
        newRoot.getChildren().add(root);
        newRoot.splitChild(0);
        root = newRoot;
    }
    root.insertNonFull(key, value);
}
```

### 3. Page Lookup Algorithm

```java
public Page getPage(int pageId) {
    // 1. Check cache first (O(1))
    if (pageCache.containsKey(pageId)) {
        return pageCache.get(pageId);  // Cache hit!
    }

    // 2. Cache miss - load from disk
    Page page = loadPageFromDisk(pageId);

    // 3. Add to cache
    pageCache.put(pageId, page);

    return page;
}
```

**Complexity**:
- Best case (cache hit): O(1)
- Worst case (cache miss): O(1) + disk I/O

### 4. Sequential Scan Algorithm

```java
public List<Record> selectAll() {
    List<Record> results = new ArrayList<>();

    // Scan all pages
    for (int pageId = 0; pageId < totalPages; pageId++) {
        Page page = getPage(pageId);

        // Extract all records from page
        for (Record record : extractRecords(page)) {
            results.add(record);
        }
    }

    return results;
}
```

**Complexity**: O(n) where n = number of records

---

## Step-by-Step Code Walkthrough

### Example: Creating and Querying a Table

Let's trace what happens when you run:

```sql
CREATE TABLE users (id, name, email)
INSERT INTO users VALUES (1, Alice, alice@email.com)
SELECT * FROM users WHERE id = 1
```

#### Step 1: CREATE TABLE

```
SimpleQueryParser.execute("CREATE TABLE users...")
    ↓
Database.createTable("users", ["id", "name", "email"])
    ↓
new Table("users", columns, "./db_data")
    ↓
new PageManager("./db_data/users.dat")
    ↓
new BTree()  // For primary key index
```

**Result**: Table structure created in memory, ready to store data.

#### Step 2: INSERT INTO

```
SimpleQueryParser.execute("INSERT INTO users...")
    ↓
Table.insert(record)
    ↓
1. Serialize record to bytes
2. PageManager.allocatePage()  // Get new page
3. Page.writeAtOffset(0, recordData)  // Write to page
4. BTree.insert(1, "pageId:offset")  // Index it!
5. PageManager.writePage(page)  // Flush to disk
```

**Physical Storage**:
```
users.dat:
[Page 0: 4096 bytes]
├── Bytes 0-3:   Size = 156
├── Bytes 4-159: Serialized Record(id=1, name=Alice, ...)
└── Bytes 160+:  Free space

users.meta:
nextRecordId=2
columns=id,name,email
```

**B-Tree Index**:
```
root: [1 → "0:4"]
      (key 1 maps to page 0, offset 4)
```

#### Step 3: SELECT WHERE id = 1

```
SimpleQueryParser.execute("SELECT * FROM users WHERE id = 1")
    ↓
Table.selectById(1)
    ↓
1. BTree.search(1)  // Returns "0:4"
   - Search root node (found: key=1, value="0:4")
   - O(log n) operation
2. Parse location: pageId=0, offset=4
3. PageManager.getPage(0)
   - Check cache (miss)
   - Read from disk
   - Cache the page
4. Page.readFromOffset(4, recordSize)
5. Record.fromBytes(data)
    ↓
Return Record(id=1, name=Alice, email=alice@email.com)
```

**Performance**:
- Without index: O(n) - scan all records
- With B-Tree: O(log n) + 1 disk read

---

## Exercises and Challenges

### Beginner Exercises

1. **Trace B-Tree Operations**
   - Insert keys: 5, 15, 25, 35, 45
   - Draw the B-Tree structure after each insertion
   - Identify when splits occur

2. **Calculate Time Complexity**
   - How many comparisons to find key 45 in a B-Tree of 1000 keys (order=10)?
   - Compare with binary search tree

3. **Understand Page Layout**
   - Design a page layout for storing records of 200 bytes each
   - How many records fit in a 4KB page?

### Intermediate Exercises

1. **Implement Range Query**
   - Add method: `List<Record> selectRange(int minId, int maxId)`
   - Use B-Tree to find start point
   - Traverse to get all records in range

2. **Add Update Functionality**
   - Implement: `UPDATE users SET name = 'Bob' WHERE id = 1`
   - Find record using index
   - Update in place or delete + insert?

3. **Implement LRU Cache**
   - Replace HashMap with LRU cache in PageManager
   - Evict least recently used page when cache is full

### Advanced Challenges

1. **Secondary Index**
   - Add index on non-primary key (e.g., email)
   - Handle non-unique keys
   - Implement: `SELECT * FROM users WHERE email = 'alice@email.com'`

2. **JOIN Operation**
   - Implement: `SELECT * FROM orders JOIN users ON orders.user_id = users.id`
   - Use nested loop join algorithm
   - Calculate time complexity

3. **Transaction Support**
   - Add BEGIN, COMMIT, ROLLBACK
   - Implement write-ahead logging (WAL)
   - Ensure ACID properties

4. **Query Optimization**
   - Implement cost-based optimizer
   - Choose between index scan vs sequential scan
   - Optimize multi-condition WHERE clauses

---

## Key Takeaways

### Data Structure Choices

| Use Case | Data Structure | Why? |
|----------|---------------|------|
| Fast lookups | B-Tree | O(log n), disk-friendly |
| In-memory cache | HashMap | O(1) average |
| Fixed-size storage | Array | O(1) access, contiguous |
| Dynamic lists | ArrayList | O(1) append, resizable |

### Algorithm Patterns

1. **Divide and Conquer**: B-Tree search divides problem in half
2. **Caching**: Store frequently accessed data in memory
3. **Lazy Loading**: Load pages only when needed
4. **Buffer Management**: Keep hot pages in memory

### Performance Trade-offs

```
Operation          | Without Index | With B-Tree | Memory Cost
-------------------|---------------|-------------|-------------
Insert             | O(1)          | O(log n)    | +O(log n) space
Search by key      | O(n)          | O(log n)    | +O(log n) space
Range query        | O(n)          | O(log n + k)| +O(log n) space
Full scan          | O(n)          | O(n)        | Same
```

### Real-World Databases

This project simplifies many concepts. Real databases add:
- **Concurrency control**: Multiple users accessing simultaneously
- **ACID transactions**: Atomicity, Consistency, Isolation, Durability
- **Query optimization**: Choose best execution plan
- **Crash recovery**: Recover from failures
- **Distributed storage**: Data across multiple machines

---

## Further Reading

### Books
1. "Database System Concepts" - Comprehensive theory
2. "Database Internals" - Modern implementation details
3. "Introduction to Algorithms" (CLRS) - Algorithm analysis

### Online Courses
1. [CMU 15-445: Database Systems](https://15445.courses.cs.cmu.edu/)
2. [MIT 6.824: Distributed Systems](https://pdos.csail.mit.edu/6.824/)

### Practice
1. Modify this codebase
2. Build your own features
3. Compare with real databases (SQLite, PostgreSQL)
4. Profile performance

---

**Remember**: The best way to learn is by doing! Try breaking things, fixing them, and understanding why they work the way they do.
