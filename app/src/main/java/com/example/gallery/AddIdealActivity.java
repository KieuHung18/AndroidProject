package com.example.gallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.entities.Ideal;
import com.example.gallery.services.Request;

import org.json.JSONException;
import org.json.JSONObject;

public class AddIdealActivity extends AppCompatActivity {
    private Button done;
    private ImageButton back;
    private EditText name;
    private Switch publish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ideal ideal = (Ideal) getIntent().getSerializableExtra("ideal");
        setContentView(R.layout.add_ideal_acticity);
        done = findViewById(R.id.addIdealDone);
        back = findViewById(R.id.addIdealBack);
        name = findViewById(R.id.editTextIdealTitle);
        publish = findViewById(R.id.switchPublish);

        if(ideal!=null){
            name.setText(ideal.getName());
            publish.setChecked(ideal.isPublish());
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Title is required",Toast.LENGTH_SHORT).show();
                }else {
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("publish", publish.isChecked());
                        postData.put("name", name.getText().toString());
                        if(ideal!=null){
                            new IdealTask().execute("/users/ideals/"+ideal.getId(),postData.toString());
                        }
                        else{
                            new IdealTask().execute("/users/ideals",postData.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private class IdealTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            Request request = new Request(AddIdealActivity.this);
            JSONObject data = request.doPost(params[0],params[1]);
            return data;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            try {
                JSONObject response = result.getJSONObject("response");
                Log.d("AddIdealActivity", "onPostExecute: "+response);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessage = new HandleRequestError().handle(result).getMessage();
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
