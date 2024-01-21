package com.example.odyssey.clients;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface FileDownloadService {
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET("accommodations/stats/host/{id}/file")
    @Streaming
    Call<ResponseBody> downloadHostReport(@Path("id") Long id, @Query("startDate") Long startDate, @Query("endDate") Long endDate);

    @GET("accommodations/stats/accommodation/{id}/file")
    Call<ResponseBody> downloadAccommodationReport(@Path("id") Long id, @Query("startDate") Long startDate, @Query("endDate") Long endDate);

}
