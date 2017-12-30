package com.androimage.booksagar.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androimage.booksagar.R;
import com.androimage.booksagar.adapters.BooksAdapter;
import com.androimage.booksagar.adapters.RealmBooksAdapter;
import com.androimage.booksagar.app.Config;
import com.androimage.booksagar.app.MyApplication;
import com.androimage.booksagar.app.PrefManager;
import com.androimage.booksagar.app.Prefs;
import com.androimage.booksagar.model.Book;
import com.androimage.booksagar.realm.RealmController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;


public class MyBooksActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MyBooksActivity.class.getSimpleName();

    private final static String API_KEY = "123";
    PrefManager pref;
    private BooksAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private Realm realm;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);
        recycler = (RecyclerView) findViewById(R.id.recycler_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.realm = RealmController.with(this).getRealm();

        pref = new PrefManager(getApplicationContext());

        setupRecycler();

        if (!Prefs.with(this).getPreLoad()) {
            setRealmData();
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        setRealmData();
                                    }
                                }
        );


        // refresh the Releam instance
        RealmController.with(this).refresh();
        // get all persisted objects
        // create the helper adapter and notify data set changes
        // changes will be reflected automatically
        setRealmAdapter(RealmController.with(this).myBooks(pref.getUserDetails().get("id")));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
// Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);

    }

    public void setRealmAdapter(RealmResults<Book> books) {

        RealmBooksAdapter realmAdapter = new RealmBooksAdapter(this, books, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recycler.setHasFixedSize(true);

        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);
        recycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), false));
        recycler.setItemAnimator(new DefaultItemAnimator());
        // create an empty adapter and add it to the recycler view
        adapter = new BooksAdapter(this);
        recycler.setAdapter(adapter);


    }

    private void setRealmData() {


//        swipeRefreshLayout.setRefreshing(true);


        final ArrayList<Book> books = new ArrayList<>();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.GET_URL_ID, new Response.Listener<String>() {

            @Override
            public void onResponse(String res) {

                try {
                    Log.d("Response", res.toString());
                    JSONObject response = new JSONObject(res);
                    // display response
                    Log.d("Response", response.toString());
                    if (!response.getBoolean("error")) {
                        JSONArray ja = response.getJSONArray("books");
                        RealmController.with(MyBooksActivity.this).clearAll();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = ja.getJSONObject(i);

                            Log.d("ResponseInt", ja.toString());

                            Book b = new Book(jo.getInt("id"), jo.getInt("seller_id"), jo.getString("title"), jo.getString("author"), jo.getString("publisher"), jo.getInt("year"), jo.getString("mrp"), jo.getString("price"), jo.getString("standard"), jo.getString("language"), jo.getInt("verified") == 1, jo.getInt("sold") == 1, jo.getInt("noshared") == 1, jo.getString("added_at"), jo.getString("image"), jo.getString("description"), jo.getString("location"), jo.getString("type"));

                            books.add(b);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    for (Book b : books) {
                        // Persist your data easily
                        Log.d("Error.Response", b.getImageUrl());
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(b);
                        realm.commitTransaction();


                        Prefs.with(MyBooksActivity.this).setPreLoad(true);
                    }
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                }


                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", pref.getUserDetails().get("id"));

                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, "GettingUserBooks");
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        setRealmAdapter(RealmController.with(this).queryedBooks(query, query));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        setRealmAdapter(RealmController.with(this).queryedBooks(query, query));
        return true;
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        setRealmData();
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRealmData();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}