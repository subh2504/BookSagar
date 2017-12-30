package com.androimage.booksagar.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androimage.booksagar.R;
import com.androimage.booksagar.adapters.BooksAdapter;
import com.androimage.booksagar.adapters.ExpandableListAdapter;
import com.androimage.booksagar.adapters.RealmBooksAdapter;
import com.androimage.booksagar.app.Config;
import com.androimage.booksagar.app.PrefManager;
import com.androimage.booksagar.app.Prefs;
import com.androimage.booksagar.model.Book;
import com.androimage.booksagar.realm.RealmController;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static String API_KEY = "123";
    boolean doubleBackToExitPressedOnce = false;
    List<String> groupList;
    List<Pair<String, Boolean>> childList;
    Map<String, List<Pair<String, Boolean>>> filtersCollection;
    ExpandableListView expListView;
    Set<String> language = new TreeSet<>();
    Set<String> author = new TreeSet<>();
    Set<String> standard = new TreeSet<>();
    Set<String> type = new TreeSet<>();
    Set<String> location = new TreeSet<>();
    private DrawerLayout mDrawerLayout;
    private BooksAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private Realm realm;
    private LayoutInflater inflater;
    private FloatingActionButton fab, fabFil;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fabFil = (FloatingActionButton) findViewById(R.id.fabFil);
        recycler = (RecyclerView) findViewById(R.id.recycler_view);

        //get BuyNSell instance
        this.realm = RealmController.with(this).getRealm();

        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();
        setupRecycler();


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup Navigation Drawer Layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });


        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("1st", "1st");


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
        setRealmAdapter(RealmController.with(this).getBooks());


        //add new item
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrefManager prefManager = new PrefManager(getApplicationContext());
                if (prefManager.isLoggedIn()) {
                    Intent i = new Intent(MainActivity.this, AddBookActivity.class);
                    MainActivity.this.startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, "Please Login to see the contact details", Toast.LENGTH_SHORT);

                    Intent i = new Intent(MainActivity.this, LoginActivity.class);

                    startActivity(i);
                }


            }
        });


        fabFil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilter();
            }
        });


    }

    private void showFilter() {
        final AlertDialog b;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.filter_layout, null);


        expListView = (ExpandableListView) dialogView.findViewById(R.id.filter_list);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                this, groupList, filtersCollection);
        expListView.setAdapter(expListAdapter);

        //setGroupIndicatorToRight();

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String selected = (String) expListAdapter.getChild(
                        groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                        .show();

                return true;
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Customize your results");
        dialogBuilder.setMessage("");

        Button btn = (Button) dialogView.findViewById(R.id.cancel);
        Button done = (Button) dialogView.findViewById(R.id.done);

//        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        filtersCollection=expListAdapter.getLaptopCollections();
//                        setRealmAdapter(RealmController.with(MainActivity.this).filter(filtersCollection));
//                    }
//                });
//        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //pass
//            }
//        });
        b = dialogBuilder.create();
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (b != null) {
                    b.cancel();
                }
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                filtersCollection = expListAdapter.getLaptopCollections();
                setRealmAdapter(RealmController.with(MainActivity.this).filter(filtersCollection));
                if (b != null) {
                    b.cancel();
                }
            }
        });
        b.show();
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

        RealmBooksAdapter realmAdapter = new RealmBooksAdapter(this.getApplicationContext(), books, true);
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


        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Config.GET_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        try {
                            if (!response.getBoolean("error")) {
                                JSONArray ja = response.getJSONArray("books");
                                RealmController.with(MainActivity.this).clearAll();


                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);

                                    Log.d("ResponseInt", ja.toString());

                                    Book b = new Book(jo.getInt("id"), jo.getInt("seller_id"), jo.getString("title"), jo.getString("author"), jo.getString("publisher"), jo.getInt("year"), jo.getString("mrp"), jo.getString("price"), jo.getString("standard"), jo.getString("language"), jo.getInt("verified") == 1, jo.getInt("sold") == 1, jo.getInt("noshared") == 1, jo.getString("added_at"), jo.getString("image"), jo.getString("description"), jo.getString("location"), jo.getString("type"));


                                    books.add(b);

                                    if (!jo.getString("language").equals("null")) {
                                        language.add(jo.getString("language"));
                                    }
                                    if (!jo.getString("author").equals("null")) {
                                        author.add(jo.getString("author"));
                                    }
                                    if (!jo.getString("standard").equals("null")) {
                                        standard.add(jo.getString("standard"));
                                    }
                                    if (!jo.getString("type").equals("null")) {
                                        type.add(jo.getString("type"));
                                    }
                                    if (!jo.getString("location").equals("null")) {
                                        location.add(jo.getString("location"));

                                    }


                                }

                                createGroupList();

                                createCollection();
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


                                Prefs.with(MainActivity.this).setPreLoad(true);
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.toString());
                        }

                        setRealmAdapter(RealmController.with(MainActivity.this).getBooks());


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
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getRequest);





/*

        for (Book b : books) {
            // Persist your data easily
            Log.d("Error.Response", b.getImageUrl());
            realm.beginTransaction();
            realm.copyToRealm(b);
            realm.commitTransaction();


            Prefs.with(this).setPreLoad(true);
            }
*/


    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
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

    private void createGroupList() {
        groupList = new ArrayList<String>();
        groupList.add("Language");
        groupList.add("Standard");
        groupList.add("Author");
        groupList.add("Type");
        groupList.add("Location");

    }

    private void createCollection() {


        filtersCollection = new LinkedHashMap<String, List<Pair<String, Boolean>>>();

        for (String group : groupList) {
            if (group.equals("Language")) {
                loadChild(language);
            } else if (group.equals("Standard")) {
                loadChild(standard);
            } else if (group.equals("Type")) {
                loadChild(type);
            } else if (group.equals("Author"))
                loadChild(author);
            else
                loadChild(location);

            filtersCollection.put(group, childList);
        }
    }

    private void loadChild(Set<String> childModels) {

        List<Pair<String, Boolean>> l = new ArrayList<>();
        for (String i : childModels) {

            Pair<String, Boolean> p = new Pair<String, Boolean>(i, false);
            l.add(p);
        }
        childList = l;
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onBackPressed() {


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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