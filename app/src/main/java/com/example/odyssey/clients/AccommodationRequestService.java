package com.example.odyssey.clients;

import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.accommodations.AccommodationRequestSubmission;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AccommodationRequestService {
    @POST("accommodationRequests")
    Call<AccommodationRequestSubmission> create(@Body AccommodationRequestSubmission request);

    @Multipart
    @POST("accommodationRequests/image/{id}")
    Call<ResponseBody> uploadImage(@Path("id") Long id, @Part MultipartBody.Part image);

    @GET("accommodationRequests")
    Call<List<AccommodationRequest>> findByStatus(@Query("status") String status);

    @PUT("accommodationRequests/status/{id}")
    Call<ResponseBody> updateStatus(@Path("id") Long id, @Query("status") String status);

    @GET("accommodationRequests/{id}/images")
    Call<ArrayList<String>> getImages(@Path("id") Long id);
}
