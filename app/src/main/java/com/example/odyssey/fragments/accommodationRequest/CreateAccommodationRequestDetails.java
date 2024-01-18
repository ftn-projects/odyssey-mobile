package com.example.odyssey.fragments.accommodationRequest;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.utils.TokenUtils;
import com.example.odyssey.utils.Validation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class CreateAccommodationRequestDetails extends Fragment {
    private static final String ARG_REQUEST = "Request";
    private static final String ARG_ACCOMMODATION_ID = "Accommodation id";
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
        loadData();

        nextBtn.setOnClickListener(v1 -> {
            if (!validFields()) return;
            collectData();

            Bundle args = new Bundle();
            args.putSerializable("Request", request);
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

    private boolean validFields() {
        return Validation.validateText(inputTitle, editTitle, requireActivity().getWindow()) &&
                Validation.validateText(inputDescription, editDescription, requireActivity().getWindow()) &&
                Validation.validateNumberCompare(inputMax, editMax, editMin, requireActivity().getWindow()) &&
                Validation.validateNumber(inputMin, editMin, requireActivity().getWindow()) &&
                Validation.validateDouble(inputPrice, editPrice, requireActivity().getWindow()) &&
                Validation.validateNumber(inputCancel, editCancel, requireActivity().getWindow()) &&
                Validation.validateSelection(priceType, requireActivity().getWindow()) &&
                Validation.validateSelection(confirmationType, requireActivity().getWindow()) &&
                Validation.validateSelection(accommodationType, requireActivity().getWindow());
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
                Validation.validateText(inputTitle, editTitle, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditDescription)
                Validation.validateText(inputDescription, editDescription, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditMaxGuests)
                Validation.validateNumberCompare(inputMax, editMax, editMin, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditMinGuests)
                Validation.validateNumber(inputMin, editMin, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditPrice)
                Validation.validateDouble(inputPrice, editPrice, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditCancel)
                Validation.validateNumber(inputCancel, editCancel, requireActivity().getWindow());
        }
    }
}