package com.example.odyssey.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.odyssey.R;
import com.example.odyssey.model.reports.UserReport;
import com.example.odyssey.model.reports.UserReportSubmission;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserReportAdapter extends ArrayAdapter<UserReport> {
    public UserReportAdapter(Context context, @NonNull List<UserReport> reports) {
        super(context, 0, reports);
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        UserReport report = getItem(position);
        if (report == null) {
            Log.e("UserReportAdapter", "Report is null");
            return convertView;
        }

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.report_summary_item, parent, false);

        TextView submitterView = convertView.findViewById(R.id.submitter);
        TextView dateView = convertView.findViewById(R.id.date);
        TextView descriptionView = convertView.findViewById(R.id.description);
        String submitter = report.getSubmitter().getName() + " " + report.getSubmitter().getSurname();
        submitterView.setText(submitter);
        dateView.setText(formatter.format(report.getSubmissionDate()));
        descriptionView.setText(report.getDescription());

        return convertView;
    }
}
