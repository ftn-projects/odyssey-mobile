package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements FilterPopupDialog.FilterDialogListener, SearchPopupDialog.SearchDialogListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View rootView;

    private Accommodation.Type type;
    private Float startPrice;
    private Float endPrice;
    private List<Amenity> amenities = new ArrayList<>();
    private Date startDate;
    private Date endDate;
    private String location;
    private Integer numberOfGuests;

    private ArrayList<Accommodation> accommodations = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


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
        getAccommodations();
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

    private void showSearchPopup(){
        SearchPopupDialog dialog = new SearchPopupDialog();
        dialog.setSearchDialogListener(this);
        dialog.show(requireActivity().getSupportFragmentManager(), "searchPopupDialog");
    }

    @Override
    public void onFilterApplied(List<Amenity> selectedAmenities, float startPrice, float endPrice, Accommodation.Type type){
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

    public void getAccommodations(){
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
                }else{
                    Log.d("REZ","Bad");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("REZ",t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void addAccommodationCardFragment(Accommodation accommodation) {
        AccommodationCardFragment cardFragment = new AccommodationCardFragment(getContext());
        cardFragment.setAccommodation(accommodation);
        cardFragment.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putSerializable("Accommodation",accommodation);
            Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_details,args);

        });

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