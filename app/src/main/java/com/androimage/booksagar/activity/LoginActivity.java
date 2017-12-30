package com.androimage.booksagar.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.androimage.booksagar.helper.UpdateToken;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private static final int RC_SIGN_IN = 9001;
    public GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    private Button btnlogin, btnSignUp;
    private GoogleSignInAccount account;
    private LoginButton btnLoginFB;
    private SignInButton btnLoginGoogle;
    private EditText txtEmail, txtPassword;
    private CallbackManager callbackManager;
    private TextView lnkSkipLogin, lnkSignUp, lnkForgetPwd;
    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        btnlogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        btnLoginFB = (LoginButton) findViewById(R.id.facebook_sign_in_button);
        //btnLoginGoogle= (Button) findViewById(R.id.google_sign_in_button);
        //lnkSignUp = (TextView) findViewById(R.id.link_signup);
        lnkSkipLogin = (TextView) findViewById(R.id.skip_login);
        txtEmail = (EditText) findViewById(R.id.input_email);

        txtPassword = (EditText) findViewById(R.id.input_password);
        lnkForgetPwd = (TextView) findViewById(R.id.link_forgetpassword);

        btnLoginFB = (LoginButton) findViewById(R.id.facebook_sign_in_button);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)

                .build();


        btnLoginFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email"));


                //Toast.makeText(LoginActivity.this, "Fb Success ", Toast.LENGTH_LONG).show();
                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application codeIntent intent = new Intent(LoginActivity.this, SignupActivity.class);

                                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);

                                try {
                                    Log.d("jsonObjFb", object.toString());
                                    String email = object.getString("email");
                                    signIn("social", object.getString("email"), object.getString("name"), "null");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Sign In Cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LoginActivity.this, "Please Try Again ...", Toast.LENGTH_LONG).show();
            }
        });


        btnLoginGoogle = (SignInButton) findViewById(R.id.google_sign_in_button);
        btnLoginGoogle.setSize(SignInButton.SIZE_WIDE);
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });


        btnlogin.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                signIn("email", txtEmail.getText().toString(), "", txtPassword.getText().toString());
            }
        });

        lnkSkipLogin.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                LoginActivity.this.finish();
            }
        });

        lnkForgetPwd.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                //printKeyHash();


                //showForgetPasswordDialog();

                Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(i);

            }
        });

//
//        lnkSignUp.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//
//            public void onClick(View view) {
//                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
//                startActivity(i);
//            }
//        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

    }


    private void signInWithGoogle() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(final GoogleSignInAccount account) {

        signIn("social", account.getEmail(), account.getDisplayName(), "null");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                final GoogleApiClient client = mGoogleApiClient;
                account = result.getSignInAccount();
                // Toast.makeText(LoginActivity.this, "Gmail Success "+account.getEmail(), Toast.LENGTH_LONG).show();

                // Toast t=new Toast(this)
                handleSignInResult(account);
            } else {
                Log.e("Gogle Error", result.toString());
                //handleSignInResult(...);
                //Toast.makeText(LoginActivity.this, "Gmail Error "+result.getStatus(), Toast.LENGTH_LONG).show();

            }
        } else {
            // Handle other values for requestCode
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if (loggedIn) {
            //We will start the Profile Activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void signIn(final String loginType, final String email, final String name, final String password) {


        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String re) {
                Log.d("SignUpAt", re.toString());


                try {

                    JSONObject response = new JSONObject(re);
                    if (response.getString("status").equalsIgnoreCase(Config.LOGIN_SUCCESS)) {


                        JSONObject profileObj = response.getJSONObject("profile");

                        String name = profileObj.getString("name");
                        String email = profileObj.getString("email");
                        String mobile = profileObj.getString("mobile");
                        String id = profileObj.getString("id");

                        PrefManager pref = new PrefManager(getApplicationContext());
                        pref.createLogin(name, email, mobile, id);
                        //Starting profile activity
                        UpdateToken u = new UpdateToken(LoginActivity.this, FirebaseInstanceId.getInstance().getToken(), id);

                        u.registerToken();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //If the server response is not success
                        //Displaying an error message on toastre
                        if (loginType.equalsIgnoreCase("social")) {
                            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);

                            intent.putExtra("email", email);
                            intent.putExtra("name", name);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    response.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoginAct", "Error2: " + error.getMessage());
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

                params.put("email", email);
                params.put("loginType", loginType);
                params.put("password", password);

                Log.e("LoginAct", "Posting params: " + params.toString());

                return params;
            }

        };


        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);


    }

    public void showForgetPasswordDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.forget_password, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.input_1);
        final RadioGroup rg = (RadioGroup) dialogView.findViewById(R.id.rb);
        edt.setHint("Registered Email Id");

        String key = "email";
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch (id) {
                    case R.id.rb_email:
                        edt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                        edt.setHint("Registered Email Id");

                        break;
                    case R.id.rb_mobile:
                        edt.setInputType(InputType.TYPE_CLASS_PHONE);
                        edt.setHint("Registered Mobile Number");
                        break;

                }
            }
        });


        dialogBuilder.setTitle("BookSagar Forget password");
        dialogBuilder.setMessage("");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if (rg.getCheckedRadioButtonId() == R.id.rb_email) {
                    Toast.makeText(getApplicationContext(), "Email " + edt.getText().toString(), Toast.LENGTH_SHORT).show();
                } else if (rg.getCheckedRadioButtonId() == R.id.rb_mobile) {
                    Toast.makeText(getApplicationContext(), "Mobile " + edt.getText().toString(), Toast.LENGTH_SHORT).show();
                }


                StringRequest strReq = new StringRequest(Request.Method.POST,
                        Config.URL_LOGIN, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String re) {
                        Log.d("ForgetPassword", re.toString());


                        try {

                            JSONObject response = new JSONObject(re);
                            if (response.getString("status").equalsIgnoreCase(Config.LOGIN_SUCCESS)) {

                                Toast.makeText(getApplicationContext(),
                                        response.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {
                                //If the server response is not success
                                //Displaying an error message on toastre


                                Toast.makeText(getApplicationContext(),
                                        response.getString("message"), Toast.LENGTH_SHORT).show();


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ForgetPassword", "Error2: " + error.getMessage());
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

                        params.put("key", rg.getCheckedRadioButtonId() == R.id.rb_email ? "email" : "mobile");
                        params.put("value", edt.getText().toString().toLowerCase().trim());


                        Log.e("ForgetPassword", "Posting params: " + params.toString());

                        return params;
                    }

                };


                // Adding request to request queue
                MyApplication.getInstance().addToRequestQueue(strReq);


            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    private void printKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.androimage.booksagar",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", e.toString());
        }
    }


}
