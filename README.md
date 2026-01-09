# library-management-system
Data Management System application

Features:
- Maven project with sqlite-jdbc dependency.
- SQLite database `library.db` created automatically.
- Book hierarchy: `Book` (abstract), `EBook`, `PrintedBook`.
- Generic `Library<T extends Book>` using `DatabaseManager` for persistence.
- Exceptions for error handling.
- Swing GUI with full features: add, edit, delete, search, borrow, return.
- Double-click row to view subclass-specific field (demonstrates casting).

## How to build & run
Requirements: Java 17+, Maven installed.

From project root:
```
mvn compile
mvn exec:java
```
or single command:
```
mvn compile exec:java -Dexec.mainClass="com.example.library.Main"
```

The application will create `library.db` in the project root and the `books` table if missing.

