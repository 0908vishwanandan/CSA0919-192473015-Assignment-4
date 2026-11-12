import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {

    // Retrieve active events
    public List<Event> findActiveEvents()
            throws SQLException {

        List<Event> events = new ArrayList<>();

        String sql =
            "SELECT event_id, event_name " +
            "FROM events WHERE active = TRUE " +
            "ORDER BY event_name";

        try (
            Connection con = Database.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                events.add(
                    new Event(
                        rs.getInt("event_id"),
                        rs.getString("event_name")
                    )
                );
            }
        }

        return events;
    }

    // Insert a new registration
    public void insert(Registration registration)
            throws SQLException,
                   DuplicateRegistrationException {

        String sql =
            "INSERT INTO registrations " +
            "(student_id, student_name, email, event_id) " +
            "VALUES (?, ?, ?, ?)";

        try (
            Connection con = Database.getConnection();

            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, registration.getStudentId());

            ps.setString(2, registration.getStudentName());

            ps.setString(3, registration.getEmail());

            ps.setInt(4, registration.getEventId());

            ps.executeUpdate();

        } catch (SQLException e) {

            // MySQL error code 1062 indicates a duplicate key
            if (e.getErrorCode() == 1062) {

                throw new DuplicateRegistrationException(
                    "This student is already registered for this event."
                );
            }

            throw e;
        }
    }

    // Fetch one page of registrations
    public RegistrationPage findPage(
            Integer eventId,
            int limit,
            int offset)
            throws SQLException {

        String where = eventId == null
            ? ""
            : " WHERE r.event_id = ?";

        String countSql =
            "SELECT COUNT(*) FROM registrations r" + where;

        String dataSql =
            "SELECT r.registration_id, " +
            "r.student_id, r.student_name, r.email, " +
            "r.event_id, e.event_name, r.registered_at " +
            "FROM registrations r " +
            "JOIN events e ON r.event_id = e.event_id " +
            where +
            " ORDER BY r.registration_id DESC " +
            "LIMIT ? OFFSET ?";

        try (Connection con = Database.getConnection()) {

            int totalRecords = 0;

            // Count matching registrations
            try (PreparedStatement countPs =
                    con.prepareStatement(countSql)) {

                if (eventId != null) {

                    countPs.setInt(1, eventId);
                }

                try (ResultSet rs = countPs.executeQuery()) {

                    if (rs.next()) {

                        totalRecords = rs.getInt(1);
                    }
                }
            }

            List<Registration> registrations =
                new ArrayList<>();

            // Retrieve only the required page
            try (PreparedStatement ps =
                    con.prepareStatement(dataSql)) {

                int parameter = 1;

                if (eventId != null) {

                    ps.setInt(parameter++, eventId);
                }

                ps.setInt(parameter++, limit);

                ps.setInt(parameter, offset);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        registrations.add(
                            new Registration(
                                rs.getLong("registration_id"),
                                rs.getString("student_id"),
                                rs.getString("student_name"),
                                rs.getString("email"),
                                rs.getInt("event_id"),
                                rs.getString("event_name"),
                                rs.getTimestamp("registered_at")
                            )
                        );
                    }
                }
            }

            return new RegistrationPage(
                registrations, totalRecords
            );
        }
    }

    // Export all matching registrations to CSV
    public void exportCsv(Integer eventId, File file)
            throws SQLException, IOException {

        String where = eventId == null
            ? ""
            : " WHERE r.event_id = ?";

        String sql =
            "SELECT r.registration_id, r.student_id, " +
            "r.student_name, r.email, e.event_name, " +
            "r.registered_at " +
            "FROM registrations r " +
            "JOIN events e ON r.event_id = e.event_id " +
            where +
            " ORDER BY r.registration_id";

        try (
            Connection con = Database.getConnection();

            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            if (eventId != null) {

                ps.setInt(1, eventId);
            }

            try (
                ResultSet rs = ps.executeQuery();

                BufferedWriter writer = Files.newBufferedWriter(
                    file.toPath(),
                    StandardCharsets.UTF_8
                )
            ) {

                // CSV header
                writer.write(
                    "Registration ID,Student ID,Student Name," +
                    "Email,Event,Registered At"
                );

                writer.newLine();

                // Write matching records one at a time
                while (rs.next()) {

                    writer.write(
                        rs.getLong("registration_id") + "," +
                        csv(rs.getString("student_id")) + "," +
                        csv(rs.getString("student_name")) + "," +
                        csv(rs.getString("email")) + "," +
                        csv(rs.getString("event_name")) + "," +
                        csv(rs.getTimestamp("registered_at").toString())
                    );

                    writer.newLine();
                }
            }
        }
    }

    // Escape CSV values correctly
    private String csv(String value) {

        if (value == null) {
            value = "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
