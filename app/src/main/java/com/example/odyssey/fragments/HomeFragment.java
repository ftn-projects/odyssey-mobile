package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.User;
import com.example.odyssey.model.accommodations.Amenity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    private ArrayList<Amenity> amenities = new ArrayList<>();

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


        AccommodationCard accommodationCard = rootView.findViewById(R.id.accommodationCard1);
        accommodationCard.setOnClickListener(view -> {
            AccommodationDetailsFragment accommodationDetailsFragment = new AccommodationDetailsFragment();
            Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigate(R.id.nav_accommodation_details);

        });

        return rootView;
    }

    private void getDataFromClient(){
        Call<ArrayList<Amenity>> call = ClientUtils.amenityService.getAll();
        call.enqueue(new Callback<ArrayList<Amenity>>() {
            @Override
            public void onResponse(Call<ArrayList<Amenity>> call, Response<ArrayList<Amenity>> response) {
                if (response.code() == 200){
                    Log.d("REZ","Meesage recieved");
                    amenities = response.body();

                    for (Amenity amenity: amenities){
                        Log.d("REZ", amenity.getTitle());
                    }

                }else{
                    Log.d("REZ","Meesage recieved: "+response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Amenity>> call, Throwable t) {
                Log.d("REZ", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDataFromClient();
    }
    private void showPopup() {
        FilterPopupDialog dialog = new FilterPopupDialog();
        dialog.show(requireActivity().getSupportFragmentManager(), "filterPopupDialog");
    }

    private void showSearchPopup(){
        SearchPopupDialog dialog = new SearchPopupDialog();
        dialog.show(requireActivity().getSupportFragmentManager(), "searchPopupDialog");
    }
}