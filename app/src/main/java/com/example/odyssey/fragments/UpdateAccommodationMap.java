package com.example.odyssey.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.accommodations.AvailabilitySlot;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAccommodationMap extends Fragment implements MapListener {
    private AccommodationRequest accommodation;
    ArrayList<String> images = new ArrayList<>();
    View v;
    LinearLayout map;
    Button create;

    MapView mapView;
    IMapController controller;
    MyLocationNewOverlay mMyLocationOverlay;
    Marker pickedLocationMarker;
    boolean isLocationPicked = false;
    public UpdateAccommodationMap() {

    }

    public static UpdateAccommodationMap newInstance(String param1, String param2) {
        return new UpdateAccommodationMap();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        accommodation = (AccommodationRequest) getArguments().getSerializable("Request");
        images = getArguments().getStringArrayList("Images");
        v = inflater.inflate(R.layout.fragment_create_accommodation_map, container, false);
        map = v.findViewById(R.id.mapGoesHere);

        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(requireActivity().getPackageName());

        mapView = v.findViewById(R.id.osmmap);
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
        mapView.addMapListener(this);

        pickedLocationMarker = new Marker(mapView);

        MapEventsReceiver mReceive = new MapEventsReceiver() {@Override
        public boolean singleTapConfirmedHelper(GeoPoint p) {
            //Toast.makeText(requireContext(),p.getLatitude() + " - "+p.getLongitude(),Toast.LENGTH_LONG).show();

            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            String result = null;
            String street = "", city = "", country = "";
            try {
                List<Address> addressList = geocoder.getFromLocation(
                        p.getLatitude(), p.getLongitude(), 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    street = address.getThoroughfare() + " " +address.getSubThoroughfare();
                    city = address.getLocality();
                    country = address.getCountryName();
                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getSubThoroughfare()).append("\n");
                    sb.append(address.getThoroughfare()).append("\n");
                    sb.append(address.getCountryName());
                    result = sb.toString();
                }
            } catch (IOException e) {
                Log.e("TAG", "Unable connect to Geocoder", e);
            }
            Log.d("HELP", result);

            Toast.makeText(requireContext(),street + ", " + city + ", "  + country ,Toast.LENGTH_LONG).show();

            accommodation.setNewAddress(new com.example.odyssey.model.Address(street, city, country));

            pickedLocationMarker.setPosition(new GeoPoint(p.getLatitude(),p.getLongitude())); //gde stavlja marker
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

        create = v.findViewById(R.id.buttonCreate);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "Accommodation request created", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigate(R.id.nav_home);
            }
        });

        loadAccommodationRequest();
        return v;
    }

    private void loadAccommodationRequest() {
        Long id = 2L;
        ClientUtils.accommodationService.getOne(id).enqueue(new Callback<Accommodation>() {
            @Override
            public void onResponse(@NonNull Call<Accommodation> call, @NonNull Response<Accommodation> response) {
                if (!response.isSuccessful()) {
                    Log.e("ACCOMMODATION REQUEST", response.message() != null ? response.message() : "error");
                    return;
                }
                updateFormWithData(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Accommodation> call, @NonNull Throwable t) {
                Log.e("ACCOMMODATION REQUEST", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void updateFormWithData(Accommodation accommodation) {
        com.example.odyssey.model.Address address = new com.example.odyssey.model.Address(
                accommodation.getAddress().getStreet(), accommodation.getAddress().getCity(), accommodation.getAddress().getCountry()
        );
        this.accommodation.setNewAddress(address);

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        GeoPoint p;

        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocationName(
                    address.getStreet() + ", " + address.getCity() + ", " + address.getCountry(),
                    1
            );
            if (addressList != null && addressList.size() > 0) {
                Address first = addressList.get(0);
                p = new GeoPoint(first.getLatitude(), first.getLongitude());
                first.getLongitude();

                pickedLocationMarker.setPosition(new GeoPoint(p.getLatitude(),p.getLongitude()));
                pickedLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mapView.getOverlays().add(pickedLocationMarker);
            }
        } catch (IOException e) {
            Log.e("TAG", "Unable connect to Geocoder", e);
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
}