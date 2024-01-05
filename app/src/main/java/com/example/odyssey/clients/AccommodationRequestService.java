package com.example.odyssey.clients;

import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.AccommodationRequest;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AccommodationRequestService {
    @POST("accommodationRequests")
    Call<ResponseBody> create(@Body AccommodationRequest request);

    @GET("accommodationRequests")
    Call<ArrayList<String>> getImages(@Path("id") Long id);

}
