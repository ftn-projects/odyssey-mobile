package com.example.odyssey.clients;

import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.Review;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReviewService {

    @GET("reviews/accommodation")
    Call<ArrayList<AccommodationReview>> getAllAccommodationReviews(
            @Query("accommodationId") Long accommodationId,
            @Query("userId") Long submitterId,
            @Query("status") ArrayList<Review.Status> listTypes
    );

    @GET("reviews/accommodation/{id}")
    Call<ArrayList<AccommodationReview>> getOneAccommodationReview(@Query("id") Long id);

    @POST("reviews/accommodation")
    Call<ResponseBody> create(@Body AccommodationReview request);

}
