package com.example.library.gui;

import com.example.library.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final Library<Book> library;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;

    // Panel i personalizuar per background
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            this.backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public MainFrame(Library<Book> library) {
        super("Library Management System");
        this.library = library;

        BackgroundPanel background = new BackgroundPanel("C:/Users/User/Pictures/librari.jpg");
        setContentPane(background);
        background.setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        searchField = new JTextField(30);
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");
        top.add(new JLabel("Search (title/author):"));
        top.add(searchField);
        top.add(searchBtn);
        top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        String[] cols = new String[]{"ID","Title","Author","Copies","Type","Extra"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane sc = new JScrollPane(table);
        sc.setOpaque(false);
        sc.getViewport().setOpaque(false);
        add(sc, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(10,10,10,10));
        right.setOpaque(false);
        JButton addBtn = new JButton("Add Book");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton borrowBtn = new JButton("Borrow");
        JButton returnBtn = new JButton("Return");
        right.add(addBtn); right.add(Box.createVerticalStrut(8));
        right.add(editBtn); right.add(Box.createVerticalStrut(8));
        right.add(deleteBtn); right.add(Box.createVerticalStrut(8));
        right.add(borrowBtn); right.add(Box.createVerticalStrut(8));
        right.add(returnBtn);
        add(right, BorderLayout.EAST);

        JLabel status = new JLabel("Ready");
        status.setOpaque(false);
        add(status, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> { refreshTable(); status.setText("Refreshed"); });
        searchBtn.addActionListener(e -> {
            try {
                List<Book> res = library.search(searchField.getText().trim());
                loadToTable(res);
                status.setText("Found: " + res.size());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage()); }
        });
        addBtn.addActionListener(e -> {
            AddBookDialog dlg = new AddBookDialog(this);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                Book b = dlg.getBook();
                try {
                    library.addBook(b);
                    refreshTable();
                    status.setText("Added book");
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Add failed: " + ex.getMessage()); }
            }
        });
        editBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            String id = (String) tableModel.getValueAt(r,0);
            Book b = library.getById(id).orElse(null);
            if (b == null) { JOptionPane.showMessageDialog(this, "Selected book not found"); return; }
            AddBookDialog dlg = new AddBookDialog(this, b);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                try {
                    library.updateBook(dlg.getBook());
                    refreshTable();
                    status.setText("Updated");
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage()); }
            }
        });
        deleteBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            String id = (String) tableModel.getValueAt(r,0);
            int ans = JOptionPane.showConfirmDialog(this, "Delete selected book?","Confirm", JOptionPane.YES_NO_OPTION);
            if (ans != JOptionPane.YES_OPTION) return;
            try {
                library.remove(id);
                refreshTable();
                status.setText("Deleted");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage()); }
        });
        borrowBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            String id = (String) tableModel.getValueAt(r,0);
            try {
                library.borrow(id);
                refreshTable();
                status.setText("Borrowed");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Borrow failed: " + ex.getMessage()); }
        });
        returnBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) return;
            String id = (String) tableModel.getValueAt(r,0);
            try {
                library.returnBook(id);
                refreshTable();
                status.setText("Returned");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Return failed: " + ex.getMessage()); }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int r = table.getSelectedRow();
                    if (r < 0) return;
                    String id = (String) tableModel.getValueAt(r,0);
                    Book b = library.getById(id).orElse(null);
                    if (b == null) return;
                    if (b instanceof EBook) {
                        EBook eb = (EBook) b;
                        JOptionPane.showMessageDialog(MainFrame.this, "EBook URL: " + eb.getExtra());
                    } else if (b instanceof PrintedBook) {
                        PrintedBook pb = (PrintedBook) b;
                        JOptionPane.showMessageDialog(MainFrame.this, "Printed shelf: " + pb.getExtra());
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, b.toString());
                    }
                }
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        try {
            library.loadAll();
            loadToTable(library.search(""));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Refresh failed: " + e.getMessage());
        }
    }

    private void loadToTable(java.util.List<Book> list) {
        tableModel.setRowCount(0);
        for (Book b : list) {
            tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getCopies(), b.getType(), b.getExtra()});
        }
    }
}