package com.example.library;

import com.example.library.gui.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception ignored){}

        // Initialize DB
        DatabaseManager db = DatabaseManager.getInstance();
        try {
            db.initDatabase();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database init failed: " + e.getMessage());
            return;
        }

        // Create library and load from DB
        Library<Book> library = new Library<>(db);
        try {
            library.loadAll();
        } catch (LibraryException e) {
            System.out.println("Warning: couldn't load library: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(library);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
