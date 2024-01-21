package com.example.odyssey.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.stats.AccommodationTotalStats;
import com.example.odyssey.model.stats.MonthlyStats;
import com.example.odyssey.services.FileDownloadManager;
import com.example.odyssey.utils.TokenUtils;
import com.google.android.material.button.MaterialButton;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccommodationStatDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccommodationStatDialogFragment extends DialogFragment {

    private static final String ARGUMENT_KEY = "stats";
    private AccommodationTotalStats accommodationStats;

    public AccommodationStatDialogFragment() {
        // Required empty public constructor
    }

    public static AccommodationStatDialogFragment newInstance(AccommodationTotalStats stats) {
        AccommodationStatDialogFragment fragment = new AccommodationStatDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_KEY, stats);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);

            // Set left and right margin of dialog window
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.horizontalMargin = 10; // This is in percentage. 10% of total width of screen
            params.verticalMargin = 10; // This is in percentage. 10% of total height of screen
            dialog.getWindow().setAttributes(params);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accommodationStats = (AccommodationTotalStats) getArguments().getSerializable(ARGUMENT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AccommodationTotalStats stats = (AccommodationTotalStats) getArguments().getSerializable(ARGUMENT_KEY);
        View view = inflater.inflate(R.layout.fragment_accommodation_stat_dialog, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton button = getView().findViewById(R.id.exportAccommodationToPDFDialogButton);

        populateLineChart((LineChart) getView().findViewById(R.id.dialogLineChart));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadReport();
            }
        });
    }


    private void downloadReport() {
        Call<ResponseBody> call = ClientUtils.fileDownloadService.downloadAccommodationReport(TokenUtils.getId(requireContext()), accommodationStats.getStart(), accommodationStats.getEnd());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Get the download URL from the response body
                    FileDownloadManager manager = new FileDownloadManager();
                    manager.requestDownload(call.request().url().toString(), getContext(), getActivity(), "accommodationReport.pdf");
                }else{
                    String error = ClientUtils.getError(response, "Error while downloading accommodation report");
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error while downloading report", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateLineChart(LineChart lineChart) {
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        List<Entry> entries = new ArrayList<Entry>();
        for(MonthlyStats monthlyStats : accommodationStats.getMonthlyStats()){
            entries.add(new Entry(monthlyStats.getMonth(), monthlyStats.getTotalIncome().floatValue()));
        }
        LineDataSet dataSet = getDataSet(entries, "Income");
        dataSets.add(dataSet);

        lineChart.getDescription().setText("Total income for " + getMilisecondsFormatted(accommodationStats.getStart()) + " - " + getMilisecondsFormatted(accommodationStats.getEnd()));
        lineChart.getDescription().setTypeface(getResources().getFont(R.font.montserrat_regular));

        Legend legend = lineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTypeface(getResources().getFont(R.font.montserrat_regular));
        lineChart.getDescription().setTextSize(12);
        lineChart.setDrawMarkers(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setTypeface(getResources().getFont(R.font.montserrat_regular));
        lineChart.animateY(1000);
        lineChart.getXAxis().setLabelCount(5);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return getMilisecondsFormatted((long) value);
            }
        });

        lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "$" + value;
            }
        });

        lineChart.invalidate();
    }

    private LineDataSet getDataSet(List<Entry> entries, String label) {
        LineDataSet lineDataSet;

        lineDataSet = new LineDataSet(entries, label);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setValueTextSize(12);
//        lineDataSet.setDrawValues(true);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setDrawCircles(true);
        return lineDataSet;
    }

    private String getMilisecondsFormatted(Long milliseconds) {
        Instant instant = Instant.ofEpochMilli(milliseconds);

        // Convert Instant to LocalDate
        LocalDate localDate = instant.atZone(ZoneOffset.UTC).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        String formattedDate = localDate.format(formatter);
        return formattedDate;
    }
}