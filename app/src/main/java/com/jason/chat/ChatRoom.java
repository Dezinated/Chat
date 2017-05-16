package com.jason.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Created by Jason on 1/6/2017.
 */

public class ChatRoom extends AppCompatActivity {

    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    //DatabaseReference myRef = database.getReference("rooms");
    ArrayList<Message> messages;
    private String roomId = "";
    private String myId = "";
    MessageAdapter msgAdapter;
    private boolean chatEnded = false;

    public boolean onOptionsItemSelected(MenuItem item){


        new AlertDialog.Builder(this)
                .setTitle("Leave chat")
                .setMessage("Do you really want to leave the chat?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        sendExit();
                        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivityForResult(myIntent, 0);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
        return true;

    }

    public void sendExit() {
        if(!chatEnded) {
            Message m = new Message("exit", "exit");
            root.child("Rooms").child(roomId).push().setValue(m);
            clearChatRoom();
        }
    }

    public void clearChatRoom() {
        root.child("Rooms").child(roomId).setValue("");
    }

    protected void onDestroy(){
        sendExit();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        chatEnded = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        messages = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        roomId = (String) intent.getExtras().get("id");
        myId = (String) intent.getExtras().get("androidId");
        getSupportActionBar().setTitle("Chat ");

        ListView yourListView = (ListView) findViewById(R.id.messagesList);
        msgAdapter = new MessageAdapter(this, messages, myId);
        yourListView.setAdapter(msgAdapter);


        root.child("Rooms").child(roomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                Message m = snapshot.getValue(Message.class);
                if (m.senderId.equals("exit")) {
                    chatEnded = true;
                    TextView textBox = (TextView) findViewById(R.id.inputMessage);
                    textBox.setFocusable(false);
                }
                messages.add(m);
                msgAdapter.notifyDataSetChanged();
                ((ListView) findViewById(R.id.messagesList)).setSelection(msgAdapter.getCount() - 1);
            }
            public void onChildChanged(DataSnapshot snapshot, String previousChild) {
                //onChildAdded(snapshot,previousChild);
            }
            public void onChildRemoved(DataSnapshot snapshot) {}
            public void onChildMoved(DataSnapshot snapshot, String previousChild) {}
            public void onCancelled(DatabaseError e) {}
        });
    }

    public void sendBtn(View v)
    {
        TextView textBox = (TextView) findViewById(R.id.inputMessage);
        if(textBox.getText().toString().equals(""))
            return;
        Message m = new Message(textBox.getText().toString(),myId);
        textBox.setText("");
        root.child("Rooms").child(roomId).push().setValue(m);
    }
}
