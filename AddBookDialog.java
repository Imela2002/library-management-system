package com.example.library.gui;

import com.example.library.*;
import javax.swing.*;
import java.awt.*;
import java.util.UUID;

/**
 * Dialog used both for adding and editing books.
 * Demonstrates creating subclasses based on selected type.
 */

public class AddBookDialog extends JDialog {
    private boolean ok = false;
    private Book book;

    private final JTextField titleF = new JTextField(30);
    private final JTextField authorF = new JTextField(30);
    private final JTextField copiesF = new JTextField("1",5);
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"EBOOK","PRINTED"});
    private final JTextField extraF = new JTextField(30);

    public AddBookDialog(Frame owner) {
        super(owner, "Add Book", true);
        init(null);
    }

    public AddBookDialog(Frame owner, Book existing) {
        super(owner, "Edit Book", true);
        init(existing);
    }

    private void init(Book existing) {
        setLayout(new BorderLayout());
        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p.add(new JLabel("Title:")); p.add(titleF);
        p.add(new JLabel("Author:")); p.add(authorF);
        p.add(new JLabel("Copies:")); p.add(copiesF);
        p.add(new JLabel("Type:")); p.add(typeBox);
        p.add(new JLabel("Extra (URL or Shelf):")); p.add(extraF);
        add(p, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");
        btns.add(okBtn); btns.add(cancelBtn);
        add(btns, BorderLayout.SOUTH);

        if (existing != null) {
            titleF.setText(existing.getTitle());
            authorF.setText(existing.getAuthor());
            copiesF.setText(String.valueOf(existing.getCopies()));
            typeBox.setSelectedItem(existing.getType());
            extraF.setText(existing.getExtra());
            // keep the same id by creating book later with same id
            this.book = existing;
        }

        okBtn.addActionListener(e -> {
            try {
                String title = titleF.getText().trim();
                String author = authorF.getText().trim();
                int copies = Integer.parseInt(copiesF.getText().trim());
                String type = (String) typeBox.getSelectedItem();
                String extra = extraF.getText().trim();
                if (title.isBlank() || author.isBlank()) throw new IllegalArgumentException("Title and author required");
                if (existing == null) {
                    String id = UUID.randomUUID().toString();
                    if ("EBOOK".equals(type)) book = new EBook(id, title, author, copies, extra);
                    else book = new PrintedBook(id, title, author, copies, extra);
                } else {
                    // update existing book fields (keeping class type)
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setCopies(copies);
                    // if type changed, create new subclass instance with same id
                    if (!existing.getType().equals(type)) {
                        String id = existing.getId();
                        if ("EBOOK".equals(type)) book = new EBook(id, title, author, copies, extra);
                        else book = new PrintedBook(id, title, author, copies, extra);
                    } else {
                        book.setExtra(extra);
                    }
                }
                ok = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid data: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> { ok = false; dispose(); });

        pack();
        setLocationRelativeTo(getOwner());
    }

    public boolean isOk() { return ok; }
    public Book getBook() { return book; }
}
