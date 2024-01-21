package com.example.odyssey.fragments.accommodation;

import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.odyssey.R;
import com.example.odyssey.clients.AmenityIconMapper;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.fragments.AvailabilitySlots;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.accommodations.Amenity;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationRequestDetailsFragment extends Fragment {
    public static final String ARG_ACCOMMODATION = "request";
    private TextView titleView, typeView, descriptionView, pricingView, defaultPriceView, automaticApprovalView, minMaxGuestView;
    LinearLayout amenitiesContainer;
    LinearLayout slotsList;
    Button acceptBtn, declineBtn;
    private AccommodationRequest accommodation;

    public AccommodationRequestDetailsFragment() {
    }

    public static AccommodationRequestDetailsFragment newInstance() {
        return new AccommodationRequestDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_ACCOMMODATION))
            accommodation = (AccommodationRequest) getArguments().getSerializable(ARG_ACCOMMODATION);
        if (accommodation == null)
            throw new RuntimeException("Accommodation request is null");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_accommodation_request_details, container, false);

        titleView = v.findViewById(R.id.title);
        typeView = v.findViewById(R.id.type);
        pricingView = v.findViewById(R.id.pricing);
        descriptionView = v.findViewById(R.id.details_about);
        defaultPriceView = v.findViewById(R.id.default_price);
        automaticApprovalView = v.findViewById(R.id.automatic_approval);
        minMaxGuestView = v.findViewById(R.id.min_max_guests);
        slotsList = v.findViewById(R.id.plsRadiOpet);
        amenitiesContainer = v.findViewById(R.id.details_amenities_container);

        acceptBtn = v.findViewById(R.id.activate_button);
        declineBtn = v.findViewById(R.id.deactivate_button);
        acceptBtn.setOnClickListener(b -> accept());
        declineBtn.setOnClickListener(b -> decline());

        map = v.findViewById(R.id.mapGoesHereDetails);
        mapView = v.findViewById(R.id.osmmapDetails);
        imageSlider = v.findViewById(R.id.image_slider);

        loadData();
        loadImages();
        initMap();
        return v;
    }

    private void decline() {
        ClientUtils.accommodationRequestService.updateStatus(accommodation.getId(), "DECLINED").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    String message = ClientUtils.getError(response, "Accommodation request could not be declined.");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    Log.e("AccommodationRequestDetailsFragment", "Accommodation request could not be declined: " + response.code());
                    return;
                }
                ;
                Toast.makeText(getContext(), "Accommodation request declined.", Toast.LENGTH_LONG).show();
                Navigation.findNavController(requireView()).navigateUp();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("AccommodationRequestDetailsFragment", "Accommodation request could not be declined: " + t.getMessage());
            }
        });
    }

    private void accept() {
        ClientUtils.accommodationRequestService.updateStatus(accommodation.getId(), "ACCEPTED").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    String message = ClientUtils.getError(response, "Accommodation request could not be accepted.");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    Log.e("AccommodationRequestDetailsFragment", "Accommodation request could not be accepted: " + response.code());
                    return;
                }
                Toast.makeText(getContext(), "Accommodation request accepted.", Toast.LENGTH_LONG).show();
                Navigation.findNavController(requireView()).navigateUp();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("AccommodationRequestDetailsFragment", "Accommodation request could not be accepted: " + t.getMessage());
            }
        });
    }

    private void loadData() {
        Accommodation details = accommodation.getDetails();
        titleView.setText(details.getTitle());
        typeView.setText(details.getType().toString());
        pricingView.setText(details.getPricing().toString());
        descriptionView.setText(details.getDescription());
        defaultPriceView.setText(details.getDefaultPrice().toString());
        automaticApprovalView.setText(details.getAutomaticApproval().toString());
        minMaxGuestView.setText(details.getMinGuests() + " - " + details.getMaxGuests());

        addAmenities(details.getAmenities());
        details.getAvailableSlots().forEach(s ->
                slotsList.addView(new AvailabilitySlots(s, requireContext())));
    }

    public void addAmenities(Set<Amenity> amenities) {
        for (Amenity amenity : amenities) {
            Log.e("REZ", "Amenity: " + amenity.getTitle());
            // Create a new LinearLayout for each amenity group
            LinearLayout amenityGroupLayout = new LinearLayout(requireContext());
            amenityGroupLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            amenityGroupLayout.setOrientation(LinearLayout.HORIZONTAL);
            amenityGroupLayout.setPadding(8, 8, 8, 8);

            // Create and add ImageView for the amenity icon
            ImageView amenityIcon = new ImageView(requireContext());
            amenityIcon.setImageResource(AmenityIconMapper.getIconResourceId(amenity.getTitle()));
            amenityIcon.setLayoutParams(new LinearLayout.LayoutParams(
                    84,
                    84
            ));
            amenityGroupLayout.addView(amenityIcon);

            TextView amenityText = new TextView(requireContext());
            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textLayoutParams.leftMargin = 16;
            amenityText.setLayoutParams(textLayoutParams);
            amenityText.setText(amenity.getTitle());
            Typeface montserratTypeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular);
            amenityText.setTypeface(montserratTypeface);
            amenityGroupLayout.addView(amenityText);


            amenitiesContainer.addView(amenityGroupLayout);
        }
    }

    LinearLayout map;
    MapView mapView;
    IMapController controller;
    private final Handler handler = new Handler(Looper.getMainLooper());
    MyLocationNewOverlay mMyLocationOverlay;
    Marker pickedLocationMarker;

    private void initMap() {
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(requireActivity().getPackageName());

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        controller = mapView.getController();
        controller.setZoom(10.0);
        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireActivity()), mapView);
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);

        GeoPoint startPoint = new GeoPoint(45.25167, 19.83694); //gde te postavi kad otvori mapu
        controller.setCenter(startPoint);

        mapView.getOverlays().add(mMyLocationOverlay);

        pickedLocationMarker = new Marker(mapView);

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(
                    accommodation.getDetails().getAddress().toString(), 1);
            Address address = Objects.requireNonNull(addresses).get(0);

            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            GeoPoint startPointNew = new GeoPoint(latitude, longitude);
            controller.setCenter(startPointNew);
            Log.e("REZ", "Latitude: " + latitude + ", Longitude: " + longitude);
            pickedLocationMarker.setPosition(new GeoPoint(latitude, longitude));
            pickedLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(pickedLocationMarker);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ImageSlider imageSlider;

    public void loadImages() {
        Call<ArrayList<String>> call = ClientUtils.accommodationRequestService.getImages(accommodation.getId());
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<String>> call, @NonNull Response<ArrayList<String>> response) {
                if (response.isSuccessful()) {
                    List<String> accommodationImages = response.body();
                    if (accommodationImages != null && accommodationImages.size() > 0) {
                        List<String> imageUrls = new ArrayList<>();
                        for (String image : accommodationImages) {
                            String imagePath = ClientUtils.SERVICE_API_PATH + "accommodationRequests/" + accommodation.getId() + "/images/" + image;
                            imageUrls.add(imagePath);
                        }
                        initImageSlider(imageUrls);
                    }
                } else {
                    String message = ClientUtils.getError(response, "Images could not be fetched.");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    Log.d("AccommodationRequestDetailsFragment", "Bad");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<String>> call, @NonNull Throwable t) {
                Log.d("AccommodationRequestDetailsFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void initImageSlider(List<String> imageUrls) {
        ArrayList<SlideModel> imageList = new ArrayList<>();

        for (String imageUrl : imageUrls)
            imageList.add(new SlideModel(imageUrl, ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(imageList);
        imageSlider.stopSliding();
    }
}