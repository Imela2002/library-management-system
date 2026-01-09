package com.example.library;

public class EBook extends Book {
    private String downloadUrl;

    public EBook(String id, String title, String author, int copies, String downloadUrl) {
        super(id, title, author, copies);
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String getType() {
        return "EBOOK"; 
    }
    

    @Override
    public String getExtra() {
        return downloadUrl; 
    }
    

    @Override
    public void setExtra(String extra) {
        this.downloadUrl = extra; 
    }
    
}
