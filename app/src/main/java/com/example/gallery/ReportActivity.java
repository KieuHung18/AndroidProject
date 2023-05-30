package com.example.gallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.addapter.SimpleReportGriddapter;
import com.example.gallery.entities.Artwork;
import com.example.gallery.entities.Report;
import com.example.gallery.services.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {
    private ArrayList<Report> reports;
    private Artwork artwork;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);
        artwork = (Artwork) getIntent().getSerializableExtra("artwork");
        GridView gridView = findViewById(R.id.idealGridView);
        gridView.setNumColumns(1);
        populateReport();

        SimpleReportGriddapter adapter = new SimpleReportGriddapter(this, reports);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JSONObject postData = new JSONObject();
                try {
                    postData.put("name", reports.get(i).getName());
                    postData.put("description", reports.get(i).getDescription());
                    postData.put("artworkId", artwork.getId());
                    new ReportArtwork().execute("/users/reports",postData.toString());
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void populateReport(){
        String [][]reportList ={{"Spam","Misleading or repetitive posts"},{"Self-harm","Encouraging self-injury, cutting, suicide"}};
        reports = new ArrayList<Report>();
        for (int i = 0; i < reportList.length;i++){
            Report report = new Report();
            report.setName(reportList[i][0]);
            report.setDescription(reportList[i][1]);
            report.setArtWorkId(artwork.getId());
            reports.add(report);
        }
    };
    private class ReportArtwork extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(ReportActivity.this);
            return request.doPost(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONObject response = result.getJSONObject("response");
            } catch (Exception e) {
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Log.d("ReportActivity", "onPostExecute: "+errorMessage.toString());
            }
        }
    }
}
