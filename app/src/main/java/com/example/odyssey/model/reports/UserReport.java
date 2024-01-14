package com.example.odyssey.model.reports;

public class UserReport {
    private String description;
    private Long submitterId;
    private Long reportedId;

    public UserReport(String description, Long submitterId, Long reportedId) {
        this.description = description;
        this.submitterId = submitterId;
        this.reportedId = reportedId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(Long submitterId) {
        this.submitterId = submitterId;
    }

    public Long getReportedId() {
        return reportedId;
    }

    public void setReportedId(Long reportedId) {
        this.reportedId = reportedId;
    }
}
