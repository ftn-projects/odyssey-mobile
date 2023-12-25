package com.example.odyssey.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.Amenity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements FilterPopupDialog.FilterDialogListener, SearchPopupDialog.SearchDialogListener {
    private Accommodation.Type type;
    private float startPrice;
    private float endPrice;
    private List<Amenity> amenities = new ArrayList<>();
    private Date startDate;
    private Date endDate;
    private String location;
    private Integer numberOfGuests;

    private ArrayList<Accommodation> accommodations = new ArrayList<>();

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("(¬‿¬)", "HomeFragment onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment;
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ImageButton showPopupButton = rootView.findViewById(R.id.filter_button);
        showPopupButton.setOnClickListener(view -> showPopup());

        LinearLayout searchContainerButton = rootView.findViewById(R.id.search_text_container);
        searchContainerButton.setOnClickListener(view -> showSearchPopup());

        ImageButton searchButton = rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(view -> getAccommodations());


        AccommodationCard accommodationCard = rootView.findViewById(R.id.accommodationCard1);
        accommodationCard.setOnClickListener(view -> {
            AccommodationDetailsFragment accommodationDetailsFragment = new AccommodationDetailsFragment();
            Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigate(R.id.nav_accommodation_details);

        });

        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void showPopup() {
        FilterPopupDialog dialog = new FilterPopupDialog();
        dialog.setFilterDialogListener(this);
        dialog.show(requireActivity().getSupportFragmentManager(), "filterPopupDialog");
    }

    private void showSearchPopup() {
        SearchPopupDialog dialog = new SearchPopupDialog();
        dialog.setSearchDialogListener(this);
        dialog.show(requireActivity().getSupportFragmentManager(), "searchPopupDialog");
    }

    @Override
    public void onFilterApplied(List<Amenity> selectedAmenities, float startPrice, float endPrice, Accommodation.Type type) {
        this.amenities = selectedAmenities;
        this.startPrice = startPrice;
        this.endPrice = endPrice;
        this.type = type;
        Log.i("(¬‿¬)", "HomeFragment onFilterApplied()");
        Log.i("(¬‿¬)", "HomeFragment onFilterApplied() selectedAmenities: " + this.amenities);
        Log.i("(¬‿¬)", "HomeFragment onFilterApplied() startPrice: " + this.startPrice);
        Log.i("(¬‿¬)", "HomeFragment onFilterApplied() endPrice: " + this.endPrice);
        Log.i("(¬‿¬)", "HomeFragment onFilterApplied() type: " + this.type);
    }

    @Override
    public void onSearchApplied(Date startDate, Date endDate, String location, Integer numberOfGuests) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.numberOfGuests = numberOfGuests;
        Log.i("(¬‿¬)", "HomeFragment onSearchApplied()");
        Log.i("(¬‿¬)", "HomeFragment onSearchApplied() startDate: " + this.startDate);
        Log.i("(¬‿¬)", "HomeFragment onSearchApplied() endDate: " + this.endDate);
        Log.i("(¬‿¬)", "HomeFragment onSearchApplied() location: " + this.location);
        Log.i("(¬‿¬)", "HomeFragment onSearchApplied() numberOfGuests: " + this.numberOfGuests);
    }

    public void getAccommodations() {
        Call<ArrayList<Accommodation>> call = ClientUtils.accommodationService.getAll();
        call.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.code() == 200) {
                    accommodations = response.body();
                    for (Accommodation accommodation : accommodations) {
                        Log.d("REZ", accommodation.getTitle());
                    }
                } else {
                    Log.d("REZ", "Bad");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
}