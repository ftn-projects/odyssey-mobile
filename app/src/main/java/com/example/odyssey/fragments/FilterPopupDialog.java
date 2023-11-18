package com.example.odyssey.fragments;

import static android.app.PendingIntent.getActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.odyssey.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.slider.RangeSlider;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FilterPopupDialog extends DialogFragment {

    public FilterPopupDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.popup_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        MaterialButton button = view.findViewById(R.id.selectDateButtonFilter);
        TextView startingDate = view.findViewById(R.id.startDateTextFilter);
        TextView endingDate = view.findViewById(R.id.endDateTextFilter);

        RangeSlider rangeSlider = view.findViewById(R.id.priceRangeSliderFilter);
        TextView priceRangeStart = view.findViewById(R.id.priceStartFilterEditText);
        TextView priceRangeEnd = view.findViewById(R.id.priceEndFilterEditText);

        List<Float> values = rangeSlider.getValues();
        priceRangeStart.setText(MessageFormat.format("{0}", NumberFormat.getCurrencyInstance().format(values.get(0))));
        priceRangeEnd.setText(MessageFormat.format("{0}", NumberFormat.getCurrencyInstance().format(values.get(1))));



// If you only want the slider start and end value and don't care about the previous values
        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> values = slider.getValues();
                priceRangeStart.setText(MessageFormat.format("{0}", NumberFormat.getCurrencyInstance().format(values.get(0))));
                priceRangeEnd.setText(MessageFormat.format("{0}", NumberFormat.getCurrencyInstance().format(values.get(1))));
            }
        });



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
                        String date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.first));
                        String date2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.second));

                        startingDate.setText(MessageFormat.format("Selected Starting Date: {0}", date1));
                        endingDate.setText(MessageFormat.format("Selected Ending Date: {0}", date2));
                    }
                });

                materialDatePicker.show(getChildFragmentManager(), "tag");
            }
        });

        MaterialButton closeButton = view.findViewById(R.id.filter_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Dismiss the dialog
            }
        });

        MaterialButton applyButton = view.findViewById(R.id.filter_apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Dismiss the dialog
            }
        });


    }
}