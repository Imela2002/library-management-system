package com.example.library;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple singleton DatabaseManager for SQLite.
 * Creates `books` table automatically.
 *
 * Table schema:
 * id TEXT PRIMARY KEY,
 * title TEXT,
 * author TEXT,
 * copies INTEGER,
 * type TEXT, -- EBOOK or PRINTED
 * extra TEXT  -- downloadUrl or shelf
 */

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static DatabaseManager instance;

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

public void initDatabase() throws SQLException {
    try (Connection c = getConnection(); Statement s = c.createStatement()) {
        s.executeUpdate(
            "CREATE TABLE IF NOT EXISTS books (" +
            "id TEXT PRIMARY KEY, " +
            "title TEXT NOT NULL, " +
            "author TEXT NOT NULL, " +
            "copies INTEGER NOT NULL, " +
            "type TEXT NOT NULL, " +
            "extra TEXT" +
            ")"
        );
    }
}

public void insertBook(Book b) throws SQLException {
    String sql = "INSERT INTO books(id,title,author,copies,type,extra) VALUES(?,?,?,?,?,?)";
    try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
        p.setString(1, b.getId());
        p.setString(2, b.getTitle());
        p.setString(3, b.getAuthor());
        p.setInt(4, b.getCopies());
        p.setString(5, b.getType());
        p.setString(6, b.getExtra());
        p.executeUpdate();
    }
}

    public void updateBook(Book b) throws SQLException {
        String sql = "UPDATE books SET title=?,author=?,copies=?,type=?,extra=? WHERE id=?";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, b.getTitle());
            p.setString(2, b.getAuthor());
            p.setInt(3, b.getCopies());
            p.setString(4, b.getType());
            p.setString(5, b.getExtra());
            p.setString(6, b.getId());
            p.executeUpdate();
        }
    }

    public void deleteBook(String id) throws SQLException {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            p.executeUpdate();
        }
    }

    public List<Book> findAllBooks() throws SQLException {
        String sql = "SELECT id,title,author,copies,type,extra FROM books ORDER BY title";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet rs = p.executeQuery()) {
            List<Book> out = new ArrayList<>();
            while (rs.next()) {
                out.add(readBook(rs));
            }
            return out;
        }
    }

    public List<Book> searchBooks(String q) throws SQLException {
        String sql = "SELECT id,title,author,copies,type,extra FROM books WHERE title LIKE ? OR author LIKE ? ORDER BY title";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            String like = "%" + q + "%";
            p.setString(1, like);
            p.setString(2, like);
            try (ResultSet rs = p.executeQuery()) {
                List<Book> out = new ArrayList<>();
                while (rs.next()) out.add(readBook(rs));
                return out;
            }
        }
    }

    private Book readBook(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        int copies = rs.getInt("copies");
        String type = rs.getString("type");
        String extra = rs.getString("extra");
        if ("EBOOK".equals(type)) {
            return new EBook(id, title, author, copies, extra);
        } else {
            return new PrintedBook(id, title, author, copies, extra);
        }
    }
}
