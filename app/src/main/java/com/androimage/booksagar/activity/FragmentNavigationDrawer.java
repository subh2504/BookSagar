package com.androimage.booksagar.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androimage.booksagar.R;
import com.androimage.booksagar.app.PrefManager;


public class FragmentNavigationDrawer extends Fragment {

    PrefManager pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        pref = new PrefManager(getActivity());
        ImageView log = (ImageView) view.findViewById(R.id.log);
        TextView logOut = (TextView) view.findViewById(R.id.tvLogout);
        TextView myBooks = (TextView) view.findViewById(R.id.tvMyBooks);
        TextView profile = (TextView) view.findViewById(R.id.tvProfile);
        TextView name = (TextView) view.findViewById(R.id.name);

        TextView email = (TextView) view.findViewById(R.id.email);

        TextView aboutUs = (TextView) view.findViewById(R.id.tvAboutus);
        TextView rateApp = (TextView) view.findViewById(R.id.tvRateus);
        TextView developers = (TextView) view.findViewById(R.id.tvDevelopers);


        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(i);


            }
        });


        rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent in = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(in);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }


            }
        });

        developers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DeveloperActivity.class);
                startActivity(i);


            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager pref = new PrefManager(getActivity());
                if (pref.isLoggedIn()) {
                    Intent i = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);

                }

            }
        });


        if (pref.isLoggedIn()) {
            logOut.setText("Logout");
            email.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            email.setText(pref.getUserDetails().get("email"));
            name.setText(pref.getUserDetails().get("name"));
            log.setImageResource(R.drawable.logout);
        } else {
            logOut.setText("Login/SignUp");
            email.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            email.setText("");
            name.setText("");
            log.setImageResource(R.drawable.login);

        }

        myBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pref.isLoggedIn()) {
                    Intent i = new Intent(getActivity(), MyBooksActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);

                }


            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager pref = new PrefManager(getActivity());
                pref.clearSession();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();

            }
        });
        return view;
    }

}