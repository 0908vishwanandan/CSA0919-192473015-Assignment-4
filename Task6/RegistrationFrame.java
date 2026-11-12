import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RegistrationFrame extends JFrame {

    private final RegistrationService service =
        new RegistrationService();

    // Registration fields
    private final JTextField studentIdField =
        new JTextField(18);

    private final JTextField studentNameField =
        new JTextField(18);

    private final JTextField emailField =
        new JTextField(18);

    private final JComboBox<Event> eventBox =
        new JComboBox<>();

    private final JButton registerButton =
        new JButton("Register");

    private final JButton clearButton =
        new JButton("Clear");

    private final JLabel registrationStatus =
        new JLabel("Ready for registration.");

    // Registration filter
    private final JComboBox<Event> filterEventBox =
        new JComboBox<>();

    // Table model
    private final DefaultTableModel tableModel =
        new DefaultTableModel(
            new String[] {
                "Reg ID",
                "Student ID",
                "Student Name",
                "Email",
                "Event",
                "Registered At"
            },
            0
        ) {

            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };

    private final JTable registrationTable =
        new JTable(tableModel);

    private final JButton previousButton =
        new JButton("Prev");

    private final JButton nextButton =
        new JButton("Next");

    private final JButton exportButton =
        new JButton("Export CSV");

    private final JLabel tableStatus =
        new JLabel("Loading registrations...");

    private final JTabbedPane tabs =
        new JTabbedPane();

    private static final int PAGE_SIZE = 50;

    private int currentPage = 0;

    private int totalRecords = 0;

    private boolean eventsLoaded = false;

    public RegistrationFrame() {

        super("Campus Event Registration System");

        setLayout(new BorderLayout());

        // Create application screens
        tabs.addTab(
            "Student Registration",
            createRegistrationPanel()
        );

        tabs.addTab(
            "Manage Registrations",
            createManagementPanel()
        );

        add(tabs, BorderLayout.CENTER);

        // Create menu bar
        setJMenuBar(createMenuBar());

        // Register button events
        registerButton.addActionListener(e -> registerStudent());

        clearButton.addActionListener(e -> clearFields());

        // Filter event
        filterEventBox.addActionListener(e -> {

            if (eventsLoaded) {

                currentPage = 0;

                loadRegistrations();
            }
        });

        // Previous page
        previousButton.addActionListener(e -> {

            if (currentPage > 0) {

                currentPage--;

                loadRegistrations();
            }
        });

        // Next page
        nextButton.addActionListener(e -> {

            if ((currentPage + 1) * PAGE_SIZE < totalRecords) {

                currentPage++;

                loadRegistrations();
            }
        });

        // Export
        exportButton.addActionListener(e -> exportCSV());

        // Window closing
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                int choice = JOptionPane.showConfirmDialog(
                    RegistrationFrame.this,
                    "Do you want to exit the application?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {

                    dispose();
                }
            }
        });

        setMinimumSize(new Dimension(800, 500));

        setSize(1050, 650);

        setLocationRelativeTo(null);

        // Load event information in the background
        loadEvents();
    }

    // Registration screen
    private JPanel createRegistrationPanel() {

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setBorder(
            BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel(
            "CAMPUS EVENT REGISTRATION"
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 20)
        );

        gbc.gridx = 0;

        gbc.gridy = 0;

        gbc.gridwidth = 2;

        gbc.anchor = GridBagConstraints.CENTER;

        panel.add(title, gbc);

        gbc.gridwidth = 1;

        addFormRow(
            panel, gbc, 1,
            "Student ID:", studentIdField
        );

        addFormRow(
            panel, gbc, 2,
            "Student Name:", studentNameField
        );

        addFormRow(
            panel, gbc, 3,
            "Email:", emailField
        );

        addFormRow(
            panel, gbc, 4,
            "Select Event:", eventBox
        );

        // Button panel
        JPanel buttonPanel =
            new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        buttonPanel.add(registerButton);

        buttonPanel.add(clearButton);

        gbc.gridx = 0;

        gbc.gridy = 5;

        gbc.gridwidth = 2;

        gbc.anchor = GridBagConstraints.CENTER;

        panel.add(buttonPanel, gbc);

        // Status message
        gbc.gridy = 6;

        gbc.anchor = GridBagConstraints.WEST;

        panel.add(registrationStatus, gbc);

        return panel;
    }

    // Add one input row
    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component component) {

        gbc.gridy = row;

        gbc.gridwidth = 1;

        gbc.gridx = 0;

        gbc.weightx = 0;

        gbc.fill = GridBagConstraints.NONE;

        gbc.anchor = GridBagConstraints.LINE_END;

        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;

        gbc.weightx = 1;

        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.anchor = GridBagConstraints.CENTER;

        panel.add(component, gbc);
    }

    // Management screen
    private JPanel createManagementPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Filter section
        JPanel filterPanel =
            new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("Filter by Event:"));

        filterPanel.add(filterEventBox);

        // Table
        registrationTable.setRowHeight(25);

        registrationTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane =
            new JScrollPane(registrationTable);

        // Navigation buttons
        JPanel buttonPanel =
            new JPanel(new FlowLayout(FlowLayout.CENTER));

        buttonPanel.add(previousButton);

        buttonPanel.add(nextButton);

        buttonPanel.add(exportButton);

        // Bottom area
        JPanel bottomPanel =
            new JPanel(new BorderLayout());

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        bottomPanel.add(tableStatus, BorderLayout.SOUTH);

        // Add all components
        panel.add(filterPanel, BorderLayout.NORTH);

        panel.add(scrollPane, BorderLayout.CENTER);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        previousButton.setEnabled(false);

        nextButton.setEnabled(false);

        return panel;
    }

    // Create menu bar
    private JMenuBar createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenu navigateMenu = new JMenu("Navigate");

        JMenu helpMenu = new JMenu("Help");

        JMenuItem exportItem =
            new JMenuItem("Export Registrations");

        JMenuItem exitItem =
            new JMenuItem("Exit");

        JMenuItem registerItem =
            new JMenuItem("Student Registration");

        JMenuItem manageItem =
            new JMenuItem("Manage Registrations");

        JMenuItem aboutItem =
            new JMenuItem("About");

        exportItem.addActionListener(e -> exportCSV());

        exitItem.addActionListener(e -> {

            dispatchEvent(
                new WindowEvent(
                    this,
                    WindowEvent.WINDOW_CLOSING
                )
            );
        });

        registerItem.addActionListener(e -> {

            tabs.setSelectedIndex(0);
        });

        manageItem.addActionListener(e -> {

            tabs.setSelectedIndex(1);

            loadRegistrations();
        });

        aboutItem.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                this,
                "Campus Event Registration System\n" +
                "Java Swing and MySQL JDBC",
                "About",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        fileMenu.add(exportItem);

        fileMenu.addSeparator();

        fileMenu.add(exitItem);

        navigateMenu.add(registerItem);

        navigateMenu.add(manageItem);

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);

        menuBar.add(navigateMenu);

        menuBar.add(helpMenu);

        return menuBar;
    }

    // Load events from database
    private void loadEvents() {

        new SwingWorker<List<Event>, Void>() {

            @Override
            protected List<Event> doInBackground()
                    throws Exception {

                return service.getEvents();
            }

            @Override
            protected void done() {

                try {

                    List<Event> events = get();

                    eventBox.removeAllItems();

                    filterEventBox.removeAllItems();

                    filterEventBox.addItem(
                        new Event(0, "All Events")
                    );

                    for (Event event : events) {

                        eventBox.addItem(event);

                        filterEventBox.addItem(event);
                    }

                    eventsLoaded = true;

                    registrationStatus.setText(
                        "Event list loaded."
                    );

                    loadRegistrations();

                } catch (Exception e) {

                    showError(
                        "Unable to load events: " + errorMessage(e)
                    );

                    registrationStatus.setText(
                        "Unable to load event information."
                    );
                }
            }
        }.execute();
    }

    // Register student
    private void registerStudent() {

        String studentId = studentIdField.getText();

        String studentName = studentNameField.getText();

        String email = emailField.getText();

        Event selectedEvent =
            (Event) eventBox.getSelectedItem();

        registerButton.setEnabled(false);

        registrationStatus.setText(
            "Processing registration..."
        );

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground()
                    throws Exception {

                service.register(
                    studentId,
                    studentName,
                    email,
                    selectedEvent
                );

                return null;
            }

            @Override
            protected void done() {

                try {

                    get();

                    JOptionPane.showMessageDialog(
                        RegistrationFrame.this,
                        "Registration completed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    registrationStatus.setText(
                        "Registration successful."
                    );

                    clearFields();

                    // Refresh the table after registration
                    currentPage = 0;

                    loadRegistrations();

                } catch (Exception e) {

                    showError(errorMessage(e));

                    registrationStatus.setText(
                        "Registration failed."
                    );

                } finally {

                    registerButton.setEnabled(true);
                }
            }
        }.execute();
    }

    // Clear input fields
    private void clearFields() {

        studentIdField.setText("");

        studentNameField.setText("");

        emailField.setText("");

        if (eventBox.getItemCount() > 0) {

            eventBox.setSelectedIndex(0);
        }

        registrationStatus.setText(
            "Input fields cleared."
        );
    }

    // Load registration records using pagination
    private void loadRegistrations() {

        if (!eventsLoaded) {
            return;
        }

        Event selected =
            (Event) filterEventBox.getSelectedItem();

        Integer eventId = null;

        if (selected != null && selected.getEventId() != 0) {

            eventId = selected.getEventId();
        }

        final Integer selectedEventId = eventId;

        final int requestedPage = currentPage;

        previousButton.setEnabled(false);

        nextButton.setEnabled(false);

        tableStatus.setText(
            "Loading registrations..."
        );

        new SwingWorker<RegistrationPage, Void>() {

            @Override
            protected RegistrationPage doInBackground()
                    throws Exception {

                return service.getPage(
                    selectedEventId,
                    requestedPage,
                    PAGE_SIZE
                );
            }

            @Override
            protected void done() {

                try {

                    RegistrationPage page = get();

                    List<Registration> records =
                        page.getRegistrations();

                    totalRecords = page.getTotalRecords();

                    // Clear old table rows
                    tableModel.setRowCount(0);

                    // Add current page records
                    for (Registration r : records) {

                        tableModel.addRow(
                            new Object[] {
                                r.getRegistrationId(),
                                r.getStudentId(),
                                r.getStudentName(),
                                r.getEmail(),
                                r.getEventName(),
                                r.getRegisteredAt()
                            }
                        );
                    }

                    // Update navigation buttons
                    previousButton.setEnabled(
                        requestedPage > 0
                    );

                    nextButton.setEnabled(
                        (requestedPage + 1) * PAGE_SIZE
                            < totalRecords
                    );

                    int start = totalRecords == 0
                        ? 0
                        : requestedPage * PAGE_SIZE + 1;

                    int end = Math.min(
                        (requestedPage + 1) * PAGE_SIZE,
                        totalRecords
                    );

                    tableStatus.setText(
                        "Showing " + start + " to " + end
                        + " of " + totalRecords
                        + " registrations. Page "
                        + (requestedPage + 1)
                    );

                } catch (Exception e) {

                    showError(
                        "Unable to load registrations: "
                        + errorMessage(e)
                    );

                    tableStatus.setText(
                        "Unable to load registration records."
                    );
                }
            }
        }.execute();
    }

    // Export registrations to CSV
    private void exportCSV() {

        if (!eventsLoaded) {

            showError("Event information is not ready.");

            return;
        }

        JFileChooser chooser = new JFileChooser();

        chooser.setSelectedFile(
            new File("registrations.csv")
        );

        int choice = chooser.showSaveDialog(this);

        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(".csv")) {

            file = new File(file.getAbsolutePath() + ".csv");
        }

        Event selected =
            (Event) filterEventBox.getSelectedItem();

        Integer eventId = null;

        if (selected != null && selected.getEventId() != 0) {

            eventId = selected.getEventId();
        }

        final Integer selectedEventId = eventId;

        final File selectedFile = file;

        exportButton.setEnabled(false);

        tableStatus.setText(
            "Exporting registration records..."
        );

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground()
                    throws Exception {

                service.export(
                    selectedEventId,
                    selectedFile
                );

                return null;
            }

            @Override
            protected void done() {

                try {

                    get();

                    JOptionPane.showMessageDialog(
                        RegistrationFrame.this,
                        "CSV exported successfully:\n"
                        + selectedFile.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    tableStatus.setText(
                        "CSV export completed."
                    );

                } catch (Exception e) {

                    showError(
                        "Export failed: " + errorMessage(e)
                    );

                } finally {

                    exportButton.setEnabled(true);
                }
            }
        }.execute();
    }

    // Display error messages
    private void showError(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    // Extract the real background exception message
    private String errorMessage(Throwable error) {

        Throwable cause = error.getCause();

        while (cause != null) {

            error = cause;

            cause = error.getCause();
        }

        if (error instanceof RegistrationService.ValidationException
                || error instanceof DuplicateRegistrationException) {

            return error.getMessage();
        }

        return error.getMessage() == null
            ? "An unexpected error occurred."
            : error.getMessage();
    }

    // Main method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            RegistrationFrame frame =
                new RegistrationFrame();

            frame.setVisible(true);
        });
    }
}
