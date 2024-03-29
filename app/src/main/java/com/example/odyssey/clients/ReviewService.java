package com.example.odyssey.clients;

import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.HostReview;
import com.example.odyssey.model.reviews.Review;

import java.util.ArrayList;
import java.util.List;

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
    @GET("reviews")
    Call<List<Review>> getAll(
            @Query("search") String search,
            @Query("statuses") List<Review.Status> statuses,
            @Query("types") List<String> types
    );

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
            @Query("listTypes") ArrayList<Review.Status> listTypes
    );
    @GET("reviews/accommodation/{id}")
    Call<AccommodationReview> getOneAccommodationReview(@Path("id") Long id);

    @GET("reviews/host/{id}")
    Call<HostReview> getOneHostReview(@Path("id") Long id);

    @GET("reviews/accommodation/host/{id}")
    Call<ArrayList<AccommodationReview>> getAllAccommodationReviewsByHostId(
            @Path("id") Long id,
            @Query("listTypes") ArrayList<Review.Status> listTypes
    );

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

    @PUT("reviews/accept/{id}")
    Call<ResponseBody> accept(@Path("id") Long id);

    @PUT("reviews/decline/{id}")
    Call<ResponseBody> decline(@Path("id") Long id);

    @PUT("reviews/dismiss/{id}")
    Call<ResponseBody> dismiss(@Path("id") Long id);
}
