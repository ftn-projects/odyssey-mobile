package com.example.odyssey.clients;

import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.Review;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
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


}
