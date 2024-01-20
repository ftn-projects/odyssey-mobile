package com.example.odyssey.clients;

import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.stats.AccommodationTotalStats;
import com.example.odyssey.model.stats.TotalStats;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
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
    Call<Accommodation> findById(@Path("id") Long id);

    @GET("accommodations/{id}/totalPrice")
    Call<Accommodation> getAccommodationWithPrice(
            @Path("id") Long id,
            @Query("dateStart") Long startDate,
            @Query("dateEnd") Long endDate,
            @Query("guestNumber") Integer numberOfGuests
    );

    @GET("accommodations/{id}/images")
    Call<ArrayList<String>> getImages(@Path("id") Long id);

    @PUT("accommodations/favorites/{guestId}/{accommodationId}")
    Call<Void> addFavorite(@Path("guestId") Long guestId, @Path("accommodationId") Long accommodationId);

    @DELETE("accommodations/favorites/{guestId}/{accommodationId}")
    Call<Void> removeFavorite(@Path("guestId") Long guestId, @Path("accommodationId") Long accommodationId);

    @GET("accommodations/favorites/{id}")
    Call<ArrayList<Accommodation>> getFavorites(@Path("id") Long id);

    @GET("accommodations/stats/accommodation/{id}")
    Call<AccommodationTotalStats> getPeriodStatsForAccommodation(@Path("id") Long id, @Query("startDate") Long startDate, @Query("endDate") Long endDate);

    @GET("accommodations/stats/host/{id}")
    Call<TotalStats> generatePeriodStats(@Path("id") Long id, @Query("startDate") Long startDate, @Query("endDate") Long endDate);

    @GET("accommodations/stats/host/{id}/all")
    Call<ArrayList<AccommodationTotalStats>> getPeriodStatsAllAccommodation(@Path("id") Long id, @Query("startDate") Long startDate, @Query("endDate") Long endDate);


}
