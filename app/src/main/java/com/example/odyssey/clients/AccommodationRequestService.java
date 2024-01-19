package com.example.odyssey.clients;

import com.example.odyssey.model.accommodations.AccommodationRequest;

import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AccommodationRequestService {
    @POST("accommodationRequests")
    Call<AccommodationRequest> create(@Body AccommodationRequest request);

    @GET("accommodationRequests/{id}/images/{imageName}")
    Call<ArrayList<String>> getImage(@Path("id") Long id, @Path("imageName") String imageName);

    @Multipart
    @POST("accommodationRequests/image/{id}")
    Call<ResponseBody> uploadImage(@Path("id") Long id, @Part MultipartBody.Part image);
}
