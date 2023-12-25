package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import com.example.odyssey.R;
import com.example.odyssey.databinding.ActivityMainBinding;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.utils.Validation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class CreateAccommodationDetails extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private AccommodationRequest accommodation;
    TextInputLayout inputTitle, inputDescription, inputMin, inputMax, inputPrice, inputCancel;
    TextInputEditText editTitle, editDescription, editMin, editMax, editPrice, editCancel;
    AutoCompleteTextView accommodationType, priceType, confirmationType;
    Button nextBtn;

    public CreateAccommodationDetails() {}
    public static CreateAccommodationDetails newInstance(String param1, String param2) {
        CreateAccommodationDetails fragment = new CreateAccommodationDetails();
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

        View v = inflater.inflate(R.layout.fragment_create_accommodation_details, container, false);
        nextBtn = v.findViewById(R.id.buttonNext);

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

        nextBtn.setOnClickListener(v1 -> {
            String price = priceType.getText().toString();
            String acc = accommodationType.getText().toString();
            String confirm = confirmationType.getText().toString();
            if(!Validation.validateText(inputTitle, editTitle, requireActivity().getWindow()) ||
            !Validation.validateText(inputDescription,editDescription, requireActivity().getWindow()) ||
            !Validation.validateNumber(inputMax,editMax, requireActivity().getWindow()) ||
            !Validation.validateNumber(inputMin,editMin, requireActivity().getWindow()) ||
            !Validation.validateNumber(inputPrice,editPrice, requireActivity().getWindow()) ||
            !Validation.validateNumber(inputCancel,editCancel, requireActivity().getWindow()) || price.equals("") || acc.equals("") || confirm.equals("")){
                return;
            }

            accommodation = new AccommodationRequest(0L, AccommodationRequest.Type.CREATE, editTitle.getText().toString(),
                    editDescription.getText().toString(), Accommodation.Type.valueOf(acc.toUpperCase()), null,
                    Accommodation.getPricingType(acc.toUpperCase()), new HashSet<>(), Double.parseDouble(editPrice.getText().toString()),
                    confirm.equals("Automatic"), Long.parseLong(editCancel.getText().toString()), new HashSet<>(),
                    Integer.parseInt(editMin.getText().toString()), Integer.parseInt(editMax.getText().toString()),
                    null, null);

            Bundle args = new Bundle();
            args.putSerializable("Request",accommodation);
            Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_create_amenities,args);
        });

        return v;
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
            if(view.getId() == R.id.inputEditTitle) Validation.validateText(inputTitle, editTitle, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditDescription) Validation.validateText(inputDescription,editDescription, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditMaxGuests) Validation.validateNumber(inputMax,editMax, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditMinGuests) Validation.validateNumber(inputMin,editMin, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditPrice) Validation.validateNumber(inputPrice,editPrice, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditCancel) Validation.validateNumber(inputCancel,editCancel, requireActivity().getWindow());
        }
    }

}