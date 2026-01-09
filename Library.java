package com.example.library;

import java.util.*;

public class Library<T extends Book> {
    private final Map<String, T> books = new LinkedHashMap<>();
    private final DatabaseManager db;

    public Library(DatabaseManager db) {
        this.db = db;
    }

    public void addBook(T book) throws LibraryException {
        if (books.containsKey(book.getId())) throw new LibraryException("Book id exists: " + book.getId());
        try {
            db.insertBook(book);
            books.put(book.getId(), book);
        } catch (Exception e) {
            throw new LibraryException(e.getMessage());
        }
    }

    public void updateBook(T book) throws LibraryException {
        if (!books.containsKey(book.getId())) throw new LibraryException("Book not found: " + book.getId());
        try {
            db.updateBook(book);
            books.put(book.getId(), book);
        } catch (Exception e) {
            throw new LibraryException(e.getMessage());
        }
    }

    public void remove(String id) throws LibraryException {
        if (!books.containsKey(id)) throw new LibraryException("Book not found: " + id);
        try {
            db.deleteBook(id);
            books.remove(id);
        } catch (Exception e) {
            throw new LibraryException(e.getMessage());
        }
    }

    public Optional<T> getById(String id) {
        return Optional.ofNullable(books.get(id));
    }

    public Collection<T> getAll() { return Collections.unmodifiableCollection(books.values()); }

    public void borrow(String id) throws LibraryException {
        T b = books.get(id);
        if (b == null) throw new LibraryException("Book not found");
        if (b.getCopies() <= 0) throw new LibraryException("No copies available");
        b.setCopies(b.getCopies() - 1);
        try { db.updateBook(b); } catch (Exception e) { throw new LibraryException(e.getMessage()); }
    }

    public void returnBook(String id) throws LibraryException {
        T b = books.get(id);
        if (b == null) throw new LibraryException("Book not found");
        b.setCopies(b.getCopies() + 1);
        try { db.updateBook(b); } catch (Exception e) { throw new LibraryException(e.getMessage()); }
    }

    public void clear() {
        books.clear();
    }

    public void loadAll() throws LibraryException {
        try {
            List<Book> list = db.findAllBooks();
            books.clear();
            for (Book b : list) {
                @SuppressWarnings("unchecked")
                T t = (T) b;
                books.put(t.getId(), t);
            }
        } catch (Exception e) {
            throw new LibraryException(e.getMessage());
        }
    }

    public List<T> search(String q) throws LibraryException {
        try {
            List<Book> list = db.searchBooks(q);
            List<T> out = new ArrayList<>();
            for (Book b : list) {
                @SuppressWarnings("unchecked")
                T t = (T) b;
                out.add(t);
            }
            return out;
        } catch (Exception e) {
            throw new LibraryException(e.getMessage());
        }
    }
}
