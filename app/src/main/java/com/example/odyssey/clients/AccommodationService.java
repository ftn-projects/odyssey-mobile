package com.example.odyssey.clients;

import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.Amenity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AccommodationService {
    @GET("accommodations")
    Call<ArrayList<Accommodation>> getAll();
}
