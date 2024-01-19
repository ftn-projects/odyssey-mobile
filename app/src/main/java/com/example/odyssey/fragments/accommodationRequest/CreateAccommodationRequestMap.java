package com.example.odyssey.fragments.accommodationRequest;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.utils.Validation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CreateAccommodationRequestMap extends Fragment implements MapListener {
    private static final String ARG_REQUEST = "request";
    private AccommodationRequest request = null;

    MapView mapView;
    LinearLayout map;
    IMapController controller;
    MyLocationNewOverlay mMyLocationOverlay;
    Marker pickedLocationMarker;

    TextInputLayout addressInput, cityInput, countryInput;
    TextInputEditText addressEdit, cityEdit, countryEdit;
    Button nextBtn, backBtn;
    View v;

    LocalDateTime lastChange = null;

    public CreateAccommodationRequestMap() {
    }

    public static CreateAccommodationRequestMap newInstance() {
        return new CreateAccommodationRequestMap();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_REQUEST))
            request = (AccommodationRequest) getArguments().getSerializable(ARG_REQUEST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_create_accommodation_map, container, false);
        initializeElemenets(v);
        initializeMapEventReceiver();
        loadData();

        nextBtn.setOnClickListener(v -> navigateTo(R.id.nav_accommodation_create_amenities));
        backBtn.setOnClickListener(c -> navigateTo(R.id.nav_accommodation_create_details));

        return v;
    }

    private void navigateTo(int id) {
        if (validFields()) return;
        collectData();
        Bundle args = new Bundle();
        args.putSerializable(ARG_REQUEST, request);
        Navigation.findNavController(requireView()).navigate(id, args);
    }

    private void initializeElemenets(View v) {
        map = v.findViewById(R.id.mapGoesHere);

        addressInput = v.findViewById(R.id.inputAddress);
        addressEdit = v.findViewById(R.id.inputEditAddress);
        addressEdit.addTextChangedListener(new ValidationTextWatcher(addressEdit));
        addressEdit.addTextChangedListener(new MapUpdateTextWatcher());

        cityInput = v.findViewById(R.id.inputCity);
        cityEdit = v.findViewById(R.id.inputEditCity);
        cityEdit.addTextChangedListener(new ValidationTextWatcher(cityEdit));
        cityEdit.addTextChangedListener(new MapUpdateTextWatcher());

        countryInput = v.findViewById(R.id.inputCountry);
        countryEdit = v.findViewById(R.id.inputEditCountry);
        countryEdit.addTextChangedListener(new ValidationTextWatcher(countryEdit));
        countryEdit.addTextChangedListener(new MapUpdateTextWatcher());

        nextBtn = v.findViewById(R.id.buttonCreate);
        backBtn = v.findViewById(R.id.buttonBack);

        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(requireActivity().getPackageName());

        mapView = v.findViewById(R.id.osmmap);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        controller = mapView.getController();
        controller.setZoom(20.0);

        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireActivity()), mapView);
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);

        GeoPoint startPoint = new GeoPoint(45.25167, 19.83694); //gde te postavi kad otvori mapu
        controller.setCenter(startPoint);

        mapView.getOverlays().add(mMyLocationOverlay);
        mapView.addMapListener(this);

        pickedLocationMarker = new Marker(mapView);
    }

    private void initializeMapEventReceiver() {
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            p.getLatitude(), p.getLongitude(), 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                            sb.append(address.getAddressLine(i)).append("\n");

                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getSubThoroughfare()).append("\n");
                        sb.append(address.getThoroughfare()).append("\n");
                        sb.append(address.getCountryName());

                        String street = address.getThoroughfare() + " " + address.getSubThoroughfare();
                        addressEdit.setText(street);
                        cityEdit.setText(address.getLocality());
                        countryEdit.setText(address.getCountryName());
                    }
                } catch (IOException e) {
                    Log.e("TAG", "Unable connect to Geocoder", e);
                }

                pickedLocationMarker.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude())); // position set
                pickedLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mapView.getOverlays().add(pickedLocationMarker);
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(requireContext(), mReceive);
        mapView.getOverlays().add(OverlayEvents);
    }

    private void loadData() {
        addressEdit.setText(request.getNewAddress().getStreet());
        cityEdit.setText(request.getNewAddress().getCity());
        countryEdit.setText(request.getNewAddress().getCountry());
        updateMap();
    }

    private void updateMap() {
        collectData();

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            com.example.odyssey.model.Address address = request.getNewAddress();
            Address result = Objects.requireNonNull(geocoder.getFromLocationName(
                    address.getStreet() + " ," + address.getCity() + " ," +
                            address.getCountry(), 1)).get(0);
            GeoPoint startPoint = new GeoPoint(result.getLatitude(), result.getLongitude());
            controller.setCenter(startPoint);
            pickedLocationMarker.setPosition(startPoint);
            pickedLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(pickedLocationMarker);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validFields() {
        return !Validation.validateLettersAndNumber(addressEdit) ||
                !Validation.validateText(cityEdit) ||
                !Validation.validateText(countryEdit);
    }

    private void collectData() {
        request.setNewAddress(new com.example.odyssey.model.Address(
                Objects.requireNonNull(addressEdit.getText()).toString(),
                Objects.requireNonNull(cityEdit.getText()).toString(),
                Objects.requireNonNull(countryEdit.getText()).toString()));
    }

    private class MapUpdateTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            lastChange = LocalDateTime.now();

            ScheduledExecutorService backgroundExecutor = Executors.newSingleThreadScheduledExecutor();
            backgroundExecutor.schedule(() -> {
                requireActivity().runOnUiThread(() -> {
                    if (Duration.between(lastChange, LocalDateTime.now()).toMillis() >= 800)
                        updateMap();
                });
            }, 1000, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean onScroll(ScrollEvent event) {
        return false;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        return false;
    }

    private class ValidationTextWatcher implements TextWatcher {
        private final View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (view.getId() == R.id.inputEditAddress)
                Validation.validateLettersAndNumber(addressEdit);
            else if (view.getId() == R.id.inputEditCity)
                Validation.validateText(cityEdit);
            else if (view.getId() == R.id.inputEditCountry)
                Validation.validateText(countryEdit);
        }
    }
}