package com.example.gallery.addapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gallery.R;
import com.example.gallery.entities.Report;

import java.util.ArrayList;

public class SimpleReportGriddapter extends ArrayAdapter<Report> {
    public SimpleReportGriddapter(@NonNull Context context, ArrayList<Report> reports) {
        super(context, 0, reports);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View view,  @NonNull ViewGroup parent) {
        if (view == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            view = LayoutInflater.from(getContext()).inflate(R.layout.report_item, parent, false);
        }

//        Report report = getItem(position);
//        TextView name = view.findViewById(R.id.textViewReportName);
//        TextView description = view.findViewById(R.id.textViewReportDescription);
//        name.setText(report.getName());
//        description.setText(report.getDescription());
        return view;
    }
}