package com.androimage.booksagar.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androimage.booksagar.R;
import com.androimage.booksagar.app.Config;
import com.androimage.booksagar.app.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    Button requestOtp, submitOtp, resetPassword;
    EditText edt, newPwd, cnfPwd, otp;
    LinearLayout ll1, ll2, ll3;
    RadioGroup rg;
    String key = "email";
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        edt = (EditText) findViewById(R.id.input_1);

        requestOtp = (Button) findViewById(R.id.btn_requestotp);


        requestOtp.setOnClickListener(this);


    }


    public void requestOtp() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Processing Request ...");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_REQUEST_OTP, new Response.Listener<String>() {

            @Override
            public void onResponse(String re) {
                Log.d("ForgetPassword", re.toString());
                pDialog.hide();

                try {

                    JSONObject response = new JSONObject(re);

                    if (!response.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(),
                                response.getString("message"), Toast.LENGTH_SHORT).show();

                        //If the server response is not success
                        //Displaying an error message on toastre

                        id = response.getString("emailid");


                    } else {
                        id = "";

                        Toast.makeText(getApplicationContext(),
                                response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    pDialog.hide();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ForgetPassword", "Error2: " + error.getMessage());
                pDialog.hide();
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

                params.put("key", key);
                params.put("value", edt.getText().toString().toLowerCase().trim());


                Log.e("ForgetPassword", "Posting params: " + params.toString());

                return params;
            }

        };


        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);


    }

    private void validateFields() {
        String regex = "\\d+";
        if (edt.getText().length() == 0) {
            edt.setError("Enter Valid Email /Mobile No");
            return;
        } else {
            if (edt.getText().toString().matches(regex) && edt.getText().toString().length() == 10) {

                key = "mobile";
                requestOtp();
            } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(edt.getText().toString()).matches()) {

                key = "email";
                requestOtp();
            } else {
                edt.setError("Enter Valid Email /Mobile No");
                key = "";
                return;
            }
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_requestotp:

                validateFields();

                break;
        }


    }
}

