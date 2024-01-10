package com.example.odyssey.clients;

import com.example.odyssey.model.accommodations.Reservation;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReservationService {
    @GET("host/{id}")
    Call<List<Reservation>> getReservationsByHost(
            @Path("id") Long id,
            @Query("title") String title,
            @Query("status") List<String> status,
            @Query("startDate") Long startDate,
            @Query("endDate") Long endDate
    );

    @PUT("status/{id}")
    Call<ResponseBody> updateStatus(@Path("id") Long id, @Query("status") String status);
}