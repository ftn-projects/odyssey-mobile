package com.example.odyssey.clients;


import com.example.odyssey.model.accommodations.Amenity;

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
public interface AmenityService {
    @GET("accommodations/amenities")
    Call<ArrayList<Amenity>> getAll();



}
