package com.example.odyssey.clients;

import com.example.odyssey.BuildConfig;
import com.example.odyssey.adapters.LocalDateAdapter;
import com.example.odyssey.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {
    public static final String SERVICE_API_PATH = "http://" + BuildConfig.SERVER_IP + "/api/v1/";
    public static final String WEB_SOCKET_PATH = "ws://" + BuildConfig.SERVER_IP + "/websocket";

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
                .addInterceptor(loggingInterceptor).addInterceptor(new AuthInterceptor()).build();
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
    public static NotificationService notificationService = retrofit.create(NotificationService.class);
}
