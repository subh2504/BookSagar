package com.androimage.booksagar.adapters;

/**
 * Created by subha on 9/24/2016.
 */


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androimage.booksagar.R;
import com.androimage.booksagar.model.Chat;

import java.util.List;

public class ChatListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Chat> chatItems;

    public ChatListAdapter(Activity activity, List<Chat> chatItems) {
        this.activity = activity;
        this.chatItems = chatItems;
    }

    @Override
    public int getCount() {
        return chatItems.size();
    }

    @Override
    public Object getItem(int location) {
        return chatItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.chat_row, null);


        TextView name = (TextView) convertView.findViewById(R.id.textViewName);
        TextView time = (TextView) convertView.findViewById(R.id.textViewTime);
        TextView message = (TextView) convertView.findViewById(R.id.textViewMessage);


        // getting movie data for the row
        Chat m = chatItems.get(position);

        name.setText(m.getName());
        message.setText(m.getMsg());
        time.setText("");

        return convertView;
    }

}