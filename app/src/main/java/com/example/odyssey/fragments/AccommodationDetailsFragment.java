package com.example.odyssey.fragments;

import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.odyssey.R;
import com.example.odyssey.clients.AmenityIconMapper;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.fragments.accommodationRequest.CreateAccommodationRequestDetails;
import com.example.odyssey.fragments.user.ProfileFragment;
import com.example.odyssey.fragments.review.ReviewSectionFragment;
import com.example.odyssey.model.TimeSlot;
import com.example.odyssey.model.reservations.AccreditReservation;
import com.example.odyssey.model.reservations.ReservationRequest;
import com.example.odyssey.model.users.User;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.Amenity;
import com.example.odyssey.model.accommodations.AvailabilitySlot;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationDetailsFragment extends Fragment {
    private Accommodation accommodation;
    private LinearLayout reservationInputSection;
    private ImageButton toggleReservationButton;
    private Date startDate;
    private Date endDate;
    private Integer numberOfGuests;

    LinearLayout map;
    MapView mapView;
    IMapController controller;
    private final Handler handler = new Handler(Looper.getMainLooper());
    MyLocationNewOverlay mMyLocationOverlay;
    View rootView;
    Marker pickedLocationMarker;

    private Long startDateLong;

    private Long endDateLong;

    LinearLayout reviewsSummaryContainer;

    public AccommodationDetailsFragment() {
    }

    private List<String> imageUrls = new ArrayList<>();
    private User loggedUser;

    public static AccommodationDetailsFragment newInstance() {
        return new AccommodationDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accommodation_details, container, false);
        this.rootView = view;
        getCurrentUser();
        accommodation = (Accommodation) getArguments().getSerializable("Accommodation");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addReviewSection();
        Set<Amenity> amenities = accommodation.getAmenities();
        reservationInputSection = view.findViewById(R.id.details_reservation_input_section);
        toggleReservationButton = view.findViewById(R.id.toggle_reservation_button);

        TextView minMaxGuests = view.findViewById(R.id.MinMaxGuests);

        view.findViewById(R.id.details_host_container).setOnClickListener(v -> {
            Bundle arg = new Bundle();
            arg.putLong(ProfileFragment.ARG_USER_ID, accommodation.getHost().getId());
            Navigation.findNavController(view).navigate(R.id.nav_profile, arg);
        });

        minMaxGuests.setText(accommodation.getMinGuests() + " - " + accommodation.getMaxGuests() + " guests");
        toggleReservationButton.setOnClickListener(v -> toggleReservationInput());

        TextView accommodationTitle = view.findViewById(R.id.details_title);
        loadImages();
        addAmenities(amenities);
        accommodationTitle.setText(accommodation.getTitle());

        TextView ratingSmall = view.findViewById(R.id.details_rating_small);
        if (accommodation.getAverageRating() != null)
            ratingSmall.setText(String.valueOf(accommodation.getAverageRating()));
        else
            ratingSmall.setText("0.0");

        ImageView hostImage = view.findViewById(R.id.hostImageView);
        String imagePath = ClientUtils.SERVICE_API_PATH + "users/image/" + accommodation.getHost().getId();
        Glide.with(requireContext()).load(imagePath).into(hostImage);
        TextView hostName = view.findViewById(R.id.details_host_name);
        hostName.setText("Hosted by " + (isOwnerMode() ? "you" : accommodation.getHost().getName()));
        Button editButton = view.findViewById(R.id.details_host_edit);
        editButton.setVisibility(isOwnerMode() ? View.VISIBLE : View.INVISIBLE);
        editButton.setOnClickListener(v -> {
            Bundle arg = new Bundle();
            arg.putLong(CreateAccommodationRequestDetails.ARG_ACCOMMODATION_ID, accommodation.getId());
            Navigation.findNavController(view).navigate(R.id.nav_accommodation_create_details, arg);
        });

        TextView detailsAddress = view.findViewById(R.id.details_address);
        String accommodationType;
        switch (accommodation.getType()) {
            case APARTMENT:
                accommodationType = "Apartment";
                break;
            case ROOM:
                accommodationType = "Room";
                break;
            case HOUSE:
                accommodationType = "House";
                break;
            default:
                accommodationType = null;
                break;
        }
        detailsAddress.setText(accommodation.getAddress().toString());
        TextView detailsDescription = view.findViewById(R.id.details_about);
        detailsDescription.setText(accommodation.getDescription());

        MaterialButton dateButton = view.findViewById(R.id.selectDateButtonReservation);
        TextView startingDate = view.findViewById(R.id.startDateTextReservation);
        TextView endingDate = view.findViewById(R.id.endDateTextReservation);
        TextView pricingType = view.findViewById(R.id.reservationPricingType);
        switch (accommodation.getPricing()) {
            case PER_NIGHT:
                pricingType.setText("per night");
                break;
            case PER_PERSON:
                pricingType.setText("per person");
                break;
            default:
                throw new IllegalArgumentException("Invalid pricing type");
        }

        dateButton.setOnClickListener(view1 -> {
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new Pair<>(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
            )).setCalendarConstraints(new CalendarConstraints.Builder()
                    .setValidator(new CalendarConstraints.DateValidator() {
                        @Override
                        public int describeContents() {
                            return 0;
                        }

                        @Override
                        public void writeToParcel(@NonNull Parcel dest, int flags) {

                        }

                        @Override
                        public boolean isValid(long date) {
                            return isDateAvailable(accommodation, date);
                        }
                    })
                    .build()).build();
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    startDateLong = selection.first;
                    endDateLong = selection.second;
                    startDate = new Date(selection.first);
                    endDate = new Date(selection.second);
                    String date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(startDate);
                    String date2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(endDate);

                    startingDate.setText(MessageFormat.format("Selected Starting Date: {0}", date1));
                    endingDate.setText(MessageFormat.format("Selected Ending Date: {0}", date2));
                    loadAccommodation(accommodation.getId(), startDate, endDate, numberOfGuests);
                }
            });

            materialDatePicker.show(getChildFragmentManager(), "tag");
        });

        TextInputEditText numberOfGuestsInput = view.findViewById(R.id.NumberOfGuestsEditTextReservation);
        numberOfGuestsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    numberOfGuests = Integer.parseInt(s.toString().trim());

                    handler.removeCallbacks(requestRunnable);

                    handler.postDelayed(requestRunnable, 1000);
                } catch (NumberFormatException ex) {
                    numberOfGuests = null;
                    Log.e(">.<", "Oopsie haha how did this get here :(");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadAccommodation(accommodation.getId(), startDate, endDate, numberOfGuests);
        MaterialButton sendReservationButton = view.findViewById(R.id.details_complete_button);
        sendReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReservation();
            }
        });

        map = view.findViewById(R.id.mapGoesHereDetails);
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(requireActivity().getPackageName());

        mapView = view.findViewById(R.id.osmmapDetails);
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
                    accommodation.getAddress().getStreet() + " ," +
                            accommodation.getAddress().getCity() + " ," +
                            accommodation.getAddress().getCountry(), 1);
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

    public boolean isDateAvailable(Accommodation accommodationInput, long dateMil) {

        LocalDateTime now = LocalDateTime.now();
        Instant instant = Instant.ofEpochMilli(dateMil);
        LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (date.isBefore(now)) {
            return false;
        }

        for (AvailabilitySlot availabilitySlot : accommodationInput.getAvailableSlots()) {

            TimeSlot timeSlot = availabilitySlot.getTimeSlot();
            if (timeSlot.coolerContainsDay(date.toLocalDate())) {
                return true;
            }
        }

        return false;
    }
    public void setImages() {
        ArrayList<SlideModel> imageList = new ArrayList<>();

        for (String imageUrl : this.imageUrls) {
            imageList.add(new SlideModel(imageUrl, ScaleTypes.CENTER_CROP));
        }

        if (this.rootView == null) {
            Log.e("REZ", "Root view is null");
            return;
        }
        ImageSlider imageSlider = this.rootView.findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);
        imageSlider.stopSliding();
    }

    public void loadImages() {
        if (accommodation == null || rootView == null) return;
        imageUrls = new ArrayList<>();

        Call<ArrayList<String>> call = ClientUtils.accommodationService.getImages(accommodation.getId());
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<String>> call, @NonNull Response<ArrayList<String>> response) {
                if (response.code() == 200) {
                    List<String> accommodationImages = response.body();
                    if (accommodationImages != null && accommodationImages.size() > 0) {
                        for (String image : accommodationImages) {
                            String imagePath = ClientUtils.SERVICE_API_PATH + "accommodations/" + accommodation.getId() + "/images/" + image;
                            imageUrls.add(imagePath);
                            setImages();
                        }
                    }
                } else {
                    Log.d("REZ", "Bad");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<String>> call, @NonNull Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
    public void loadAccommodation(Long accommodationId, Date startDate, Date endDate, Integer numberOfGuests) {
        Long startDateLong = startDate != null ? startDate.getTime() : null;
        Long endDateLong = endDate != null ? endDate.getTime() : null;
        Call<Accommodation> call = ClientUtils.accommodationService.getAccommodationWithPrice(accommodationId, startDateLong, endDateLong, numberOfGuests);
        call.enqueue(new Callback<Accommodation>() {
            @Override
            public void onResponse(@NonNull Call<Accommodation> call, @NonNull Response<Accommodation> response) {
                if (response.code() == 200) {
                    Accommodation accommodationResult = response.body();
                    if (accommodationResult != null) {
                        accommodation = accommodationResult;
                        fillReservationData();

                    }
                } else {
                    Log.d("REZ", "Bad");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Accommodation> call, @NonNull Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void addReviewSection(){
        LinearLayout reviewSectionContainer = getView().findViewById(R.id.accommodation_details_reviews_section_container);
        ReviewSectionFragment reviewSectionFragment = new ReviewSectionFragment();
        Bundle args = new Bundle();
        args.putSerializable("id", accommodation.getId());
        args.putSerializable("type", "ACCOMMODATION");
        reviewSectionFragment.setArguments(args);
        getChildFragmentManager().beginTransaction()
                .add(reviewSectionContainer.getId(), reviewSectionFragment)
                .commit();
    }

    public void addAmenities(Set<Amenity> amenities) {
        LinearLayout amenitiesContainer = rootView.findViewById(R.id.details_amenities_container);

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

    private void toggleReservationInput() {
        if (reservationInputSection.getVisibility() == View.VISIBLE) {
            reservationInputSection.setVisibility(View.GONE);
            toggleReservationButton.setImageResource(R.drawable.ic_arrow_up_icon);
        } else {
            reservationInputSection.setVisibility(View.VISIBLE);
            toggleReservationButton.setImageResource(R.drawable.ic_arrow_down_icon);
        }
    }

    private void fillReservationData() {
        TextView pricingCurrency = rootView.findViewById(R.id.perPricingCurrency);
        TextView pricingAmount = rootView.findViewById(R.id.perPricingAmount);
        TextView pricingTypeView = rootView.findViewById(R.id.reservationPricingType);
        TextView totalPrice = rootView.findViewById(R.id.reservationTotalPrice);
        TextView guestAmount = rootView.findViewById(R.id.reservationGuestAmount);
        if (accommodation.getDefaultPrice() == null || accommodation.getDefaultPrice() < 0) {
            pricingCurrency.setVisibility(View.GONE);
            String priceNotAvailableText = "Price not available";
            pricingAmount.setText(priceNotAvailableText);
            pricingTypeView.setVisibility(View.GONE);
        } else {
            pricingCurrency.setVisibility(View.VISIBLE);
            pricingAmount.setText(String.valueOf(accommodation.getDefaultPrice()));
            pricingTypeView.setVisibility(View.VISIBLE);
        }

        if (accommodation.getTotalPrice() == null || accommodation.getTotalPrice() < 0) {
            totalPrice.setVisibility(View.GONE);
        } else {
            totalPrice.setVisibility(View.VISIBLE);
            String totalPriceText = "$" + accommodation.getTotalPrice().toString() + " Total";
            totalPrice.setText(totalPriceText);
        }

        if (numberOfGuests == null || numberOfGuests < 0) {
            String noGuestsText = "No guests";
            guestAmount.setText(noGuestsText);
        } else {
            String guestsText = numberOfGuests + (numberOfGuests == 1 ? " guest" : " guests");
            guestAmount.setText(guestsText);
        }


    }

    private void sendReservation() {
        if (startDate == null || endDate == null || numberOfGuests == null || numberOfGuests <= 0) {
            Toast.makeText(requireActivity(), "Invalid input data", Toast.LENGTH_LONG).show();
            return;
        }
        if (TokenUtils.getId(requireContext()) == null || TokenUtils.getRole(requireContext()).equals("USER")) {
            Toast.makeText(requireActivity(), "You must be logged in as a user to make a reservation", Toast.LENGTH_LONG).show();
            return;
        }
        Long rid = 1L;
        Instant instantStart = Instant.ofEpochMilli(startDateLong);
        Instant instantEnd = Instant.ofEpochMilli(endDateLong);
        LocalDateTime startDateLocalDate = instantStart.atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endDateLocalDate = instantEnd.atZone(ZoneId.systemDefault()).toLocalDateTime();
        TimeSlot timeSlot = new TimeSlot(startDateLocalDate, endDateLocalDate);
        ReservationRequest reservation = new ReservationRequest(rid, accommodation.getTotalPrice(), numberOfGuests, ReservationRequest.Status.REQUESTED, LocalDateTime.now(), timeSlot, accommodation.getId(), loggedUser.getId());

        Call<ResponseBody> getUserCall = ClientUtils.reservationService.createReservation(reservation);
        Log.e("REZ", "Sending reservation request");
        getUserCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireActivity(), "Reservation request sent", Toast.LENGTH_LONG).show();
                } else {
                    loggedUser = null;
                    Toast.makeText(requireActivity(), "Error sending reservation request", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                loggedUser = null;
                t.printStackTrace();
            }
        });

    }

    private void getCurrentUser() {
        Call<User> getUserCall = ClientUtils.userService.findById(TokenUtils.getId(requireContext()));
        getUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    loggedUser = response.body();
                } else {
                    loggedUser = null;
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                loggedUser = null;
                t.printStackTrace();
            }
        });
    }

    private final Runnable requestRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                loadAccommodation(accommodation.getId(), startDate, endDate, numberOfGuests);
            } catch (NumberFormatException e) {
                Log.e("OOPS", "Error >.<");
            }
        }
    };

    private boolean isOwnerMode() {
        return accommodation.getHost().getId().equals(TokenUtils.getId(requireContext()));
    }
}