package com.jason.chat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jason.chat.R;

import java.util.ArrayList;

/**
 * Created by Jason on 1/6/2017.
 */

public class MessageAdapter extends ArrayAdapter<Message> {

    private String myId;

    public MessageAdapter(Context context, int textViewResourceId, String id) {
        super(context, textViewResourceId);
        myId = id;
    }

    public MessageAdapter(Context context, ArrayList<Message> items, String id) {
        super(context, R.layout.message, items);
        myId = id;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.message, null);
        }

        Message p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.messageText);
            LinearLayout container = (LinearLayout) v.findViewById(R.id.messageContainer);

            if (tt1 != null) {
                tt1.setText(p.text);
                if(p.senderId == myId){
                    container.setGravity(Gravity.RIGHT);
                    tt1.setBackgroundResource(R.drawable.message_bubble_send);
                }
            }

        }

        return v;
    }

}
