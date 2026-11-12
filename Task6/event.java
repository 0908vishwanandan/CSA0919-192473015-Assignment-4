public class Event {

    private final int eventId;

    private final String eventName;

    public Event(int eventId, String eventName) {

        this.eventId = eventId;

        this.eventName = eventName;
    }

    public int getEventId() {

        return eventId;
    }

    public String getEventName() {

        return eventName;
    }

    @Override
    public String toString() {

        return eventName;
    }
}
