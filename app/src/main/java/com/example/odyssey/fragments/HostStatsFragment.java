package com.example.odyssey.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.stats.AccommodationTotalStats;
import com.example.odyssey.model.stats.TotalStats;
import com.example.odyssey.utils.TokenUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.w3c.dom.Text;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HostStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HostStatsFragment extends Fragment {
    private List<Long> dates;

    public void setDates(List<Long> dates) {
        this.dates = dates;
        getTotalStats();
    }
    private TotalStats totalStats;

    private void setTotalStats(TotalStats totalStats) {
        this.totalStats = totalStats;
    }

    private List<AccommodationTotalStats> accommodationTotalStatsList;

    private void setAccommodationTotalStatsList(List<AccommodationTotalStats> accommodationTotalStatsList) {
        this.accommodationTotalStatsList = accommodationTotalStatsList;
    }

    private int[] colors = new int[]{
            0xFFFFC3E0, // Light Pink
            0xFFFFD7B2, // Apricot
            0xFFD0F0C0, // Mint Green
            0xFFB2EBF2, // Sky Blue
            0xFFF5C8A9, // Peach
            0xFFFFE082  // Warm Yellow
    };



    public HostStatsFragment() {
        // Required empty public constructor
    }

    public static HostStatsFragment newInstance() {
        HostStatsFragment fragment = new HostStatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_host_stats, container, false);

        if (dates == null || dates.size() != 2) {
            LocalDate today = LocalDate.now();
            Instant endOfDay = today.atStartOfDay(ZoneOffset.UTC).plusDays(1).minusNanos(1).toInstant();

            LocalDate previousYear = today.minusMonths(3);
            Instant startOfDayPreviousYear = previousYear.atStartOfDay(ZoneOffset.UTC).toInstant();
            List<Long> defaultDates = new ArrayList<>();
            defaultDates.add(startOfDayPreviousYear.toEpochMilli());
            defaultDates.add(endOfDay.toEpochMilli());
            setDates(defaultDates);
        }

        MaterialButton button = view.findViewById(R.id.selectDateButtonFilter);
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
                        List<Long> dates = new ArrayList<>();
                        dates.add(selection.first);
                        dates.add(selection.second);
                        setDates(dates);
                    }
                });

                materialDatePicker.show(getChildFragmentManager(), "tag");
            }
        });
        return view;
    }


    private void populateData() {
        if (totalStats == null || accommodationTotalStatsList == null) return;
        populateTextView();
        populateLineChart((LineChart) getView().findViewById(R.id.mainLineChart));
        populatePieChart((PieChart) getView().findViewById(R.id.mainPieChart));
        populateAccommodationCards();
    }

    private void populateAccommodationCards() {

    }

    private void populateTextView() {
        TextView timeTextView = (TextView) getView().findViewById(R.id.timePeriodTextView);
        TextView reservationsTextView = (TextView) getView().findViewById(R.id.totalReservationsTextView);
        TextView incomeTextView = (TextView) getView().findViewById(R.id.totalIncomeTextView);
        if (totalStats != null) {
            timeTextView.setText(getMilisecondsFormatted(totalStats.getStart()) + " - " + getMilisecondsFormatted(totalStats.getEnd()));
            reservationsTextView.setText("Total reservations: " + totalStats.getTotalReservations());
            incomeTextView.setText("Total income: $" + totalStats.getTotalIncome());

        } else {
            timeTextView.setText("Unable to get time period");
            reservationsTextView.setText("Unable to get total reservations");
            incomeTextView.setText("Unable to get total income");
        }
    }

    private void populateLineChart(LineChart lineChart) {
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        if (totalStats == null || accommodationTotalStatsList == null) return;
        else {
            List<Entry> lineEntries = new ArrayList<Entry>();
            for (int i = 0; i < totalStats.getMonthlyStats().size(); i++) {
                lineEntries.add(new Entry(totalStats.getMonthlyStats().get(i).getMonth(), totalStats.getMonthlyStats().get(i).getTotalIncome().floatValue()));
            }
            LineDataSet lineDataSet = getDataSet(lineEntries, "Total Income");
            dataSets.add(lineDataSet);
        }

        for (int i = 0; i < accommodationTotalStatsList.size(); i++) {
            List<Entry> lineEntries = new ArrayList<Entry>();
            for (int j = 0; j < accommodationTotalStatsList.get(i).getMonthlyStats().size(); j++) {
                lineEntries.add(new Entry(accommodationTotalStatsList.get(i).getMonthlyStats().get(j).getMonth(), accommodationTotalStatsList.get(i).getMonthlyStats().get(j).getTotalIncome().floatValue()));
            }
            LineDataSet lineDataSet = getDataSet(lineEntries, accommodationTotalStatsList.get(i).getAccommodation().getTitle());
            lineDataSet.setColor(colors[i % colors.length]);
            dataSets.add(lineDataSet);
        }

        lineChart.getDescription().setText("Total income for " + getMilisecondsFormatted(totalStats.getStart()) + " - " + getMilisecondsFormatted(totalStats.getEnd()));


        Legend legend = lineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        lineChart.getDescription().setTextSize(12);
        lineChart.setDrawMarkers(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.animateY(1000);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(1.0f);
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

    private void populatePieChart(PieChart pieChart) {
        List<PieEntry> entries = new ArrayList<>();
        if (totalStats == null || accommodationTotalStatsList == null) return;
        else {
            for (int i = 0; i < accommodationTotalStatsList.size(); i++) {
                PieEntry entry = new PieEntry(accommodationTotalStatsList.get(i).getTotalIncome().floatValue(), accommodationTotalStatsList.get(i).getAccommodation().getTitle());
                entries.add(entry);
            }

        }
        pieChart.setEntryLabelColor(Color.BLACK);
        PieDataSet dataSet = new PieDataSet(entries, "Income");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);

        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "$" + super.getFormattedValue(value);
            }
        });
        dataSet.setValueTextSize(20f);
        pieChart.setData(data);
        Description description = new Description();
        description.setText("Total income for " + getMilisecondsFormatted(totalStats.getStart()) + " - " + getMilisecondsFormatted(totalStats.getEnd()));
        pieChart.setDescription(description);
        pieChart.setCenterText("Total income\n" + "$" + totalStats.getTotalIncome().toString());
        pieChart.setCenterTextSize(20f);
        pieChart.invalidate();
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

    private void getTotalStats() {
        Call<TotalStats> call = ClientUtils.accommodationService.generatePeriodStats(TokenUtils.getId(), dates.get(0), dates.get(1));

        call.enqueue(new Callback<TotalStats>() {
            @Override
            public void onResponse(Call<TotalStats> call, Response<TotalStats> response) {
                if (response.isSuccessful()) {
                    setTotalStats(response.body());
                    getAllAccommodationStats();
                }
            }

            @Override
            public void onFailure(Call<TotalStats> call, Throwable t) {
                Toast.makeText(getContext(), "Error while fetching stats", Toast.LENGTH_SHORT).show();
                totalStats = null;
            }
        });
    }

    private void getAllAccommodationStats() {
        Call<ArrayList<AccommodationTotalStats>> call = ClientUtils.accommodationService.getPeriodStatsAllAccommodation(TokenUtils.getId(), dates.get(0), dates.get(1));

        call.enqueue(new Callback<ArrayList<AccommodationTotalStats>>() {
            @Override
            public void onResponse(Call<ArrayList<AccommodationTotalStats>> call, Response<ArrayList<AccommodationTotalStats>> response) {
                if (response.isSuccessful()) {
                    setAccommodationTotalStatsList(response.body());
                    populateData();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AccommodationTotalStats>> call, Throwable t) {
                Toast.makeText(getContext(), "Error while fetching accommodation stats", Toast.LENGTH_SHORT).show();
                setAccommodationTotalStatsList(null);
            }
        });
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