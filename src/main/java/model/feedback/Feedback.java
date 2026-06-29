package model.feedback;

public class Feedback {
    private String feedbackId;
    private String fanId;
    private String content;
    private String response;

    public Feedback(String feedbackId, String fanId, String content, String response) {
        this.feedbackId = feedbackId;
        this.fanId = fanId;
        this.content = content;
        this.response = response;
    }

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }

    public String getFanId() { return fanId; }
    public void setFanId(String fanId) { this.fanId = fanId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String toCsvLine() {
        return String.join(",", feedbackId, fanId, content.replace(",", ";"), response.replace(",", ";"));
    }

    public static Feedback fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split(",");
        if (parts.length < 4) return null;
        if (parts[0].trim().equalsIgnoreCase("feedbackId")) return null;
        return new Feedback(
            parts[0].trim(),
            parts[1].trim(),
            parts[2].trim().replace(";", ","),
            parts[3].trim().replace(";", ",")
        );
    }
}
