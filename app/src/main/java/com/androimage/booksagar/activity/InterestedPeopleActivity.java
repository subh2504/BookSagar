package com.androimage.booksagar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androimage.booksagar.R;
import com.androimage.booksagar.app.Config;
import com.androimage.booksagar.app.MyApplication;
import com.androimage.booksagar.app.PrefManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InterestedPeopleActivity extends AppCompatActivity {

    private static final String TAG = "InterestedPeopleAct";
    Map<String, String> map1 = new HashMap<String, String>();
    private Button add_room;
    private EditText room_name;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested_people);


        listView = (ListView) findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_of_rooms);

        listView.setAdapter(arrayAdapter);


        getInterestedUsers();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(InterestedPeopleActivity.this, ChatActivity.class);
                String[] str = list_of_rooms.get(i).split("-");
                PrefManager prefManager = new PrefManager(getApplicationContext());

                Log.d("str", str[1]);
                intent.putExtra("id", str[1]);

                intent.putExtra("book_id", getIntent().getExtras().get("book_id").toString());

                intent.putExtra("seller_id", getIntent().getExtras().get("seller_id").toString());
                startActivity(intent);
            }
        });

    }


    private void getInterestedUsers() {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_USERINTERESTED, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject res = new JSONObject(response);
                    boolean error = res.getBoolean("error");
                    if (!error) {

                        Set<String> set = new HashSet<String>();
                        JSONArray ja = res.getJSONArray("interestedUsers");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = ja.getJSONObject(i);

                            Log.d("ResponseIntrestedUser", ja.toString());

                            // map1.put(jo.getString("name"),jo.getString("buyer_id"));

                            set.add(jo.getString("name") + "-" + jo.getString("buyer_id"));
                        }


                        list_of_rooms.clear();
                        list_of_rooms.addAll(set);

                        arrayAdapter.notifyDataSetChanged();


                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error1: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();


                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error2: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("book_id", getIntent().getExtras().get("book_id").toString());
                params.put("seller_id", getIntent().getExtras().get("id").toString());

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, "InterestedPeople");


    }


}
