package model.notification;

public class Notification {
    private String notificationId;
    private String message;
    private String date;

    public Notification(String notificationId, String message, String date) {
        this.notificationId = notificationId;
        this.message = message;
        this.date = date;
    }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String toCsvLine() {
        return String.join(",", notificationId, message.replace(",", ";"), date);
    }

    public static Notification fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split(",");
        if (parts.length < 3) return null;
        if (parts[0].trim().equalsIgnoreCase("notificationId")) return null;
        return new Notification(
            parts[0].trim(),
            parts[1].trim().replace(";", ","),
            parts[2].trim()
        );
    }
}
