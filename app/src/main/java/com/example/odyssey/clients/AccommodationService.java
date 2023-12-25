package com.example.odyssey.clients;

import com.example.odyssey.model.accommodations.Accommodation;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AccommodationService {
    @GET("accommodations")
    Call<ArrayList<Accommodation>> getAll();

    @GET("accommodations/{id}")
    Call<Accommodation> getOne(@Path("id") Long id);
}
