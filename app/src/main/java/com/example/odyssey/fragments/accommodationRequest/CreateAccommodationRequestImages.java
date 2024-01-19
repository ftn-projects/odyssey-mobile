package com.example.odyssey.fragments.accommodationRequest;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.utils.FileUploadUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccommodationRequestImages extends Fragment {
    private static final String ARG_REQUEST = "request";
    private AccommodationRequest request = null;
    Button selectImageButton, createBtn, backBtn;
    LinearLayout imagesLayout;
    View v;

    ArrayList<Uri> images = new ArrayList<>();

    public CreateAccommodationRequestImages() {
    }

    public static CreateAccommodationRequestImages newInstance() {
        return new CreateAccommodationRequestImages();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_REQUEST))
            request = (AccommodationRequest) getArguments().getSerializable(ARG_REQUEST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_create_accommodation_upload_images, container, false);
        imagesLayout = v.findViewById(R.id.plsRadi);
        createBtn = v.findViewById(R.id.buttonCreate);
        backBtn = v.findViewById(R.id.buttonBack);
        selectImageButton = v.findViewById(R.id.buttonUpload);
        selectImageButton.setOnClickListener(c -> imageChooser());

        loadImages();

        createBtn.setOnClickListener(v -> {
            collectImages();
            ClientUtils.accommodationRequestService.create(request).enqueue(new Callback<AccommodationRequest>() {
                @Override
                public void onResponse(@NonNull Call<AccommodationRequest> call, @NonNull Response<AccommodationRequest> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireActivity(), "Accommodation request created", Toast.LENGTH_SHORT).show();
                        assert response.body() != null;
                        uploadImages(response.body().getId());
                        Navigation.findNavController(requireView()).navigate(R.id.nav_home);
                    } else {
                        Toast.makeText(requireActivity(), "Could not make a reservation request", Toast.LENGTH_SHORT).show();
                        Log.e("CreateAccommodationMap", response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AccommodationRequest> call, @NonNull Throwable t) {
                    Log.e("CreateAccommodationMap", t.getMessage() != null ? t.getMessage() : "Could not make a reservation request");
                }
            });
        });
        backBtn.setOnClickListener(c -> {
            collectImages();
            Bundle args = new Bundle();
            args.putSerializable(ARG_REQUEST, request);
            Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_create_slots, args);
        });
        return v;
    }

    private void uploadImages(Long requestId) {
        images.forEach(uri -> {
            try {
                MultipartBody.Part body = FileUploadUtils.createRequestBody(uri, requireContext());
                ClientUtils.accommodationRequestService.uploadImage(requestId, body).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (!response.isSuccessful())
                            Log.e("CreateAccommodationMap", "IMAGE NOT UPLOADED: " + response.message());
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.e("CreateAccommodationMap", "IMAGE NOT UPLOADED: " + t.getMessage());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeOtherActivity.launch(i);
    }

    private void loadImages() {
        request.getLocalImageUris().forEach(this::inflateImage);
        request.getRemoteImageNames().forEach(this::inflateRemoteImage);
    }

    private void collectImages() {
        request.setLocalImageUris(images);
        request.setNewImages(images.stream().map(uri -> uri.getLastPathSegment() + ".png")
                .collect(Collectors.toSet()));
        request.getNewImages().addAll(request.getRemoteImageNames());
    }

    ActivityResultLauncher<Intent> launchSomeOtherActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getClipData() != null) {
                        ClipData clipData = data.getClipData();

                        for (int i = 0; i < data.getClipData().getItemCount(); i++)
                            inflateImage(clipData.getItemAt(i).getUri());
                    } else if (data != null && data.getData() != null)
                        inflateImage(data.getData());
                }
            });

    private void inflateImage(Uri imageUri) {
        images.add(imageUri);

        View itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_image_view, null);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);

        ImageButton closeButton = itemView.findViewById(R.id.closeButton);
        closeButton.setTag(images.size());
        closeButton.setOnClickListener(v -> {
            images.removeIf(uri -> uri.equals(imageUri));
            imagesLayout.removeView(itemView);
        });
        imagesLayout.addView(itemView);
    }

    private void inflateRemoteImage(String imageName) {
        View itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_image_view, null);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        String imagePath = ClientUtils.SERVICE_API_PATH +
                "accommodations/" + request.getAccommodationId() + "/images/" + imageName;
        Glide.with(requireContext()).load(imagePath).into(imageView);

        ImageButton closeButton = itemView.findViewById(R.id.closeButton);
        closeButton.setTag(images.size());
        closeButton.setOnClickListener(v -> {
            request.getRemoteImageNames().removeIf(uri -> uri.equals(imageName));
            imagesLayout.removeView(itemView);
        });
        imagesLayout.addView(itemView);
    }

}