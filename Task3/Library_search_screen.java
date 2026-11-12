import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class LibrarySearchScreen extends JFrame {

    private final JTextField searchField =
        new JTextField(18);

    private final JComboBox<String> categoryBox =
        new JComboBox<>(new String[] {
            "All", "Programming", "Science",
            "Literature", "History"
        });

    private final JButton searchButton =
        new JButton("Search");

    private final JButton prevButton =
        new JButton("Prev");

    private final JButton nextButton =
        new JButton("Next");

    private final JButton reserveButton =
        new JButton("Reserve");

    private final JLabel statusLabel =
        new JLabel("Enter a book name and click Search.");

    private final CardLayout cardLayout =
        new CardLayout();

    private final JPanel resultsPanel =
        new JPanel(cardLayout);

    private final DefaultTableModel tableModel =
        new DefaultTableModel(
            new String[] {
                "Book ID", "Title", "Author",
                "Category", "Availability"
            }, 0
        ) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    private final JTable resultsTable =
        new JTable(tableModel);

    private final List<Book> books =
        new ArrayList<>();

    private final List<Book> currentResults =
        new ArrayList<>();

    private final int PAGE_SIZE = 5;

    private int currentPage = 0;

    // Constructor
    public LibrarySearchScreen() {

        super("Campus Library Search");

        setLayout(new BorderLayout(10, 10));

        // Load sample books
        loadBooks();

        // Create header
        JPanel headerPanel =
            new JPanel(new BorderLayout(5, 5));

        headerPanel.setBorder(
            new EmptyBorder(10, 10, 5, 10)
        );

        // Title panel
        JPanel titlePanel =
            new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel title = new JLabel(
            "CAMPUS LIBRARY SEARCH"
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 20)
        );

        titlePanel.add(title);

        // Search panel
        JPanel searchPanel =
            new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));

        // Search field group using GridLayout
        JPanel searchGroup =
            new JPanel(new GridLayout(1, 2, 5, 0));

        searchGroup.add(new JLabel("Search Book:"));

        searchGroup.add(searchField);

        // Category group using GridLayout
        JPanel categoryGroup =
            new JPanel(new GridLayout(1, 2, 5, 0));

        categoryGroup.add(new JLabel("Category:"));

        categoryGroup.add(categoryBox);

        searchPanel.add(searchGroup);

        searchPanel.add(categoryGroup);

        searchPanel.add(searchButton);

        headerPanel.add(titlePanel, BorderLayout.NORTH);

        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        // Welcome card
        JPanel welcomePanel = new JPanel(
            new GridLayout(2, 1)
        );

        welcomePanel.setBorder(
            new EmptyBorder(20, 20, 20, 20)
        );

        welcomePanel.add(
            new JLabel(
                "Welcome to the Campus Library",
                SwingConstants.CENTER
            )
        );

        welcomePanel.add(
            new JLabel(
                "Search for books using title or author.",
                SwingConstants.CENTER
            )
        );

        // Results table
        resultsTable.setRowHeight(28);

        resultsTable.setFillsViewportHeight(true);

        resultsTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
            new JScrollPane(resultsTable);

        JPanel tablePanel =
            new JPanel(new BorderLayout());

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Empty result card
        JPanel emptyPanel =
            new JPanel(new FlowLayout(FlowLayout.CENTER));

        emptyPanel.add(
            new JLabel("No books found.")
        );

        // Add cards
        resultsPanel.add(welcomePanel, "WELCOME");

        resultsPanel.add(tablePanel, "RESULTS");

        resultsPanel.add(emptyPanel, "EMPTY");

        cardLayout.show(resultsPanel, "WELCOME");

        // Bottom panel
        JPanel bottomPanel =
            new JPanel(new BorderLayout(10, 5));

        bottomPanel.setBorder(
            new EmptyBorder(5, 10, 10, 10)
        );

        // Navigation button panel
        JPanel buttonPanel =
            new JPanel(new FlowLayout(FlowLayout.LEFT));

        buttonPanel.add(prevButton);

        buttonPanel.add(nextButton);

        buttonPanel.add(reserveButton);

        bottomPanel.add(
            buttonPanel, BorderLayout.WEST
        );

        bottomPanel.add(
            statusLabel, BorderLayout.CENTER
        );

        // Add main sections
        add(headerPanel, BorderLayout.NORTH);

        add(resultsPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // Initially disable navigation buttons
        prevButton.setEnabled(false);

        nextButton.setEnabled(false);

        reserveButton.setEnabled(false);

        // Search button event
        searchButton.addActionListener(e -> searchBooks());

        // Enter key event
        searchField.addActionListener(e -> searchBooks());

        // Previous page event
        prevButton.addActionListener(e -> {

            if (currentPage > 0) {

                currentPage--;

                showCurrentPage();
            }
        });

        // Next page event
        nextButton.addActionListener(e -> {

            if ((currentPage + 1) * PAGE_SIZE
                    < currentResults.size()) {

                currentPage++;

                showCurrentPage();
            }
        });

        // Reserve button event
        reserveButton.addActionListener(e -> reserveBook());

        // Window settings
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(700, 450));

        setSize(950, 600);

        setLocationRelativeTo(null);
    }

    // Load sample book data
    private void loadBooks() {

        books.add(new Book(
            "B101", "Java Programming",
            "James Gosling", "Programming"
        ));

        books.add(new Book(
            "B102", "Python Basics",
            "John Smith", "Programming"
        ));

        books.add(new Book(
            "B103", "Data Structures",
            "Mark Allen", "Programming"
        ));

        books.add(new Book(
            "B104", "Database Systems",
            "Abraham Silberschatz", "Programming"
        ));

        books.add(new Book(
            "B105", "Computer Networks",
            "Andrew Tanenbaum", "Programming"
        ));

        books.add(new Book(
            "B106", "Operating Systems",
            "William Stallings", "Programming"
        ));

        books.add(new Book(
            "B201", "Physics Fundamentals",
            "David Halliday", "Science"
        ));

        books.add(new Book(
            "B202", "Organic Chemistry",
            "Paula Bruice", "Science"
        ));

        books.add(new Book(
            "B301", "Pride and Prejudice",
            "Jane Austen", "Literature"
        ));

        books.add(new Book(
            "B302", "The Great Gatsby",
            "F. Scott Fitzgerald", "Literature"
        ));

        books.add(new Book(
            "B401", "Indian History",
            "R. C. Majumdar", "History"
        ));

        books.add(new Book(
            "B402", "World History",
            "Philip Parker", "History"
        ));
    }

    // Search books
    private void searchBooks() {

        String query =
            searchField.getText().trim().toLowerCase();

        String category =
            (String) categoryBox.getSelectedItem();

        currentResults.clear();

        currentPage = 0;

        for (Book book : books) {

            boolean matchesQuery =
                book.title.toLowerCase().contains(query)
                || book.author.toLowerCase().contains(query);

            boolean matchesCategory =
                category.equals("All")
                || book.category.equals(category);

            if (matchesQuery && matchesCategory) {

                currentResults.add(book);
            }
        }

        if (currentResults.isEmpty()) {

            cardLayout.show(resultsPanel, "EMPTY");

            prevButton.setEnabled(false);

            nextButton.setEnabled(false);

            reserveButton.setEnabled(false);

            statusLabel.setText("No books found.");

        } else {

            cardLayout.show(resultsPanel, "RESULTS");

            showCurrentPage();
        }
    }

    // Display current page
    private void showCurrentPage() {

        tableModel.setRowCount(0);

        int start = currentPage * PAGE_SIZE;

        int end = Math.min(
            start + PAGE_SIZE,
            currentResults.size()
        );

        for (int i = start; i < end; i++) {

            Book book = currentResults.get(i);

            String availability =
                book.available ? "Available" : "Reserved";

            tableModel.addRow(new Object[] {
                book.id,
                book.title,
                book.author,
                book.category,
                availability
            });
        }

        prevButton.setEnabled(currentPage > 0);

        nextButton.setEnabled(end < currentResults.size());

        reserveButton.setEnabled(end > start);

        statusLabel.setText(
            "Showing " + (start + 1)
            + " to " + end
            + " of " + currentResults.size()
            + " books."
        );
    }

    // Reserve selected book
    private void reserveBook() {

        int selectedRow =
            resultsTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a book to reserve."
            );

            return;
        }

        int index =
            currentPage * PAGE_SIZE + selectedRow;

        Book selectedBook =
            currentResults.get(index);

        if (!selectedBook.available) {

            JOptionPane.showMessageDialog(
                this,
                "This book is already reserved."
            );

            return;
        }

        selectedBook.available = false;

        showCurrentPage();

        statusLabel.setText(
            "Book reserved: " + selectedBook.title
        );
    }

    // Book class
    static class Book {

        String id;

        String title;

        String author;

        String category;

        boolean available = true;

        Book(String id, String title,
             String author, String category) {

            this.id = id;

            this.title = title;

            this.author = author;

            this.category = category;
        }
    }

    // Main method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            LibrarySearchScreen screen =
                new LibrarySearchScreen();

            screen.setVisible(true);
        });
    }
}
