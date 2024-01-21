package com.example.odyssey.model.users;

import com.example.odyssey.model.Address;
import com.example.odyssey.model.reports.UserReport;
import com.example.odyssey.model.reports.UserReportSubmission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserWithReports extends User implements Serializable {
    private AccountStatus status;
    private List<UserReport> reports = new ArrayList<>();
    public enum AccountStatus {PENDING, ACTIVE, BLOCKED, DEACTIVATED}

    public UserWithReports() {
    }

    public UserWithReports(Long id, String email, String name, String surname, String phone, String role, Address address, Settings settings, String bio, AccountStatus status, List<UserReport> reports) {
        super(id, email, name, surname, phone, role, address, settings, bio);
        this.status = status;
        this.reports = reports;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public List<UserReport> getReports() {
        return reports;
    }

    public void setReports(List<UserReport> reports) {
        this.reports = reports;
    }
}
