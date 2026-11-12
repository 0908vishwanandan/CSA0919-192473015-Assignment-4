import java.io.File;
import java.io.IOException;

import java.sql.SQLException;

import java.util.List;
import java.util.Locale;

public class RegistrationService {

    private final RegistrationDAO dao =
        new RegistrationDAO();

    // Input validation and registration
    public void register(
            String studentId,
            String studentName,
            String email,
            Event event)
            throws ValidationException,
                   DuplicateRegistrationException,
                   SQLException {

        // Check student ID
        if (studentId == null || studentId.trim().isEmpty()) {

            throw new ValidationException(
                "Student ID cannot be empty."
            );
        }

        studentId = studentId.trim().toUpperCase(Locale.ROOT);

        if (!studentId.matches("[A-Z0-9_-]{3,30}")) {

            throw new ValidationException(
                "Student ID must contain 3 to 30 valid characters."
            );
        }

        // Check student name
        if (studentName == null ||
            studentName.trim().isEmpty()) {

            throw new ValidationException(
                "Student name cannot be empty."
            );
        }

        studentName = studentName.trim();

        if (studentName.length() > 100) {

            throw new ValidationException(
                "Student name is too long."
            );
        }

        // Check email
        if (email == null || email.trim().isEmpty()) {

            throw new ValidationException(
                "Email cannot be empty."
            );
        }

        email = email.trim().toLowerCase(Locale.ROOT);

        if (email.length() > 150 ||
            !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new ValidationException(
                "Enter a valid email address."
            );
        }

        // Check selected event
        if (event == null || event.getEventId() <= 0) {

            throw new ValidationException(
                "Please select an event."
            );
        }

        // Create registration object
        Registration registration =
            new Registration(
                studentId,
                studentName,
                email,
                event.getEventId()
            );

        // Save registration
        dao.insert(registration);
    }

    // Retrieve event list
    public List<Event> getEvents()
            throws SQLException {

        return dao.findActiveEvents();
    }

    // Retrieve one registration page
    public RegistrationPage getPage(
            Integer eventId,
            int page,
            int pageSize)
            throws SQLException {

        if (page < 0 || pageSize <= 0) {

            throw new IllegalArgumentException(
                "Invalid page information."
            );
        }

        int offset = page * pageSize;

        return dao.findPage(
            eventId,
            pageSize,
            offset
        );
    }

    // Export registrations
    public void export(
            Integer eventId,
            File file)
            throws SQLException, IOException {

        dao.exportCsv(eventId, file);
    }

    // Custom validation exception
    public static class ValidationException extends Exception {

        public ValidationException(String message) {

            super(message);
        }
    }
}
