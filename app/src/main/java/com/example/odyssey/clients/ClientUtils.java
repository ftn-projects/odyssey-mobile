package com.example.odyssey.clients;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.odyssey.BuildConfig;
import com.example.odyssey.adapters.LocalDateAdapter;
import com.example.odyssey.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {
    public static final String SERVICE_API_PATH = "http://" + BuildConfig.SERVER_IP + "/api/v1/";
    public static final String WEB_SOCKET_PATH = "ws://" + BuildConfig.SERVER_IP + "/websocket";
    public static SharedPreferences preferences = null;

    public static void setPreferences(SharedPreferences preferences) {
        ClientUtils.preferences = preferences;

        retrofit = new Retrofit.Builder()
                .baseUrl(SERVICE_API_PATH)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getClient())
                .build();

        amenityService = retrofit.create(AmenityService.class);
        authService = retrofit.create(AuthService.class);
        userService = retrofit.create(UserService.class);
        accommodationService = retrofit.create(AccommodationService.class);
        reviewService = retrofit.create(ReviewService.class);
        accommodationRequestService = retrofit.create(AccommodationRequestService.class);
        reservationService = retrofit.create(ReservationService.class);
        fileDownloadService = retrofit.create(FileDownloadService.class);
        reportService = retrofit.create(ReportService.class);
        notificationService = retrofit.create(NotificationService.class);
    }

    /*
     * Ovo ce nam sluziti za debug, da vidimo da li zahtevi i odgovori idu
     * odnosno dolaze i kako izgeldaju.
     * */
    public static OkHttpClient getClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor).addInterceptor(new AuthInterceptor(preferences)).build();
    }

    /*
     * Prvo je potrebno da definisemo retrofit instancu preko koje ce komunikacija ici
     * */
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                    (json, typeOfT, context) -> LocalDate.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getClient())
            .build();

    public static String getError(Response<?> response, String defaultValue) {
        String error = defaultValue;
        if (response.headers().names().contains("Error-Type")) {
            try {
                error = response.errorBody().string();
            } catch (IOException | NullPointerException e) {
                Log.e("ClientUtils", e.getMessage(), e);
            }
        }
        return error;
    }

    /*
     * Definisemo konkretnu instancu servisa na intnerntu sa kojim
     * vrsimo komunikaciju
     * */
    public static AmenityService amenityService = retrofit.create(AmenityService.class);
    public static AuthService authService = retrofit.create(AuthService.class);
    public static UserService userService = retrofit.create(UserService.class);
    public static AccommodationService accommodationService = retrofit.create(AccommodationService.class);
    public static ReviewService reviewService = retrofit.create(ReviewService.class);
    public static AccommodationRequestService accommodationRequestService = retrofit.create(AccommodationRequestService.class);
    public static ReservationService reservationService = retrofit.create(ReservationService.class);
    public static FileDownloadService fileDownloadService = retrofit.create(FileDownloadService.class);
    public static ReportService  reportService = retrofit.create(ReportService.class);
    public static NotificationService notificationService = retrofit.create(NotificationService.class);
}
