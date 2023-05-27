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
        setContentView(R.layout.add_ideal_acrivity);
        done = findViewById(R.id.addIdealDone);
        back = findViewById(R.id.addIdealBack);
        name = findViewById(R.id.editTextIdealTitle);
        publish = findViewById(R.id.switchPublish);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Title is required",Toast.LENGTH_SHORT).show();
                }else {
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("name", name.getText().toString());
                        postData.put("publish", publish.isChecked());
                        new IdealTask().execute("/users/ideals",postData.toString());
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
