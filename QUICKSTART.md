# Quick Start Examples

This file contains ready-to-run examples to help you understand how SimpleDB works.

## Example 1: Your First Database

### Step 1: Compile the project

```bash
# On Linux/Mac
./compile.sh

# On Windows
compile.bat
```

### Step 2: Run the demo

```bash
# On Linux/Mac
./run-demo.sh

# On Windows
Not available yet, but you can run:
java -cp bin com.simpledb.examples.DataStructuresDemo
```

### What you'll see:

1. **Page Storage Demo**: How data is stored in fixed-size blocks
2. **B-Tree Demo**: How indexes organize data for fast lookups
3. **Complexity Analysis**: Why some operations are faster than others

---

## Example 2: Interactive Database

### Run the interactive mode:

```bash
# On Linux/Mac
./run.sh

# On Windows
run.bat
```

### Try these queries:

```sql
-- Create a table
CREATE TABLE products (id, name, price, category)

-- Insert some products
INSERT INTO products VALUES (1, Laptop, 999.99, Electronics)
INSERT INTO products VALUES (2, Mouse, 29.99, Electronics)
INSERT INTO products VALUES (3, Desk, 299.99, Furniture)
INSERT INTO products VALUES (4, Chair, 199.99, Furniture)

-- View all products
SELECT * FROM products

-- Find specific product by ID (uses B-Tree index - fast!)
SELECT * FROM products WHERE id = 2

-- Find by category (table scan - slower)
SELECT * FROM products WHERE category = Electronics

-- Exit
exit
```

---

## Example 3: Understanding B-Tree Performance

Create a simple Java program to test B-Tree performance:

```java
import com.simpledb.index.BTree;

public class BTreePerformanceTest {
    public static void main(String[] args) {
        BTree tree = new BTree();

        // Insert 1000 keys
        long startInsert = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            tree.insert(i, "Value " + i);
        }
        long endInsert = System.nanoTime();

        // Search for keys
        long startSearch = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            Object result = tree.search(i);
        }
        long endSearch = System.nanoTime();

        System.out.println("Insert time: " + (endInsert - startInsert) / 1000000.0 + " ms");
        System.out.println("Search time: " + (endSearch - startSearch) / 1000000.0 + " ms");
        System.out.println("Average search time per key: " +
                          (endSearch - startSearch) / 1000000.0 / 1000 + " ms");
    }
}
```

Expected output:
```
Insert time: ~5-10 ms
Search time: ~2-5 ms
Average search time per key: ~0.002-0.005 ms
```

This demonstrates O(log n) performance!

---

## Example 4: Comparing Indexed vs Non-Indexed Search

```java
import com.simpledb.query.Database;
import com.simpledb.table.Table;
import com.simpledb.table.Record;
import java.util.Arrays;

public class IndexComparisonTest {
    public static void main(String[] args) {
        Database db = new Database("./test_db");
        db.createTable("users", Arrays.asList("id", "name", "email"));

        Table table = db.getTable("users");

        // Insert 100 records
        System.out.println("Inserting 100 records...");
        for (int i = 1; i <= 100; i++) {
            Record r = new Record(i);
            r.setField("name", "User" + i);
            r.setField("email", "user" + i + "@email.com");
            table.insert(r);
        }

        // Search by ID (uses B-Tree index)
        long start1 = System.nanoTime();
        Record r1 = table.selectById(50);
        long end1 = System.nanoTime();
        System.out.println("\nIndexed search (by ID): " +
                          (end1 - start1) / 1000.0 + " microseconds");
        System.out.println("Result: " + r1);

        // Search by scanning all records
        long start2 = System.nanoTime();
        var allRecords = table.selectAll();
        Record r2 = null;
        for (Record r : allRecords) {
            if (r.getField("name").equals("User50")) {
                r2 = r;
                break;
            }
        }
        long end2 = System.nanoTime();
        System.out.println("\nSequential scan (by name): " +
                          (end2 - start2) / 1000.0 + " microseconds");
        System.out.println("Result: " + r2);

        System.out.println("\nSpeedup: " +
                          ((end2 - start2) / (double)(end1 - start1)) + "x faster with index!");

        db.close();
    }
}
```

Expected output:
```
Inserting 100 records...

Indexed search (by ID): ~50-100 microseconds
Result: Record{id=50, fields={name=User50, email=user50@email.com}}

Sequential scan (by name): ~500-1000 microseconds
Result: Record{id=50, fields={name=User50, email=user50@email.com}}

Speedup: 5-10x faster with index!
```

---

## Example 5: Understanding Page-Based Storage

```java
import com.simpledb.storage.Page;
import com.simpledb.storage.PageManager;

public class PageStorageDemo {
    public static void main(String[] args) {
        System.out.println("=== Page Storage Demo ===\n");

        // Create a page
        Page page = new Page(0);
        System.out.println("Page size: " + Page.getPageSize() + " bytes");

        // Write multiple records to the same page
        String[] records = {
            "Record 1: Alice, alice@email.com",
            "Record 2: Bob, bob@email.com",
            "Record 3: Charlie, charlie@email.com"
        };

        int offset = 0;
        for (String record : records) {
            byte[] data = record.getBytes();
            page.writeAtOffset(offset, data);
            System.out.println("Written: " + record + " at offset " + offset);
            offset += data.length + 1;  // +1 for separation
        }

        System.out.println("\nPage is dirty: " + page.isDirty());

        // Read back the data
        System.out.println("\nReading data back:");
        offset = 0;
        for (String record : records) {
            byte[] readData = page.readFromOffset(offset, record.getBytes().length);
            System.out.println("Read: " + new String(readData));
            offset += record.getBytes().length + 1;
        }

        // Calculate storage efficiency
        int totalDataSize = 0;
        for (String record : records) {
            totalDataSize += record.getBytes().length;
        }
        double efficiency = (totalDataSize / (double)Page.getPageSize()) * 100;
        System.out.println("\nStorage efficiency: " +
                          String.format("%.2f", efficiency) + "%");
        System.out.println("Used: " + totalDataSize + " bytes");
        System.out.println("Free: " + (Page.getPageSize() - totalDataSize) + " bytes");
    }
}
```

Expected output:
```
=== Page Storage Demo ===

Page size: 4096 bytes
Written: Record 1: Alice, alice@email.com at offset 0
Written: Record 2: Bob, bob@email.com at offset 34
Written: Record 3: Charlie, charlie@email.com at offset 64

Page is dirty: true

Reading data back:
Read: Record 1: Alice, alice@email.com
Read: Record 2: Bob, bob@email.com
Read: Record 3: Charlie, charlie@email.com

Storage efficiency: 2.39%
Used: 98 bytes
Free: 3998 bytes
```

This shows how multiple records fit in one page!

---

## Example 6: Create a Student Database

A practical example you can type into the interactive mode:

```sql
-- Create students table
CREATE TABLE students (id, name, major, gpa)

-- Insert students
INSERT INTO students VALUES (1, Alice, Computer Science, 3.8)
INSERT INTO students VALUES (2, Bob, Mathematics, 3.6)
INSERT INTO students VALUES (3, Charlie, Physics, 3.9)
INSERT INTO students VALUES (4, Diana, Computer Science, 3.7)
INSERT INTO students VALUES (5, Eve, Mathematics, 4.0)

-- View all students
SELECT * FROM students

-- Find student by ID (fast!)
SELECT * FROM students WHERE id = 3

-- Find by major (slower, but works!)
SELECT * FROM students WHERE major = Computer Science

-- Create courses table
CREATE TABLE courses (id, name, credits, instructor)

-- Insert courses
INSERT INTO courses VALUES (1, Database Systems, 3, Dr. Smith)
INSERT INTO courses VALUES (2, Algorithms, 4, Dr. Jones)
INSERT INTO courses VALUES (3, Operating Systems, 3, Dr. Smith)

-- View all courses
SELECT * FROM courses
```

---

## Example 7: Experiment with B-Tree Visualization

```java
import com.simpledb.index.BTree;

public class BTreeVisualizationDemo {
    public static void main(String[] args) {
        BTree tree = new BTree();

        System.out.println("=== B-Tree Insertion Visualization ===\n");

        int[] keys = {50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45};

        for (int key : keys) {
            System.out.println("Inserting key: " + key);
            tree.insert(key, "Value-" + key);
            tree.traverse();
            System.out.println("---");
        }

        System.out.println("\nSearching for keys:");
        for (int key : new int[]{35, 70, 100}) {
            Object result = tree.search(key);
            System.out.println("Key " + key + ": " +
                             (result != null ? result : "Not found"));
        }
    }
}
```

---

## Troubleshooting

### Compilation fails

**Error**: `javac: command not found`

**Solution**: Install JDK:
- Ubuntu/Debian: `sudo apt install openjdk-11-jdk`
- Mac: `brew install openjdk@11`
- Windows: Download from [Oracle](https://www.oracle.com/java/technologies/downloads/)

### Can't run scripts on Linux/Mac

**Error**: `Permission denied`

**Solution**: Make scripts executable:
```bash
chmod +x compile.sh run.sh run-demo.sh
```

### Database files already exist

**Solution**: Clean up old data:
```bash
rm -rf db_data/ test_db/
```

---

## Next Steps

1. **Read the LEARNING_GUIDE.md** for in-depth explanations
2. **Try the exercises** in the learning guide
3. **Modify the code** to add new features
4. **Compare with real databases** like SQLite

Happy Learning!
