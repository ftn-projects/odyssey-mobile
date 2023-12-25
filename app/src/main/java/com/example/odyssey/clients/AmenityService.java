package com.example.odyssey.clients;


import com.example.odyssey.model.accommodations.Amenity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AmenityService {
    @GET("accommodations/amenities")
    Call<ArrayList<Amenity>> getAll();
}
