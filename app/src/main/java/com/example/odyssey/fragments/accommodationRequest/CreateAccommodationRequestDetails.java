package com.example.odyssey.fragments.accommodationRequest;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.utils.TokenUtils;
import com.example.odyssey.utils.Validation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccommodationRequestDetails extends Fragment {
    public static final String ARG_REQUEST = "request";
    public static final String ARG_ACCOMMODATION_ID = "accommodationId";
    private AccommodationRequest request = null;
    TextInputLayout inputTitle, inputDescription, inputMin, inputMax, inputPrice, inputCancel;
    TextInputEditText editTitle, editDescription, editMin, editMax, editPrice, editCancel;
    AutoCompleteTextView accommodationType, priceType, confirmationType;
    Button nextBtn;

    public CreateAccommodationRequestDetails() {
    }

    public static CreateAccommodationRequestDetails newInstance() {
        return new CreateAccommodationRequestDetails();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Long accommodationId = null;
        if (getArguments() != null && getArguments().containsKey(ARG_ACCOMMODATION_ID))
            accommodationId = getArguments().getLong(ARG_ACCOMMODATION_ID);

        if (getArguments() != null && getArguments().containsKey(ARG_REQUEST))
            request = (AccommodationRequest) getArguments().getSerializable(ARG_REQUEST);
        else {
            request = new AccommodationRequest();
            request.setHostId(TokenUtils.getId());
            request.setAccommodationId(accommodationId);
            request.setRequestType(accommodationId == null ?
                    AccommodationRequest.Type.CREATE : AccommodationRequest.Type.UPDATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_accommodation_details, container, false);
        initializeElements(v);

        if (request.getRequestType().equals(AccommodationRequest.Type.UPDATE)) {
            loadAccommodation(request.getAccommodationId());
        } else loadData();

        nextBtn.setOnClickListener(v1 -> {
            if (!validFields()) return;
            collectData();

            Bundle args = new Bundle();
            args.putSerializable(ARG_REQUEST, request);
            Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_create_map, args);
        });

        return v;
    }

    private void initializeElements(View v) {
        nextBtn = v.findViewById(R.id.buttonCreate);

        inputTitle = v.findViewById(R.id.inputTitle);
        editTitle = v.findViewById(R.id.inputEditTitle);
        editTitle.addTextChangedListener(new ValidationTextWatcher(editTitle));

        inputDescription = v.findViewById(R.id.inputDescription);
        editDescription = v.findViewById(R.id.inputEditDescription);
        editDescription.addTextChangedListener(new ValidationTextWatcher(editDescription));

        inputMax = v.findViewById(R.id.inputMaxGuests);
        editMax = v.findViewById(R.id.inputEditMaxGuests);
        editMax.addTextChangedListener(new ValidationTextWatcher(editMax));

        inputMin = v.findViewById(R.id.inputMinGuests);
        editMin = v.findViewById(R.id.inputEditMinGuests);
        editMin.addTextChangedListener(new ValidationTextWatcher(editMin));

        inputPrice = v.findViewById(R.id.inputPrice);
        editPrice = v.findViewById(R.id.inputEditPrice);
        editPrice.addTextChangedListener(new ValidationTextWatcher(editPrice));

        inputCancel = v.findViewById(R.id.inputCancel);
        editCancel = v.findViewById(R.id.inputEditCancel);
        editCancel.addTextChangedListener(new ValidationTextWatcher(editCancel));

        priceType = v.findViewById(R.id.dropdownPriceType);
        accommodationType = v.findViewById(R.id.dropdownAccommodationType);
        confirmationType = v.findViewById(R.id.dropdownConfirmationType);
    }

    private void loadAccommodation(Long accommodationId) {
        ClientUtils.accommodationService.findById(accommodationId).enqueue(new Callback<Accommodation>() {
            @Override
            public void onResponse(@NonNull Call<Accommodation> call, @NonNull Response<Accommodation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadAccommodationWithImages(response.body());
                } else {
                    Toast.makeText(requireContext(), "Error loading accommodation", Toast.LENGTH_SHORT).show();
                    Log.e("AccommodationRequest", "Error loading accommodation");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Accommodation> call, @NonNull Throwable t) {
                Log.e("AccommodationRequest", t.getMessage() != null ? t.getMessage() : "Error loading accommodation");
            }
        });
    }

    private void loadAccommodationWithImages(Accommodation accommodation) {
        ClientUtils.accommodationService.getImages(accommodation.getId()).enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<String>> call, @NonNull Response<ArrayList<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    request.loadData(accommodation);
                    request.setRemoteImageNames(response.body());
                    loadData();
                } else {
                    Toast.makeText(requireContext(), "Error loading accommodation", Toast.LENGTH_SHORT).show();
                    Log.e("AccommodationRequest", "Error loading accommodation");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<String>> call, @NonNull Throwable t) {
                Log.e("AccommodationRequest", t.getMessage() != null ? t.getMessage() : "Error loading accommodation");
            }
        });
    }

    private boolean validFields() {
        return Validation.validateText(editTitle) &&
                Validation.validateText(editDescription) &&
                Validation.validateNumberCompare(editMax, editMin) &&
                Validation.validateNumber(editMin) &&
                Validation.validateDouble(editPrice) &&
                Validation.validateNumber(editCancel) &&
                Validation.validateSelection(priceType) &&
                Validation.validateSelection(confirmationType) &&
                Validation.validateSelection(accommodationType);
    }

    private void collectData() {
        request.setNewTitle(Objects.requireNonNull(
                editTitle.getText()).toString());
        request.setNewDescription(Objects.requireNonNull(
                editDescription.getText()).toString());
        request.setNewType(Accommodation.Type.valueOf(
                accommodationType.getText().toString().toUpperCase()));
        request.setNewPricing(Accommodation.getPricingType(
                priceType.getText().toString().toUpperCase()));
        request.setNewDefaultPrice(Double.parseDouble(Objects.requireNonNull(
                editPrice.getText()).toString()));
        request.setNewAutomaticApproval(
                confirmationType.getText().toString().equals("Automatic"));
        request.setNewCancellationDue(Long.parseLong(Objects.requireNonNull(
                editCancel.getText()).toString()));
        request.setNewMinGuests(Integer.parseInt(Objects.requireNonNull(
                editMin.getText()).toString()));
        request.setNewMaxGuests(Integer.parseInt(Objects.requireNonNull(
                editMax.getText()).toString()));
    }

    private void loadData() {
        if (request.getNewTitle() != null)
            editTitle.setText(request.getNewTitle());
        if (request.getNewDescription() != null)
            editDescription.setText(request.getNewDescription());
        if (request.getNewDefaultPrice() != null)
            editPrice.setText(String.valueOf(request.getNewDefaultPrice()));
        if (request.getNewCancellationDue() != null)
            editCancel.setText(String.valueOf(request.getNewCancellationDue()));
        if (request.getNewMinGuests() != null)
            editMin.setText(String.valueOf(request.getNewMinGuests()));
        if (request.getNewMaxGuests() != null)
            editMax.setText(String.valueOf(request.getNewMaxGuests()));
        if (request.getNewType() != null)
            accommodationType.setText(accommodationType.getAdapter().getItem(
                    request.getNewType().ordinal()).toString(), false);
        if (request.getNewPricing() != null)
            priceType.setText(priceType.getAdapter().getItem(
                    request.getNewPricing().ordinal()).toString(), false);
        if (request.getNewAutomaticApproval() != null)
            confirmationType.setText(confirmationType.getAdapter().getItem(
                    request.getNewAutomaticApproval() ? 0 : 1).toString(), false);
    }

    private class ValidationTextWatcher implements TextWatcher {
        private final View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (view.getId() == R.id.inputEditTitle)
                Validation.validateText(editTitle);
            else if (view.getId() == R.id.inputEditDescription)
                Validation.validateText(editDescription);
            else if (view.getId() == R.id.inputEditMaxGuests)
                Validation.validateNumberCompare(editMax, editMin);
            else if (view.getId() == R.id.inputEditMinGuests)
                Validation.validateNumber(editMin);
            else if (view.getId() == R.id.inputEditPrice)
                Validation.validateDouble(editPrice);
            else if (view.getId() == R.id.inputEditCancel)
                Validation.validateNumber(editCancel);
        }
    }
}