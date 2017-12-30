package com.androimage.booksagar.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androimage.booksagar.R;
import com.androimage.booksagar.app.Config;
import com.androimage.booksagar.app.MyApplication;
import com.androimage.booksagar.app.PrefManager;
import com.androimage.booksagar.app.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddBookActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = AddBookActivity.class.getSimpleName();
    EditText txtTitle, txtAuthor, txtPrice, txtMRP, txtLanguage, txtPublisher, txtStandard, txtYear, txtDescription, txtType, txtLocation;
    ImageView imgView;
    SwitchCompat shareNo;
    Button addBook;
    boolean imgsel = false;
    ProgressDialog progressDialog;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String userChoosenTask;
    private PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        txtTitle = (EditText) findViewById(R.id.input_title);
        txtAuthor = (EditText) findViewById(R.id.input_author);
        txtPrice = (EditText) findViewById(R.id.input_price);
        imgView = (ImageView) findViewById(R.id.img_book);
        shareNo = (SwitchCompat) findViewById(R.id.toggle_sharenumber);
        txtDescription = (EditText) findViewById(R.id.input_description);
        txtType = (EditText) findViewById(R.id.input_type);
        txtLocation = (EditText) findViewById(R.id.input_location);
        addBook = (Button) findViewById(R.id.btn_add);
        txtYear = (EditText) findViewById(R.id.input_year);
        txtMRP = (EditText) findViewById(R.id.input_mrp);
        txtLanguage = (EditText) findViewById(R.id.input_language);

        txtPublisher = (EditText) findViewById(R.id.input_publisher);
        txtStandard = (EditText) findViewById(R.id.input_standard);


        imgView.buildDrawingCache();

        bitmap = imgView.getDrawingCache();


        imgView.setOnClickListener(this);
        addBook.setOnClickListener(this);
        pref = new PrefManager(this);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_add:
                validateForm();
                break;

            case R.id.img_book:
                selectImage();
                break;

        }
    }


    private void validateForm() {

        if (txtLanguage.getText().length() == 0 || txtAuthor.getText().length() == 0 || txtType.getText().length() == 0 || txtLocation.getText().length() == 0 || txtMRP.getText().length() == 0 || txtTitle.getText().length() == 0 || txtPrice.getText().length() == 0 || txtPublisher.getText().length() == 0 || txtStandard.getText().length() == 0 || txtYear.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!imgsel) {
            Toast.makeText(getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
            return;
        }

        addBook();
    }

    private void addBook() {


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_ADDBOOK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    // checking for error, if not error SMS is initiated
                    // device should receive it shortly
                    if (!error) {
                        // boolean flag saying device is waiting for sms


                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                message,
                                Toast.LENGTH_LONG).show();
                    }

                    // hiding the progress bar
                    progressDialog.hide();

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error1: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                    progressDialog.hide();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error2: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.hide();
            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            String image = getStringImage(bitmap);

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", txtTitle.getText().toString());
                params.put("author", txtAuthor.getText().toString());
                params.put("publisher", txtPublisher.getText().toString());
                params.put("year", txtYear.getText().toString());
                params.put("mrp", txtMRP.getText().toString());
                params.put("user_id", pref.getUserDetails().get("id"));
                params.put("price", txtPrice.getText().toString());
                params.put("standard", txtStandard.getText().toString());
                params.put("language", txtLanguage.getText().toString());
                params.put("location", txtLocation.getText().toString());
                params.put("type", txtType.getText().toString());
                params.put("description", txtDescription.getText().toString());

                params.put("noshared", shareNo.isChecked() ? "1" : "0");

                params.put(KEY_IMAGE, image);
                //Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, "AddingBook");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddBookActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(AddBookActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        imgView.setImageBitmap(bitmap);
        imgsel = true;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {


        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imgView.setImageBitmap(bitmap);
        imgsel = true;
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}


