package com.example.odyssey.clients;

import com.example.odyssey.model.reservations.AccreditReservation;
import com.example.odyssey.model.reservations.ReservationRequest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReservationService {
    @GET("reservations/host/{id}")
    Call<List<AccreditReservation>> getReservationsByHost(
            @Path("id") Long id,
            @Query("title") String title,
            @Query("status") List<String> status,
            @Query("startDate") Long startDate,
            @Query("endDate") Long endDate
    );

    @GET("reservations/guest/{id}")
    Call<List<AccreditReservation>> getReservationsByGuest(
            @Path("id") Long id,
            @Query("title") String title,
            @Query("status") List<String> status,
            @Query("startDate") Long startDate,
            @Query("endDate") Long endDate
    );

    @PUT("reservations/status/{id}")
    Call<ResponseBody> updateStatus(@Path("id") Long id, @Query("status") String status);

    @POST("reservations")
    Call<ResponseBody> createReservation(@Body ReservationRequest reservation);
}