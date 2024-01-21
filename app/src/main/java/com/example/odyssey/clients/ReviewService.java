package com.example.odyssey.clients;

import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.HostReview;
import com.example.odyssey.model.reviews.Review;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewService {

    @GET("reviews/accommodation")
    Call<ArrayList<AccommodationReview>> getAllAccommodationReviews(
            @Query("accommodationId") Long accommodationId,
            @Query("submitterId") Long submitterId,
            @Query("status") ArrayList<Review.Status> listTypes
    );

    @GET("reviews/host")
    Call<ArrayList<HostReview>> getAllHostReviews(
            @Query("hostId") Long hostId,
            @Query("submitterId") Long submitterId,
            @Query("status") ArrayList<Review.Status> listTypes
    );
    @GET("reviews/accommodation/{id}")
    Call<ArrayList<AccommodationReview>> getOneAccommodationReview(@Query("id") Long id);

    @GET("reviews/host/{id}")
    Call<ArrayList<Review>> getOneHostReview(@Query("id") Long id);

    @GET("reviews/accommodation/rating/{id}")
    Call<ArrayList<Integer>> getAccommodationRatings(@Path("id") Long id);

    @GET("reviews/host/rating/{id}")
    Call<ArrayList<Integer>> getHostRatings(@Path("id") Long id);

    @POST("reviews/accommodation")
    Call<ResponseBody> createAccommodationReview(@Body AccommodationReview request);


    @POST("reviews/host")
    Call<ResponseBody> createHostReview(@Body HostReview request);

    @PUT("reviews/accommodation/report/{id}")
    Call<ResponseBody> reportAccommodationReview(@Query("id") Long id);

    @PUT("reviews/host/report/{id}")
    Call<ResponseBody> reportHostReview(@Query("id") Long id);

    @DELETE("reviews/accommodation/{id}")
    Call<ResponseBody> deleteAccommodationReview(@Path("id") Long id);

    @DELETE("reviews/host/{id}")
    Call<ResponseBody> deleteHostReview(@Path("id") Long id);

}
