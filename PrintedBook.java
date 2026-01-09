package com.example.library;

public class PrintedBook extends Book {
    private String shelf;

    public PrintedBook(String id, String title, String author, int copies, String shelf) {
        super(id, title, author, copies);
        this.shelf = shelf;
    }

    @Override
    public String getType() { 
        return "PRINTED"; 
    }
    

    @Override
    public String getExtra() {
        return shelf; 
    }
    

    @Override
    public void setExtra(String extra) {
        this.shelf = extra; 
    }
    
}
