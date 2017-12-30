package com.androimage.booksagar.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androimage.booksagar.R;
import com.androimage.booksagar.app.Config;
import com.androimage.booksagar.app.MyApplication;
import com.androimage.booksagar.app.PrefManager;
import com.androimage.booksagar.model.Book;
import com.androimage.booksagar.realm.RealmController;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class DetailBookSellerActivity extends AppCompatActivity implements View.OnClickListener {


    private static String TAG = DetailBookActivity.class.getSimpleName();
    TextView tvTitle, tvAuthor, tvPublisher, tvYear, tvLanguage, tvPublisedOn, tvMRP, tvPrice, tvStandard, tvDescription, tvType, tvLocation;
    ImageView imgView;
    Integer position;
    Integer sold = 0;
    int seller_id = 0;
    AppCompatButton btnEdit, btnDelete, btnUserintrested, btnSold;
    PrefManager prefManager;
    private Realm realm;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_book_seller);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        tvPublisher = (TextView) findViewById(R.id.tv_publisher);
        tvYear = (TextView) findViewById(R.id.tv_year);
        tvLanguage = (TextView) findViewById(R.id.tv_language);
        tvPublisedOn = (TextView) findViewById(R.id.tv_addedon);
        tvMRP = (TextView) findViewById(R.id.tv_mrp);
        tvPrice = (TextView) findViewById(R.id.tv_sp);
        tvStandard = (TextView) findViewById(R.id.tv_standard);
        imgView = (ImageView) findViewById(R.id.img_book);
        imgView.setOnClickListener(this);
        btnEdit = (AppCompatButton) findViewById(R.id.btn_edit);

        tvDescription = (TextView) findViewById(R.id.tv_description);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvType = (TextView) findViewById(R.id.tv_type);
        btnDelete = (AppCompatButton) findViewById(R.id.btn_delete);
        btnSold = (AppCompatButton) findViewById(R.id.btn_sold);

        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnUserintrested = (AppCompatButton) findViewById(R.id.btn_interested);
        btnUserintrested.setOnClickListener(this);
        btnSold.setOnClickListener(this);


        tvMRP.setPaintFlags(tvMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        prefManager = new PrefManager(getApplicationContext());

        realm = RealmController.getInstance().getRealm();
        RealmResults<Book> results = realm.where(Book.class).findAll();

        // Get the book title to show it in toast messageSi

        Bundle extras = getIntent().getExtras();
        if (extras != null) {


            position = extras.getInt("id");
            Book b = RealmController.getInstance().getBook(position);
            seller_id = b.getSeller_id();


            if (b.getImageUrl() != null) {
                Glide.with(getApplicationContext())
                        .load(b.getImageUrl().replace("https", "http"))
                        .asBitmap()
                        .fitCenter()
                        .into(imgView);
            }
            tvTitle.setText(b.getTitle());
            tvAuthor.setText(b.getAuthor());
            tvPublisher.setText(b.getPublisher());
            tvYear.setText(String.valueOf(b.getYear()));
            tvLanguage.setText(b.getLanguage());
            if (b.getDescription().equals("null") || b.getDescription().equals("")) {
                tvDescription.setText("Description Not Available");
            } else {
                tvDescription.setText(b.getDescription());

            }
            tvType.setText(b.getType());
            tvLocation.setText(b.getLocation());

            seller_id = b.getSeller_id();
            tvPublisedOn.setText(b.getAdded_on());
            tvMRP.setText(b.getMrp());
            tvPrice.setText(getResources().getString(R.string.rs) + " " + b.getPrice());
            tvStandard.setText(b.getStandard());
            if (b.isSold()) {
                sold = 0;
                btnSold.setText("Available");
            } else {
                sold = 1;
                btnSold.setText("Sold Out");
            }


        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_edit:
                if (prefManager.isLoggedIn()) {
                    showAlert();
                } else

                {
                    Toast.makeText(getApplicationContext(), "Please Login ", Toast.LENGTH_SHORT);

                    Intent i = new Intent(DetailBookSellerActivity.this, LoginActivity.class);

                    startActivity(i);
                }
                break;
            case R.id.btn_delete:
                if (prefManager.isLoggedIn()) {
                    showDeleteAlert();
                } else

                {
                    Toast.makeText(getApplicationContext(), "Please Login ", Toast.LENGTH_SHORT);

                    Intent i = new Intent(DetailBookSellerActivity.this, LoginActivity.class);

                    startActivity(i);
                }
                break;

            case R.id.btn_sold:
                if (prefManager.isLoggedIn()) {
                    showSoldAlert();
                } else

                {
                    Toast.makeText(getApplicationContext(), "Please Login ", Toast.LENGTH_SHORT);

                    Intent i = new Intent(DetailBookSellerActivity.this, LoginActivity.class);

                    startActivity(i);
                }
                break;

            case R.id.btn_interested:
                if (prefManager.isLoggedIn()) {
                    Log.d("int", "gggg");
                    Intent i = new Intent(DetailBookSellerActivity.this, InterestedPeopleActivity.class);
                    i.putExtra("book_id", getIntent().getExtras().getInt("id"));
                    i.putExtra("id", prefManager.getUserDetails().get("id"));
                    i.putExtra("seller_id", seller_id);
                    DetailBookSellerActivity.this.startActivity(i);
                } else

                {
                    Toast.makeText(getApplicationContext(), "Please Login ", Toast.LENGTH_SHORT);

                    Intent i = new Intent(DetailBookSellerActivity.this, LoginActivity.class);

                    startActivity(i);
                }
                break;
        }

    }

    private void showSoldAlert() {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_UPDATE_SOLD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");

                    // checking for error, if not error SMS is initiated
                    // device should receive it shortly
                    if (!error) {
                        // boolean flag saying device is waiting for sms
                        Toast.makeText(getApplicationContext(),
                                responseObj.getString("message"),
                                Toast.LENGTH_LONG).show();


                    } else {
                        Toast.makeText(getApplicationContext(),
                                responseObj.getString("message"),
                                Toast.LENGTH_LONG).show();
                    }

                    DetailBookSellerActivity.this.finish();
                    // hiding the progress bar


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
                params.put("user_id", prefManager.getUserDetails().get("id"));
                params.put("book_id", Integer.toString(position));
                params.put("status", Integer.toString(sold));


                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, "GettingSeller");


    }

    public void showAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You want to edit this post");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent i = new Intent(DetailBookSellerActivity.this, UpdateBookActivity.class);
                i.putExtra("id", getIntent().getExtras().getInt("id"));
                startActivity(i);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void showDeleteAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You want to delete this post");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        Config.URL_DELETE_BOOK, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject responseObj = new JSONObject(response);

                            // Parsing json object response
                            // response will be a json object
                            boolean error = responseObj.getBoolean("error");

                            // checking for error, if not error SMS is initiated
                            // device should receive it shortly
                            if (!error) {
                                // boolean flag saying device is waiting for sms
                                Toast.makeText(getApplicationContext(),
                                        responseObj.getString("message"),
                                        Toast.LENGTH_LONG).show();


                            } else {
                                Toast.makeText(getApplicationContext(),
                                        responseObj.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }

                            finish();
                            // hiding the progress bar


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
                        params.put("user_id", prefManager.getUserDetails().get("id"));
                        params.put("book_id", Integer.toString(position));


                        Log.e(TAG, "Posting params: " + params.toString());

                        return params;
                    }

                };

                // Adding request to request queue
                MyApplication.getInstance().addToRequestQueue(strReq, "GettingSeller");


            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}




