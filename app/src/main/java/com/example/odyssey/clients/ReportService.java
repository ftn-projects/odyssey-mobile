package com.example.odyssey.clients;

import com.example.odyssey.model.reports.UserReportSubmission;
import com.example.odyssey.model.users.UserWithReports;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReportService {
    @GET("reports/user")
    Call<List<UserWithReports>> getAllUsers();

    @GET("reports/user/{id}")
    Call<UserWithReports> findUserWithReportById(@Path("id") Long id);

    @POST("reports/user")
    Call<ResponseBody> reportUser(@Body UserReportSubmission report);
}
