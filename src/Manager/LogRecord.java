package Manager;

public class LogRecord {
    private final String id;
    private final String eventDate;
    private final String level;
    private final String logger;
    private final String message;
    private final String username;

    public LogRecord(String id, String eventDate, String level, String logger, String message, String username) {
        this.id = id;
        this.eventDate = eventDate;
        this.level = level;
        this.logger = logger;
        this.message = message;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getLevel() {
        return level;
    }

    public String getLogger() {
        return logger;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }
}
