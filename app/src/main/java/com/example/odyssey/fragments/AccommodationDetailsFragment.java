package com.example.odyssey.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.odyssey.R;
import com.example.odyssey.clients.AmenityIconMapper;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.Amenity;
import com.google.android.material.carousel.MaskableFrameLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccommodationDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccommodationDetailsFragment extends Fragment {

    private Accommodation accommodation;

    private Set<Amenity> amenities = new HashSet<>();

    View rootView;

    public AccommodationDetailsFragment() {
        // Required empty public constructor
    }

    private List<String> imageUrls = new ArrayList<>();

    public static AccommodationDetailsFragment newInstance() {
        AccommodationDetailsFragment fragment = new AccommodationDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accommodation_details, container, false);
        this.rootView = view;
        accommodation = (Accommodation) getArguments().getSerializable("Accommodation");
        amenities = accommodation.getAmenities();
        TextView accommodationTitle = view.findViewById(R.id.details_title);
        loadImages();
        addAmenities(amenities);
        accommodationTitle.setText(accommodation.getTitle());

        TextView ratingSmall = view.findViewById(R.id.details_raintg_small);
        if(accommodation.getAverageRating()!=null)
            ratingSmall.setText(accommodation.getAverageRating().toString());
        else
            ratingSmall.setText("0.0");

        TextView hostName = view.findViewById(R.id.details_host_name);
        hostName.setText("Hosted by " + accommodation.getHost().getName());

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
        detailsAddress.setText(accommodationType + " in " + accommodation.getAddress().getCity() + ", " + accommodation.getAddress().getCountry());
        TextView detailsDescription = view.findViewById(R.id.details_about);
        detailsDescription.setText(accommodation.getDescription());
        return view;
    }

    public void setImages(){
        ArrayList<SlideModel> imageList = new ArrayList<>();

        for(String imageUrl : this.imageUrls){
            imageList.add(new SlideModel(imageUrl, ScaleTypes.CENTER_CROP));
        }

        if(this.rootView == null) {
            Log.e("REZ", "Root view is null");
            return;
        }
        ImageSlider imageSlider = this.rootView.findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);
        imageSlider.stopSliding();
    }
    public void loadImages(){
        if(accommodation == null || rootView == null){
            return;
        }
        Call<ArrayList<String>> call = ClientUtils.accommodationService.getImages(accommodation.getId());
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if(response.code()==200){
                    List<String> accommodationImages = response.body();
                    if(accommodationImages!=null && accommodationImages.size()>0){
                       for(String image:accommodationImages){
                           String imagePath = ClientUtils.SERVICE_API_PATH + "accommodations/" + accommodation.getId() + "/images/" + image;
                           imageUrls.add(imagePath);
                           setImages();
                       }
                    }
                }else{
                    Log.d("REZ","Bad");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Log.d("REZ",t.getMessage() != null?t.getMessage():"error");
            }
        });
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
}