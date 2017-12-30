package com.androimage.booksagar.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androimage.booksagar.app.Config;
import com.androimage.booksagar.app.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by subha on 9/25/2016.
 */

public class UpdateToken {


    private Context context;
    private String token;
    private String id;


    public UpdateToken(Context c, String token, String id) {
        this.context = c;
        this.id = id;
        this.token = token;
    }

    public void registerToken() {


        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_USER_TOKEN, new Response.Listener<String>() {

            @Override
            public void onResponse(String re) {
                Log.d("BookSagarIntent", re.toString());


                try {

                    JSONObject response = new JSONObject(re);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                    Log.e("BooksagarIntent",  error.getMessage());
                //     Toast.makeText(context,
                //        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("token", token);
                params.put("id", id);


                Log.e("BooksagarIntent", "Posting params: " + params.toString());

                return params;
            }

        };


        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);


    }
}

