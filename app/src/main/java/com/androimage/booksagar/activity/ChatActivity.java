package com.androimage.booksagar.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androimage.booksagar.R;
import com.androimage.booksagar.adapters.ChatListAdapter;
import com.androimage.booksagar.app.Config;
import com.androimage.booksagar.app.MyApplication;
import com.androimage.booksagar.app.PrefManager;
import com.androimage.booksagar.model.Book;
import com.androimage.booksagar.model.Chat;
import com.androimage.booksagar.realm.RealmController;
import com.bumptech.glide.Glide;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "CHAT_ACTIVITY";
    PrefManager prefManager;
    String seller_id;
    private List<Chat> chatList = new ArrayList<Chat>();
    private ListView listView;
    private ChatListAdapter adapter;
    private Button btn_send_msg;
    private EditText input_msg;
    private TextView title, author;
    private ImageView thumbnail;
    private String chat_id;

    private String temp_key, room_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (TextView) findViewById(R.id.title);

        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        btn_send_msg = (Button) findViewById(R.id.btn_send);
        input_msg = (EditText) findViewById(R.id.msg_input);
        author = (TextView) findViewById(R.id.author);
        listView = (ListView) findViewById(R.id.chatList);
        adapter = new ChatListAdapter(this, chatList);
        listView.setAdapter(adapter);

        prefManager = new PrefManager(getApplicationContext());
        Book b = RealmController.getInstance().getBook(Integer.valueOf(getIntent().getExtras().get("book_id").toString()));

        seller_id = Integer.toString(b.getSeller_id());
        //chat_id = b.getSeller_id()+"_"+b.getId()+"_"+getIntent().getExtras().get("id").toString().toString();
        //room_name = getIntent().getExtras().get("email").toString();
        setTitle(b.getTitle());


        if (b.getImageUrl() != null) {
            Glide.with(getApplicationContext())
                    .load(b.getImageUrl().replace("https", "http"))
                    .asBitmap()
                    .fitCenter()
                    .into(thumbnail);
        }

        title.setText(b.getTitle());
        author.setText(b.getAuthor());
        //root = FirebaseDatabase.getInstance().getReference().getRoot();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(chat_id, "");

        initChat();
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (input_msg.getText().length() != 0) {

                    java.util.Date date = new java.util.Date();
                    String time = new Timestamp(date.getTime()).toString();
                    //Chat c=new Chat(time,,input_msg.getText().toString(),prefManager.getUserDetails().get("name").toString());
                    HashMap<String, String> d = prefManager.getUserDetails();

                    Map<String, String> map1 = new HashMap<String, String>();
                    map1.put("cmd", "");
                    map1.put("timestamp", time);
                    map1.put("user_id", getIntent().getExtras().get("id").toString());
                    map1.put("seller_id", seller_id);
                    map1.put("book_id", getIntent().getExtras().get("book_id").toString());
                    map1.put("sender_id", d.get("id"));
                    map1.put("msg", input_msg.getText().toString());

                    append_chat_conversation(map1);
                    input_msg.setText("");
                }
            }
        });


    }

    private void initChat() {
        HashMap<String, String> d = prefManager.getUserDetails();

        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("cmd", "initChat");
        //map1.put("timestamp",time);
        map1.put("user_id", getIntent().getExtras().get("id").toString());
        map1.put("seller_id", seller_id);
        map1.put("book_id", getIntent().getExtras().get("book_id").toString());
        // map1.put("sender_id",d.get("id"));
        // map1.put("msg",input_msg.getText().toString());

        append_chat_conversation(map1);
    }


    public void append_chat_conversation(final Map<String, String> map1) {


        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_NOTIFYSELLER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject res = new JSONObject(response);
                    boolean error = res.getBoolean("error");
                    if (!error) {

                        JSONArray ja = res.getJSONArray("chats");
                        chatList.clear();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = ja.getJSONObject(i);

                            Log.d("ResponseChat", ja.toString());


                            Chat c = new Chat(jo.getString("timestamp"), jo.getString("from_id"), jo.getString("msg"), jo.getString("name"));
                            chatList.add(c);

                        }
                        adapter.notifyDataSetChanged();


                    }
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

                params = map1;

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, "ChatMsg");


    }


}

