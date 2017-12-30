package com.androimage.booksagar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androimage.booksagar.R;
import com.androimage.booksagar.app.PrefManager;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView name, email, mobile;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        mobile = (TextView) findViewById(R.id.mobile);


        PrefManager pref = new PrefManager(getApplicationContext());
        final HashMap<String, String> a = pref.getUserDetails();
        name.setText(a.get("name"));
        email.setText(a.get("email"));
        mobile.setText(a.get("mobile"));

        Button update = (Button) findViewById(R.id.btnEdit);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
                i.putExtra("name", a.get("name"));
                i.putExtra("email", a.get("email"));
                i.putExtra("mobile", a.get("mobile"));
                i.putExtra("id", a.get("id"));
                startActivity(i);


            }
        });
    }
}
