package com.example.odyssey.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.Amenity;
import com.example.odyssey.model.accommodations.AvailabilitySlot;
import com.example.odyssey.model.stats.AccommodationTotalStats;
import com.example.odyssey.services.ShakeDetector;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private ShakeDetector shakeDetector;
    private List<Accommodation> accommodations = new ArrayList<>();
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

        shakeDetector = new ShakeDetector(requireContext());
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShakeDetected() {
                switchSpinner();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        shakeDetector.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        shakeDetector.stopListening();
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

        Spinner sortSpinner = rootView.findViewById(R.id.sortSpinner);

        List<String> entries = Arrays.asList("Title Ascending", "Title Descending");
        ArrayAdapter adapter = new ArrayAdapter(requireContext(), R.layout.my_selected_item, entries);
        adapter.setDropDownViewResource(R.layout.my_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection (optional)
            }
        });

        this.rootView = rootView;
        getAccommodations();
        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.favoriteButtonGroup);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (TokenUtils.getRole() == null || !TokenUtils.getRole().equals("GUEST")) {
                Toast.makeText(getContext(), "You must be logged in as a guest to use this feature", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (isChecked) {
                    getFavorites();
                } else {
                    populateAccommodationCards(accommodations);
                }
            }
        });
    }

    private void getFavorites() {
        Call<ArrayList<Accommodation>> call = ClientUtils.accommodationService.getFavorites(TokenUtils.getId());
        call.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.code() == 200) {
                    List<Accommodation> favoritesResponse = response.body();

                    List<Accommodation> commonAccommodations = accommodations.stream()
                            .filter(accommodation -> favoritesResponse.stream().anyMatch(fav -> fav.getId() == accommodation.getId()))
                            .collect(Collectors.toList());

                    populateAccommodationCards(commonAccommodations);
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

    private void spinnerSelected() {
        Spinner sortSpinner = rootView.findViewById(R.id.sortSpinner);
        int position = sortSpinner.getSelectedItemPosition();
        switch (position) {
            case 0:
                sortAscending();
                break;
            case 1:
                sortDescending();
                break;
            default:
                break;
        }
        populateAccommodationCards(accommodations);
    }

    private void switchSpinner() {
        Spinner sortSpinner = rootView.findViewById(R.id.sortSpinner);
        int position = sortSpinner.getSelectedItemPosition();
        switch (position) {
            case 0:
                setSpinnerSelection(1);
                break;
            case 1:
                setSpinnerSelection(0);
                break;
            default:
                break;
        }
    }

    private void setSpinnerSelection(int position) {
        Spinner sortSpinner = rootView.findViewById(R.id.sortSpinner);
        sortSpinner.setSelection(position);
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
    }

    @Override
    public void onSearchApplied(Date startDate, Date endDate, String location, Integer numberOfGuests) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.numberOfGuests = numberOfGuests;
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
                if (response.code() == 200) {
                    accommodations = response.body();
                    spinnerSelected();
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

    private void populateAccommodationCards(List<Accommodation> accommodations) {
        LinearLayout container = getView().findViewById(R.id.accommodation_cards_container);
        container.removeAllViews();
        for (Accommodation accommodation : accommodations) {
            AccommodationCard fragment = new AccommodationCard();

            Bundle args = new Bundle();
            args.putSerializable("accommodation", accommodation);
            fragment.setArguments(args);

            // Add the fragment to the reviewsContainer
            getChildFragmentManager().beginTransaction()
                    .add(container.getId(), fragment)
                    .commit();
        }
    }

    private void fillSearchButton() {
        TextView searchButtonLocation = rootView.findViewById(R.id.searchButtonLocation);
        if (this.location != null) {
            searchButtonLocation.setText(this.location);
        } else {
            searchButtonLocation.setText("Anywhere");
        }

        TextView searchButtonDates = rootView.findViewById(R.id.searchButtonDates);
        if (this.startDate != null && this.endDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d. MMM", Locale.getDefault());
            String formattedStartDate = dateFormat.format(this.startDate);
            String formattedEndDate = dateFormat.format(this.endDate);

            String formattedDateRange = formattedStartDate + " - " + formattedEndDate;
            searchButtonDates.setText(formattedDateRange);
        } else {
            searchButtonDates.setText("Anytime");
        }

        TextView searchButtonGuests = rootView.findViewById(R.id.searchButtonGuests);
        if (this.numberOfGuests != null) {
            searchButtonGuests.setText(this.numberOfGuests.toString() + " guests");
        } else {
            searchButtonGuests.setText("Anyone");
        }
    }

    private void sortAscending() {
        accommodations = accommodations.stream()
                .sorted(Comparator.comparing(Accommodation::getTitle))
                .collect(Collectors.toList());
    }

    private void sortDescending() {
        accommodations = accommodations.stream()
                .sorted(Comparator.comparing(Accommodation::getTitle).reversed())
                .collect(Collectors.toList());
    }
}