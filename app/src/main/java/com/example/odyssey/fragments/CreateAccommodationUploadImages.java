package com.example.odyssey.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.utils.ImageAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreateAccommodationUploadImages extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    Button selectImageButton;
    Button nextBtn;
    Button backBtn;
    View v;

    ArrayList<String> images;
    private AccommodationRequest accommodation;

    public CreateAccommodationUploadImages() {
    }

    public static CreateAccommodationUploadImages newInstance(String param1, String param2) {
        CreateAccommodationUploadImages fragment = new CreateAccommodationUploadImages();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_create_accommodation_upload_images, container, false);
        accommodation = (AccommodationRequest) getArguments().getSerializable("Request");

        nextBtn = v.findViewById(R.id.buttonNext);
        backBtn = v.findViewById(R.id.buttonBack);

        selectImageButton = v.findViewById(R.id.buttonUpload);
        selectImageButton.setOnClickListener(c -> imageChooser());
        nextBtn.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putSerializable("Request",accommodation);
            args.putStringArrayList("Image", images);
            Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_create_details,args);
        });
        backBtn.setOnClickListener(c -> Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigate(R.id.nav_accommodation_create_amenities));
        return v;
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeOtherActivity.launch(i);
    }

        ActivityResultLauncher<Intent> launchSomeOtherActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // Check if the data is not null and contains images
                        if (data != null && data.getClipData() != null) {
                            ClipData clipData = data.getClipData();
                            // Iterate through selected images
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                Uri imageUri = item.getUri();
                                images.add(imageUri.getPath());
                                // Create an ImageView dynamically
                                ImageView imageView = new ImageView(requireContext());
                                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                                imageView.setImageURI(imageUri);
                                // Add the ImageView to the LinearLayout
                                LinearLayout linearLayout = v.findViewById(R.id.plsRadi);
                                linearLayout.addView(imageView);
                            }
                        } else if (data != null && data.getData()!=null) {
                            ImageView imageView = new ImageView(requireContext());
                            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            imageView.setImageURI(data.getData());
                            images.add(data.getData().getPath());
                            // Add the ImageView to the LinearLayout
                            LinearLayout linearLayout = v.findViewById(R.id.plsRadi);
                            linearLayout.addView(imageView);
                        }
                    }
                });

}