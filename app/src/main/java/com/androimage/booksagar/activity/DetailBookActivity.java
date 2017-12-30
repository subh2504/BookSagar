package com.androimage.booksagar.activity;

import android.app.ProgressDialog;
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

public class DetailBookActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = DetailBookActivity.class.getSimpleName();
    TextView tvTitle, tvAuthor, tvPublisher, tvYear, tvLanguage, tvPublisedOn, tvMRP, tvPrice, tvStandard, tvLocation, tvDescription, tvType;
    ImageView imgView;
    int seller_id = 0;
    int position;
    AppCompatButton btnContact;
    PrefManager prefManager;
    Book b;
    private Realm realm;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setContentView(R.layout.activity_detail_book);


        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        tvPublisher = (TextView) findViewById(R.id.tv_publisher);
        tvYear = (TextView) findViewById(R.id.tv_year);
        tvLanguage = (TextView) findViewById(R.id.tv_language);
        tvPublisedOn = (TextView) findViewById(R.id.tv_addedon);
        tvMRP = (TextView) findViewById(R.id.tv_mrp);
        tvPrice = (TextView) findViewById(R.id.tv_sp);
        tvStandard = (TextView) findViewById(R.id.tv_standard);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvType = (TextView) findViewById(R.id.tv_type);

        imgView = (ImageView) findViewById(R.id.img_book);
        imgView.setOnClickListener(this);
        btnContact = (AppCompatButton) findViewById(R.id.btn_contact);


        btnContact.setOnClickListener(this);

        tvMRP.setPaintFlags(tvMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        prefManager = new PrefManager(getApplicationContext());

        //realm = RealmController.getInstance().getRealm();
        //RealmResults<Book> results = realm.where(Book.class).findAll();

        // Get the book title to show it in toast messageSi

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt("id");


            b = RealmController.getInstance().getBook(position);

            if (b.getImageUrl() != null) {
                Glide.with(getApplicationContext())
                        .load(b.getImageUrl().replace("https", "http"))
                        .asBitmap()
                        .fitCenter()
                        .into(imgView);
            }
            seller_id = b.getSeller_id();
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
            //"₹"
            tvPrice.setText(getResources().getString(R.string.rs) + " " + b.getPrice());
            tvStandard.setText(b.getStandard());


        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_contact:
                if (prefManager.isLoggedIn()) {
                    chatWithSeller();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login to see the contact details", Toast.LENGTH_SHORT);

                    Intent i = new Intent(DetailBookActivity.this, LoginActivity.class);

                    startActivity(i);
                }
                break;
            case R.id.img_book:
                break;
        }

    }

    public void showAlert(String name, final String email, final String mobile) {
        Toast.makeText(this, name + email + mobile, Toast.LENGTH_LONG);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Seller Details")
                .setView(R.layout.seller_details)
                .create();

        dialog.show();
        // Include dialog.xml file
        // dialog.setContentView(R.layout.seller_details);
        // LayoutInflater inflater = this.getLayoutInflater();
        // final View dialogView = inflater.inflate(R.layout.seller_details, null);
//        TextView name1 = (TextView) dialogView.findViewById(R.id.name);
//        TextView email1 = (TextView) dialogView.findViewById(R.id.email);
//        TextView mobile1 = (TextView) dialogView.findViewById(R.id.contact);
        TextView name1 = (TextView) dialog.findViewById(R.id.name);
        TextView email1 = (TextView) dialog.findViewById(R.id.email);
        TextView mobile1 = (TextView) dialog.findViewById(R.id.contact);


        name1.setText(name);
        email1.setText(email);
        mobile1.setText(mobile);

//        mobile1.setOnClickListener(new View.OnClickListener() {
//
//            @Override

//            public void onClick(View view) {
//                String uri = "tel:0" + mobile.trim();
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse(uri));
//                DetailBookActivity.this.startActivity(intent);
//            }
//        });

//        email1.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//
//            public void onClick(View view) {
//                Intent emailIntent = new Intent(Intent.ACTION_SEND);
//
//                emailIntent.setData(Uri.parse("mailto:"));
//                emailIntent.setType("text/plain");
//                emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
//
//                try {
//                    startActivity(Intent.createChooser(emailIntent, "Mail Using ..."));
//                    finish();
//                    Log.i("Finished sending ", "");
//                }
//                catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(DetailBookActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
//                }


//            }
//        });
//


//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//
//        dialogBuilder.setTitle("Seller Info");
//        dialogBuilder.setMessage("");
        // dialog.setTitle("Seller Details");

//        dialogBuilder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //pass
//            }
//        });
//        AlertDialog b = dialogBuilder.create();
//        b.show();
        // dialog.show();

    }


    public void chatWithSeller() {


        Intent intent = new Intent(DetailBookActivity.this, ChatActivity.class);
        intent.putExtra("id", prefManager.getUserDetails().get("id"));
        intent.putExtra("book_id", Integer.toString(position));
        intent.putExtra("seller_id", Integer.toString(seller_id));

        startActivity(intent);


    }


    public void contactSeller() {


        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_SELLERDETAILS, new Response.Listener<String>() {

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


                        Intent intent = new Intent(DetailBookActivity.this, ChatActivity.class);

                        intent.putExtra("email", responseObj.getString("email"));
                        intent.putExtra("name", responseObj.getString("name"));
                        intent.putExtra("id", prefManager.getUserDetails().get("id"));
                        intent.putExtra("book_id", Integer.toString(position));


                        //String chat_id=Integer.toString(seller_id)+"_"+Integer.toString(position);
                        //intent.putExtra("chat_id",chat_id);
                        startActivity(intent);

                        //showAlert(responseObj.getString("name"),responseObj.getString("email"),responseObj.getString("mobile"));
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Something Went Wrong",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DetailBookActivity.this, ChatActivity.class);
                        intent.putExtra("id", prefManager.getUserDetails().get("id"));
                        intent.putExtra("book_id", Integer.toString(position));

                        String chat_id = Integer.toString(seller_id) + "_" + Integer.toString(position);
                        intent.putExtra("chat_id", chat_id);


                        startActivity(intent);
                    }


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
                params.put("seller_id", Integer.toString(seller_id));


                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, "GettingSeller");


    }

}
