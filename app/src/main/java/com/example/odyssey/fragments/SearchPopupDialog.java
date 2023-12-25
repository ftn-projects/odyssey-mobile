package com.example.odyssey.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.example.odyssey.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SearchPopupDialog  extends DialogFragment {

    private Date startDate;
    private Date endDate;
    private String location;
    private Integer numberOfGuests;

    private SearchPopupDialog.SearchDialogListener searchDialogListener;
    public void setSearchDialogListener(SearchPopupDialog.SearchDialogListener searchDialogListener) {
        this.searchDialogListener = searchDialogListener;
    }
    public SearchPopupDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_popup_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        MaterialButton button = view.findViewById(R.id.selectDateButtonFilter);
        TextView startingDate = view.findViewById(R.id.startDateTextFilter);
        TextView endingDate = view.findViewById(R.id.endDateTextFilter);
        TextInputEditText locationInput = view.findViewById(R.id.locationInput);
        TextInputEditText numberOfGuestsInput = view.findViewById(R.id.NumberOfGuestsEditText);
        MaterialButton closeButton = view.findViewById(R.id.search_close_button);
        MaterialButton applyButton = view.findViewById(R.id.search_apply_button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new Pair<>(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                )).build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        startDate = new Date(selection.first);
                        endDate = new Date(selection.second);
                        String date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(startDate);
                        String date2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(endDate);

                        startingDate.setText(MessageFormat.format("Selected Starting Date: {0}", date1));
                        endingDate.setText(MessageFormat.format("Selected Ending Date: {0}", date2));
                    }
                });

                materialDatePicker.show(getChildFragmentManager(), "tag");
            }
        });

        locationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                location = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numberOfGuestsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    numberOfGuests = Integer.parseInt(s.toString().trim());
                }
                catch (NumberFormatException e){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Dismiss the dialog
            }
        });


        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialogListener.onSearchApplied(startDate, endDate, location, numberOfGuests);
                dismiss();
            }
        });


    }

    public interface SearchDialogListener {
        void onSearchApplied(Date startDate, Date endDate, String location, Integer numberOfGuests);
    }

}