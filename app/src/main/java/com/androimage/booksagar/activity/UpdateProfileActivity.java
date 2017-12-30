package com.androimage.booksagar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androimage.booksagar.R;
import com.androimage.booksagar.app.Config;
import com.androimage.booksagar.app.MyApplication;
import com.androimage.booksagar.app.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = UpdateProfileActivity.class.getSimpleName();
    ProgressDialog progressBar;
    private EditText txtName, txtEmail, txtPassword, txtCnfPassword, txtMobileNumber;
    private ViewPager viewPager;
    private Button btnUpdate;
    private PrefManager pref;

    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param mobile
     * @return
     */
    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtName = (EditText) findViewById(R.id.input_name);
        txtEmail = (EditText) findViewById(R.id.input_email);


        txtMobileNumber = (EditText) findViewById(R.id.input_mobile);


        btnUpdate = (Button) findViewById(R.id.btn_update);


        btnUpdate.setOnClickListener(this);


        pref = new PrefManager(getApplicationContext());

        txtName.setText(getIntent().getExtras().get("name").toString());
        txtEmail.setText(getIntent().getExtras().get("email").toString());
        txtMobileNumber.setText(getIntent().getExtras().get("mobile").toString());


        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Please Wait . .");
        progressBar.setCancelable(false);


        // Checking for user session
        // if user is already logged in, take him to main activity
//        if (pref.isLoggedIn()) {
//            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//
//            finish();
//        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            txtEmail.setText(extras.getString("email"));
            txtName.setText(extras.getString("name"));
            txtMobileNumber.setText(extras.getString("mobile"));
        }


        /**
         * Checking if the device is waiting for sms
         * showing the user OTP screen
         */

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                validateForm();
                break;
        }
    }

    /**
     * Validating user details form
     */
    private void validateForm() {
        String name = txtName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String mobile = txtMobileNumber.getText().toString().trim();
        String id = getIntent().getExtras().get("id").toString();


        if (name.equals(getIntent().getExtras().get("name").toString()) && email.equals(getIntent().getExtras().get("email").toString()) && mobile.equals(getIntent().getExtras().get("mobile").toString()))

        {
            Toast.makeText(getApplicationContext(), "No Changes To Update", Toast.LENGTH_SHORT).show();
            return;
        }
        // validating empty name and email
        if (name.length() == 0 || email.length() == 0 || mobile.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your details", Toast.LENGTH_SHORT).show();
            return;
        }


        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        // validating mobile number
        // it should be of 10 digits length
        if (isValidPhoneNumber(mobile)) {
            if (email.matches(emailPattern)) {


                // request for sms
                progressBar.show();
                // saving the mobile number in shared preferences

                // requesting for sms
                updateProfile(name, email, mobile, id);
            } else {
                Toast.makeText(getApplicationContext(), "Please enter valid email id", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * sending the OTP to server and activating the user
     */

    /**
     * Method initiates the SMS request on the server
     *
     * @param name   user name
     * @param email  user email address
     * @param mobile user valid mobile number
     */
    private void updateProfile(final String name, final String email, final String mobile, final String id) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_UPDATE_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);

                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    progressBar.dismiss();
                    if (!error) {

                        Toast.makeText(getApplicationContext(),
                                message,
                                Toast.LENGTH_LONG).show();
                        pref.createLogin(name, email, mobile, id);

                        Intent intent = new Intent(UpdateProfileActivity.this, ProfileActivity.class);

                        startActivity(intent);
                        finish();


                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                message,
                                Toast.LENGTH_LONG).show();
                    }

                    // hiding the progress bar


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();

                    progressBar.dismiss();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error2: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.hide();
            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("mobile", mobile);
                params.put("id", id);


                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
