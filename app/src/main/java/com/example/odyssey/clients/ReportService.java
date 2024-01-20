package com.example.odyssey.clients;

import com.example.odyssey.model.reports.UserReport;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReportService {
    @POST("reports/user")
    Call<Void> reportUser(@Body UserReport report);

}
