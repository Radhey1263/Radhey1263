# SimpleDB - Educational Database System

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=java&logoColor=white)
![Data Structures](https://img.shields.io/badge/Data%20Structures-Algorithms-blue)
![Educational](https://img.shields.io/badge/Purpose-Educational-green)

A simplified database management system built from scratch in Java to demonstrate how databases work internally using fundamental data structures and algorithms. This project is perfect for learning about database internals, data structures, and algorithms.

## 🎯 Project Overview

This project implements a functional database system that demonstrates:

- **B-Tree Indexing** for efficient data retrieval
- **Page-based Storage** management similar to real databases
- **Query Parsing** and execution
- **Table and Record** management
- **Buffer Pool** concepts

## 📚 Data Structures & Algorithms Used

### 1. **B-Tree (index/BTree.java)**
- **Purpose**: Fast data lookup and range queries
- **Time Complexity**:
  - Search: O(log n)
  - Insert: O(log n)
  - Delete: O(log n)
- **Why B-Trees?**:
  - Minimizes disk I/O by storing multiple keys per node
  - Self-balancing structure
  - Optimized for systems that read/write large blocks of data
  - Used in real databases like MySQL, PostgreSQL, SQLite

### 2. **Page Structure (storage/Page.java)**
- **Purpose**: Fixed-size blocks for data storage
- **Data Structure**: Byte array (4KB size)
- **Concept**: Similar to OS virtual memory pages
- **Why Pages?**:
  - Matches disk block size for efficient I/O
  - Simplifies buffer pool management
  - Enables efficient caching

### 3. **HashMap (for caching and table lookup)**
- **Purpose**: In-memory cache for frequently accessed pages
- **Time Complexity**: O(1) average case
- **Usage**:
  - Page cache in PageManager
  - Table lookup in Database class

### 4. **Sequential Scan**
- **Purpose**: Full table scans when no index is available
- **Time Complexity**: O(n)
- **Algorithm**: Linear search through all pages and records

## 🏗️ Architecture

```
SimpleDB
├── storage/           # Storage layer
│   ├── Page.java         # 4KB fixed-size data blocks
│   └── PageManager.java  # Manages pages and disk I/O
├── index/             # Indexing layer
│   ├── BTree.java        # B-Tree implementation
│   └── BTreeNode.java    # B-Tree node structure
├── table/             # Table management
│   ├── Record.java       # Row representation
│   └── Table.java        # Table with schema and data
├── query/             # Query layer
│   ├── Database.java     # Database manager
│   └── SimpleQueryParser.java  # SQL parser
└── examples/          # Example programs
    ├── Main.java         # Interactive database demo
    └── DataStructuresDemo.java  # DS&A demonstrations
```

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- A terminal or command prompt

### Compilation

**On Linux/Mac:**
```bash
chmod +x compile.sh
./compile.sh
```

**On Windows:**
```cmd
compile.bat
```

### Running the Database

**Run the main interactive demo:**

Linux/Mac:
```bash
chmod +x run.sh
./run.sh
```

Windows:
```cmd
run.bat
```

**Run the data structures demo:**

Linux/Mac:
```bash
chmod +x run-demo.sh
./run-demo.sh
```

## 💡 Usage Examples

### SQL-like Queries

The database supports basic SQL-like syntax:

```sql
-- Create a table
CREATE TABLE users (id, name, email, age)

-- Insert records
INSERT INTO users VALUES (1, Alice, alice@email.com, 25)
INSERT INTO users VALUES (2, Bob, bob@email.com, 30)

-- Select all records
SELECT * FROM users

-- Select by ID (uses B-Tree index)
SELECT * FROM users WHERE id = 1

-- Select by other fields (table scan)
SELECT * FROM users WHERE name = Alice
```

### Java API Usage

```java
// Create a database
Database db = new Database("./db_data");

// Create a table
List<String> columns = Arrays.asList("id", "name", "email");
db.createTable("users", columns);

// Get table and insert record
Table users = db.getTable("users");
Record record = new Record(1);
record.setField("name", "Alice");
record.setField("email", "alice@email.com");
users.insert(record);

// Query by ID (fast B-Tree lookup)
Record result = users.selectById(1);

// Query all records
List<Record> allRecords = users.selectAll();

// Close database
db.close();
```

## 🎓 Learning Objectives

By studying this code, you'll learn:

1. **How Databases Store Data**
   - Page-based storage organization
   - Serialization and deserialization
   - File I/O operations

2. **How Indexing Works**
   - B-Tree structure and operations
   - Why indexes make queries faster
   - Trade-offs: speed vs space

3. **How Queries Are Processed**
   - Query parsing and tokenization
   - Query execution plans
   - Index-based vs full table scans

4. **Data Structures in Action**
   - B-Trees for indexing
   - HashMaps for caching
   - Arrays for fixed-size storage
   - Lists for dynamic collections

5. **Algorithm Complexity**
   - Understanding O(log n) vs O(n)
   - When to use which data structure
   - Performance optimization techniques

## 🔍 Key Concepts Explained

### Why B-Trees Over Binary Search Trees?

| Feature | Binary Search Tree | B-Tree |
|---------|-------------------|---------|
| Height | O(log₂ n) | O(log_m n) where m >> 2 |
| Disk I/O | Many small reads | Few large reads |
| Balance | May become unbalanced | Always balanced |
| Node size | 1 key | Multiple keys (m-1) |
| Best for | In-memory data | Disk-based data |

**Example**: For 1 million records:
- BST: ~20 disk reads (log₂ 1,000,000 ≈ 20)
- B-Tree (order=100): ~3 disk reads (log₁₀₀ 1,000,000 ≈ 3)

### Page-Based Storage

Real databases don't read individual records—they read entire pages (typically 4KB-16KB):

```
Disk Block = Page = 4096 bytes
├── Header (metadata)
├── Record 1
├── Record 2
├── Record 3
└── Free space
```

**Benefits:**
- Matches OS and disk block sizes
- Reduces number of I/O operations
- Enables efficient buffer caching

### Buffer Pool (Simplified)

Our PageManager implements a simple buffer pool:

```java
Map<Integer, Page> pageCache; // In-memory cache
```

Real databases use LRU (Least Recently Used) or Clock algorithms for page replacement.

## 🧪 Testing the Implementation

### Test B-Tree Operations

```java
BTree tree = new BTree();

// Insert keys
tree.insert(10, "ten");
tree.insert(20, "twenty");
tree.insert(5, "five");

// Search
Object value = tree.search(10); // Returns "ten" in O(log n)

// Check existence
boolean exists = tree.contains(20); // Returns true
```

### Test Storage Layer

```java
Page page = new Page(0);
byte[] data = "Hello Database".getBytes();

// Write
page.writeAtOffset(0, data);

// Read
byte[] read = page.readFromOffset(0, data.length);
```

## 📊 Performance Characteristics

| Operation | Without Index | With B-Tree Index |
|-----------|--------------|-------------------|
| Insert | O(1) | O(log n) |
| Search by ID | O(n) | O(log n) |
| Range Query | O(n) | O(log n + k) |
| Full Scan | O(n) | O(n) |

*where n = number of records, k = number of results*

## 🛠️ Extending the Project

Ideas for additional features:

1. **Advanced Indexing**
   - Hash indexes for equality searches
   - Composite indexes (multiple columns)
   - Full-text search indexes

2. **Query Optimization**
   - Query planner
   - Cost-based optimization
   - Join operations

3. **Transaction Support**
   - ACID properties
   - Locking mechanisms
   - Write-ahead logging (WAL)

4. **Advanced Storage**
   - LRU cache replacement
   - Compression
   - Variable-length records

5. **More SQL Features**
   - UPDATE and DELETE statements
   - JOIN operations
   - Aggregation (COUNT, SUM, AVG)
   - ORDER BY and GROUP BY

## 📖 Resources for Further Learning

- **Books**:
  - "Database System Concepts" by Silberschatz, Korth, Sudarshan
  - "Database Internals" by Alex Petrov
  - "Introduction to Algorithms" by CLRS

- **Online Resources**:
  - [CMU Database Course](https://15445.courses.cs.cmu.edu/)
  - [SQLite Architecture](https://www.sqlite.org/arch.html)
  - [PostgreSQL Internals](https://www.postgresql.org/docs/current/internals.html)

## 🤝 Contributing

This is an educational project. Feel free to:
- Add more data structures (Hash tables, Skip lists)
- Implement additional algorithms
- Add more SQL features
- Improve documentation
- Add unit tests

## 📝 License

This project is open source and available for educational purposes.

## 🙏 Acknowledgments

This project was created to help learn:
- How databases work internally
- Practical applications of data structures
- Algorithm design and analysis
- Java programming

## 📧 Contact

For questions or suggestions about this educational project, please open an issue on GitHub.

---

**Happy Learning! 🎓**

Remember: The best way to learn is by doing. Try modifying the code, adding features, and breaking things to see how they work!
