import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class AppointmentDesk extends JFrame {

    private final JTextField patientIdField =
        new JTextField(15);

    private final JComboBox<String> departmentBox =
        new JComboBox<>();

    private final JComboBox<Doctor> doctorBox =
        new JComboBox<>();

    private final JComboBox<String> timeSlotBox =
        new JComboBox<>();

    private final JButton bookButton =
        new JButton("Book Appointment");

    private final JLabel statusLabel =
        new JLabel("Ready to book an appointment.");

    private final Map<String, java.util.List<Doctor>>
        doctorsByDepartment = new LinkedHashMap<>();

    private final AppointmentService service =
        new AppointmentService();

    private boolean bookingInProgress = false;

    private boolean closeAfterBooking = false;

    public AppointmentDesk() {

        super("Hospital Outpatient Appointment Desk");

        // Prepare doctor information
        loadDoctorData();

        // Main layout
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout());

        JLabel title = new JLabel(
            "HOSPITAL OUTPATIENT APPOINTMENT DESK"
        );

        headerPanel.add(title);

        // Form panel
        JPanel formPanel = new JPanel(
            new GridLayout(4, 2, 10, 10)
        );

        formPanel.add(new JLabel("Patient ID:"));
        formPanel.add(patientIdField);

        formPanel.add(new JLabel("Department:"));
        formPanel.add(departmentBox);

        formPanel.add(new JLabel("Doctor:"));
        formPanel.add(doctorBox);

        formPanel.add(new JLabel("Time Slot:"));
        formPanel.add(timeSlotBox);

        // Bottom panel
        JPanel bottomPanel = new JPanel(
            new BorderLayout(10, 10)
        );

        JPanel buttonPanel = new JPanel(new FlowLayout());

        buttonPanel.add(bookButton);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);

        add(formPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // Add department options
        departmentBox.addItem("Select Department");

        for (String department : doctorsByDepartment.keySet()) {
            departmentBox.addItem(department);
        }

        // Add time slots
        timeSlotBox.addItem("Select Time Slot");

        timeSlotBox.addItem("09:00 AM");

        timeSlotBox.addItem("10:00 AM");

        timeSlotBox.addItem("11:00 AM");

        // Initially disable doctor selection
        doctorBox.setEnabled(false);

        // Event handling for patient ID
        patientIdField.addActionListener(e -> {

            statusLabel.setText("Patient ID entered.");

        });

        // Event handling for department selection
        departmentBox.addActionListener(e -> {

            refreshDoctorList();

        });

        // Event handling for doctor selection
        doctorBox.addActionListener(e -> {

            Doctor doctor =
                (Doctor) doctorBox.getSelectedItem();

            if (doctor != null) {

                statusLabel.setText(
                    "Selected doctor: " + doctor
                );
            }
        });

        // Event handling for time slot selection
        timeSlotBox.addActionListener(e -> {

            if (timeSlotBox.getSelectedIndex() > 0) {

                statusLabel.setText(
                    "Time slot selected."
                );
            }
        });

        // Book button event
        bookButton.addActionListener(e -> {

            processBooking();

        });

        // Window event handling
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {

                statusLabel.setText(
                    "Ready to book an appointment."
                );
            }

            @Override
            public void windowClosing(WindowEvent e) {

                confirmWindowClosing();

            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setSize(550, 350);

        setLocationRelativeTo(null);
    }

    // Load department-wise doctor data
    private void loadDoctorData() {

        doctorsByDepartment.put(
            "Cardiology",
            Arrays.asList(
                new Doctor("D101", "Dr. Ravi"),
                new Doctor("D102", "Dr. Meena")
            )
        );

        doctorsByDepartment.put(
            "Neurology",
            Arrays.asList(
                new Doctor("D201", "Dr. Kumar"),
                new Doctor("D202", "Dr. Priya")
            )
        );

        doctorsByDepartment.put(
            "Orthopedics",
            Arrays.asList(
                new Doctor("D301", "Dr. John"),
                new Doctor("D302", "Dr. Latha")
            )
        );
    }

    // Refresh doctors when department changes
    private void refreshDoctorList() {

        String department =
            (String) departmentBox.getSelectedItem();

        doctorBox.removeAllItems();

        if (department == null ||
            department.equals("Select Department")) {

            doctorBox.setEnabled(false);

            return;
        }

        java.util.List<Doctor> doctors =
            doctorsByDepartment.get(department);

        if (doctors != null && !doctors.isEmpty()) {

            for (Doctor doctor : doctors) {

                doctorBox.addItem(doctor);
            }

            doctorBox.setEnabled(true);

            statusLabel.setText(
                "Select a doctor."
            );

        } else {

            doctorBox.setEnabled(false);

            statusLabel.setText(
                "No doctors available."
            );
        }
    }

    // Validate and start appointment booking
    private void processBooking() {

        if (bookingInProgress) {
            return;
        }

        String patientId =
            patientIdField.getText().trim();

        String department =
            (String) departmentBox.getSelectedItem();

        Doctor doctor =
            (Doctor) doctorBox.getSelectedItem();

        String timeSlot =
            (String) timeSlotBox.getSelectedItem();

        // Validate patient ID
        if (patientId.isEmpty()) {

            showError("Enter patient ID.");

            return;
        }

        // Validate department
        if (department == null ||
            department.equals("Select Department")) {

            showError("Select a department.");

            return;
        }

        // Validate doctor
        if (doctor == null) {

            showError("Select a doctor.");

            return;
        }

        // Validate time slot
        if (timeSlot == null ||
            timeSlot.equals("Select Time Slot")) {

            showError("Select a time slot.");

            return;
        }

        // Start background booking
        bookingInProgress = true;

        bookButton.setEnabled(false);

        statusLabel.setText(
            "Booking appointment..."
        );

        // Background operation
        SwingWorker<Boolean, Void> worker =
            new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground()
                    throws Exception {

                // Simulate a slow booking operation
                Thread.sleep(1000);

                return service.book(
                    patientId,
                    department,
                    doctor,
                    timeSlot
                );
            }

            @Override
            protected void done() {

                try {

                    boolean booked = get();

                    if (booked) {

                        statusLabel.setText(
                            "Appointment booked successfully."
                        );

                        JOptionPane.showMessageDialog(
                            AppointmentDesk.this,
                            "Appointment booked successfully!"
                            + "\nPatient ID: " + patientId
                            + "\nDepartment: " + department
                            + "\nDoctor: " + doctor
                            + "\nTime Slot: " + timeSlot,
                            "Booking Confirmation",
                            JOptionPane.INFORMATION_MESSAGE
                        );

                    } else {

                        statusLabel.setText(
                            "Selected time slot is unavailable."
                        );

                        JOptionPane.showMessageDialog(
                            AppointmentDesk.this,
                            "This doctor already has a booking "
                            + "for the selected time slot.",
                            "Booking Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }

                } catch (Exception e) {

                    statusLabel.setText(
                        "Unable to complete booking."
                    );

                    JOptionPane.showMessageDialog(
                        AppointmentDesk.this,
                        "Booking failed: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );

                } finally {

                    bookingInProgress = false;

                    bookButton.setEnabled(true);

                    // Close only after the operation finishes
                    if (closeAfterBooking) {

                        dispose();
                    }
                }
            }
        };

        worker.execute();
    }

    // Display an error message
    private void showError(String message) {

        statusLabel.setText(message);

        JOptionPane.showMessageDialog(
            this,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    // Handle window closing
    private void confirmWindowClosing() {

        if (bookingInProgress) {

            int choice = JOptionPane.showConfirmDialog(
                this,
                "A booking is in progress. Close the window "
                + "after the booking finishes?",
                "Confirm Closing",
                JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {

                closeAfterBooking = true;

                statusLabel.setText(
                    "Closing after booking finishes."
                );
            }

        } else {

            dispose();
        }
    }

    // Main method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            AppointmentDesk frame =
                new AppointmentDesk();

            frame.setVisible(true);

        });
    }

    // Doctor class
    static class Doctor {

        private final String id;

        private final String name;

        Doctor(String id, String name) {

            this.id = id;

            this.name = name;
        }

        public String getId() {

            return id;
        }

        @Override
        public String toString() {

            return name;
        }
    }

    // Appointment service class
    static class AppointmentService {

        private final Set<String> bookedSlots =
            new HashSet<>();

        public synchronized boolean book(
                String patientId,
                String department,
                Doctor doctor,
                String timeSlot) {

            String key = doctor.getId() + "|" + timeSlot;

            if (bookedSlots.contains(key)) {

                return false;
            }

            bookedSlots.add(key);

            return true;
        }
    }
}
