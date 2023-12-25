package com.example.odyssey.clients;

import com.example.odyssey.model.accommodations.Accommodation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AccommodationService {
    @GET("accommodations")
    Call<ArrayList<Accommodation>> getAll(
            @Query("type") String type,
            @Query("priceStart") Float startPrice,
            @Query("priceEnd") Float endPrice,
            @Query("amenities") List<Long> amenities,
            @Query("dateStart") Long startDate,
            @Query("dateEnd") Long endDate,
            @Query("location") String location,
            @Query("guestNumber") Integer numberOfGuests
    );

    @GET("accommodations/{id}")
    Call<Accommodation> getOne(@Path("id") Long id);

    @GET("accommodations/{id}/images")
    Call<ArrayList<String>> getImages(@Path("id") Long id);

}
