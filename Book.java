package com.example.library;

import java.util.Objects;

public abstract class Book {
    private final String id;
    private String title;
    private String author;
    private int copies;

    public Book(String id, String title, String author, int copies) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id required");
        this.id = id;
        this.title = title;
        this.author = author;
        this.copies = copies;
    }

    public String getId() {
        return id; 
    }
    
    public String getTitle() { 
        return title; 
    }
    
    public String getAuthor() {
        return author; 
    }
    
    public int getCopies() { 
        return copies; 
    }

    public void setTitle(String title) { 
        this.title = title; 
    }
    
    public void setAuthor(String author) { 
        this.author = author; 
    }
    
    public void setCopies(int copies) { 
        this.copies = copies; 
    }
    

    public boolean isAvailable() { 
        return copies > 0; 
    }
    

    public abstract String getType(); // "EBOOK" or "PRINTED"

    public abstract String getExtra(); // downloadUrl or shelf

    public abstract void setExtra(String extra);
    

    @Override
    public String toString() {
        return id + " - " + title + " by " + author + " (" + copies + " copies)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
