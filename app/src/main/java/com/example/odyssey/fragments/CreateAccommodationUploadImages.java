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
import android.widget.ImageButton;
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

    ArrayList<String> images =new ArrayList<>();
    ArrayList<Integer> removed = new ArrayList<>();
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
            for(int i:removed) images.remove(i);
            args.putSerializable("Request",accommodation);
            args.putStringArrayList("Images", images);
            Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_create_slots,args);
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
                    if (data != null && data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri imageUri = item.getUri();
                            images.add(imageUri.getPath());

                            // Inflate the layout containing both ImageView and ImageButton
                            View itemView = LayoutInflater.from(requireContext())
                                    .inflate(R.layout.fragment_image_view, null);

                            // Set the image URI to the ImageView
                            ImageView imageView = itemView.findViewById(R.id.imageView);
                            imageView.setImageURI(imageUri);

                            // Add the ImageButton click listener
                            ImageButton closeButton = itemView.findViewById(R.id.closeButton);
                            closeButton.setTag(images.size() - 1); // Set the tag to the index
                            closeButton.setOnClickListener(v -> onCloseButtonClick(v));

                            // Add the layout to the LinearLayout
                            LinearLayout linearLayout = v.findViewById(R.id.plsRadi);
                            linearLayout.addView(itemView);
                        }
                    } else if (data != null && data.getData() != null) {
                        // Inflate the layout containing both ImageView and ImageButton
                        View itemView = LayoutInflater.from(requireContext())
                                .inflate(R.layout.fragment_image_view, null);

                        // Set the image URI to the ImageView
                        ImageView imageView = itemView.findViewById(R.id.imageView);
                        imageView.setImageURI(data.getData());

                        // Add the ImageButton click listener
                        ImageButton closeButton = itemView.findViewById(R.id.closeButton);
                        closeButton.setTag(images.size() - 1); // Set the tag to the index
                        closeButton.setOnClickListener(v -> onCloseButtonClick(v));

                        // Add the layout to the LinearLayout
                        LinearLayout linearLayout = v.findViewById(R.id.plsRadi);
                        linearLayout.addView(itemView);
                    }
                }
            });

    // Close button click listener
    public void onCloseButtonClick(View view) {
        int index = (int) view.getTag();
        // Handle the close button click based on the index
        // Remove the corresponding image and update the UI
        removed.add(index);
        LinearLayout linearLayout = v.findViewById(R.id.plsRadi);
        linearLayout.removeView((View) view.getParent()); // Remove the entire layout
    }

}