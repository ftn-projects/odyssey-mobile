package com.example.odyssey.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.Amenity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements FilterPopupDialog.FilterDialogListener, SearchPopupDialog.SearchDialogListener {
    private Accommodation.Type type;
    private Float startPrice;
    private Float endPrice;
    private List<Amenity> amenities = new ArrayList<>();
    private Date startDate;
    private Date endDate;
    private String location;
    private Integer numberOfGuests;

    private ArrayList<Accommodation> accommodations = new ArrayList<>();
    private View rootView;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
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


//        AccommodationCard accommodationCard = rootView.findViewById(R.id.accommodationCard1);
//        accommodationCard.setOnClickListener(view -> {
//            AccommodationDetailsFragment accommodationDetailsFragment = new AccommodationDetailsFragment();
//            Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigate(R.id.nav_accommodation_details);
//
//        });

        this.rootView = rootView;
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
        fillSearchButton();
    }

    public void getAccommodations() {
        Long startDateTime = startDate != null ? startDate.getTime() : null;
        Long endDateTime = endDate != null ? endDate.getTime() : null;

        // Convert type to String
        String accommodationType = type != null ? type.toString() : null;

        // Create a list of amenity IDs
        List<Long> amenityIds = new ArrayList<>();
        for (Amenity amenity : amenities) {
            amenityIds.add(amenity.getId());
        }

        Log.e("REZ", "getAccommodations: " + startDateTime + " " + endDateTime + " " + accommodationType + " " + startPrice + " " + endPrice + " " + amenityIds + " " + location + " " + numberOfGuests);
        Call<ArrayList<Accommodation>> call = ClientUtils.accommodationService.getAll(
                accommodationType,
                startPrice,
                endPrice,
                amenityIds,
                startDateTime,
                endDateTime,
                location,
                numberOfGuests
        );
        call.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if(response.code()==200){
                    LinearLayout accommodationContainer = rootView.findViewById(R.id.accommodation_cards_container);
                    accommodationContainer.removeAllViews();
                    accommodations = response.body();
                    for(Accommodation accommodation: accommodations){

                        Log.d("REZ",accommodation.getTitle());
                        addAccommodationCardFragment(accommodation);
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

    private void addAccommodationCardFragment(Accommodation accommodation) {
        AccommodationCardFragment cardFragment = new AccommodationCardFragment(getContext());
        cardFragment.setAccommodation(accommodation);

        // Now you can add cardFragment to your layout container
        // For example, if you have a LinearLayout container:
        LinearLayout accommodationContainer = this.rootView.findViewById(R.id.accommodation_cards_container);
        accommodationContainer.addView(cardFragment);
    }

    private void fillSearchButton(){
        TextView searchButtonLocation = rootView.findViewById(R.id.searchButtonLocation);
        if(this.location != null){
            searchButtonLocation.setText(this.location);
        }
        else{
            searchButtonLocation.setText("Anywhere");
        }

        TextView searchButtonDates = rootView.findViewById(R.id.searchButtonDates);
        if(this.startDate != null && this.endDate != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("d. MMM", Locale.getDefault());
            String formattedStartDate = dateFormat.format(this.startDate);
            String formattedEndDate = dateFormat.format(this.endDate);

            String formattedDateRange = formattedStartDate + " - " + formattedEndDate;
            searchButtonDates.setText(formattedDateRange);
        }
        else{
            searchButtonDates.setText("Anytime");
        }

        TextView searchButtonGuests = rootView.findViewById(R.id.searchButtonGuests);
        if(this.numberOfGuests != null){
            searchButtonGuests.setText(this.numberOfGuests.toString() + " guests");
        }
        else{
            searchButtonGuests.setText("Anyone");
        }
    }
}