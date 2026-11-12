import java.sql.Timestamp;

public class Registration {

    private final long registrationId;

    private final String studentId;

    private final String studentName;

    private final String email;

    private final int eventId;

    private final String eventName;

    private final Timestamp registeredAt;

    // Constructor used when creating a new registration
    public Registration(
            String studentId,
            String studentName,
            String email,
            int eventId) {

        this.registrationId = 0;

        this.studentId = studentId;

        this.studentName = studentName;

        this.email = email;

        this.eventId = eventId;

        this.eventName = "";

        this.registeredAt = null;
    }

    // Constructor used when retrieving records
    public Registration(
            long registrationId,
            String studentId,
            String studentName,
            String email,
            int eventId,
            String eventName,
            Timestamp registeredAt) {

        this.registrationId = registrationId;

        this.studentId = studentId;

        this.studentName = studentName;

        this.email = email;

        this.eventId = eventId;

        this.eventName = eventName;

        this.registeredAt = registeredAt;
    }

    public long getRegistrationId() {

        return registrationId;
    }

    public String getStudentId() {

        return studentId;
    }

    public String getStudentName() {

        return studentName;
    }

    public String getEmail() {

        return email;
    }

    public int getEventId() {

        return eventId;
    }

    public String getEventName() {

        return eventName;
    }

    public Timestamp getRegisteredAt() {

        return registeredAt;
    }
}
