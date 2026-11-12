import java.util.List;

public class RegistrationPage {

    private final List<Registration> registrations;

    private final int totalRecords;

    public RegistrationPage(
            List<Registration> registrations,
            int totalRecords) {

        this.registrations = registrations;

        this.totalRecords = totalRecords;
    }

    public List<Registration> getRegistrations() {

        return registrations;
    }

    public int getTotalRecords() {

        return totalRecords;
    }
}
