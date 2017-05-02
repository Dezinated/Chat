package com.jason.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jason.chat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import android.provider.Settings.Secure;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseReference root;
    String TAG = "MainActivity";

    private Button startBtn;
    private String android_id;
    Intent chatIntent;
    User me;

    private ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = (Button) findViewById(R.id.startChat);

        root = FirebaseDatabase.getInstance().getReference();
        android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        me = new User(android_id);

        //log on to app
        root.child("Users").child(me.id).setValue(me);
        root.child("Users").child(me.id).onDisconnect().removeValue();

    }

    @Override
    protected void onStop(){
        if(!me.avaliable){
            super.onStop();
            return;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle("Searching")
                        .setContentText("Searching for someone to chat with");
        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setProgress(0, 0, true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        //mNotificationManager.notify(1, mBuilder.build());

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "DESTROYED");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        super.onDestroy();
    }

    public void startChat(View v) {
        Button btn = (Button) findViewById(R.id.startChat);
        if(btn.getText().toString().equals("Start Chat")){
            me.avaliable = true;
            root.child("Users").child(me.id).setValue(me);
            btn.setText("Cancel Search");

            ((LinearLayout) findViewById(R.id.searchingContainer)).setVisibility(View.VISIBLE);
            new StartChat().execute("");

        }else{
            me.avaliable = false;
            root.child("Users").child(me.id).setValue(me);
            btn.setText("Start Chat");

            ((LinearLayout) findViewById(R.id.searchingContainer)).setVisibility(View.INVISIBLE);
        }

    }



    private class StartChat extends AsyncTask<String, Integer, String> {

        private User u = null;
        private Query queryRef;
        private boolean searching = false;

        private void createNotitification(Context c){

            NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(c)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setContentTitle("Searching")
                            .setContentText("Searching for someone to chat with");
            Intent resultIntent = new Intent(c, MainActivity.class);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            c,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setProgress(0, 0, true);
// mId allows you to update the notification later on.
            mNotificationManager.notify(1, mBuilder.build());
        }

        private void cancelNotification(Context c){
            NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1);
        }

        @Override
        protected void onPreExecute() {

            Log.d(TAG, "start search");
            createNotitification(MainActivity.this);

            queryRef = root.child("Users").orderByChild("avaliable");
            queryRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                    User snap = snapshot.getValue(User.class);
                    if(!snap.id.equals(android_id) && snap.avaliable) {
                        u = snap;
                        u.avaliable = false;
                        me.avaliable = false;
                        searching = false;
                        root.child("Users").child(snapshot.getKey()).setValue(u);
                        root.child("Users").child(me.id).setValue(u);
                    }
                }
                public void onChildChanged(DataSnapshot snapshot, String previousChild) {
                    onChildAdded(snapshot,previousChild);
                }
                public void onChildRemoved(DataSnapshot snapshot) {}
                public void onChildMoved(DataSnapshot snapshot, String previousChild) {}
                public void onCancelled(DatabaseError e) {}
            });

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            while(u == null || me.avaliable || searching)
                continue;
            Log.d(TAG, "doInBackground: Search ended " + me.avaliable);
            return u.id;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "done: " + result);
            cancelNotification(MainActivity.this);

            if(!result.equals("-1")) {
                Log.d(TAG, "Found user: " + result);

                Intent intent = new Intent(MainActivity.this, ChatRoom.class);
                if(result.compareTo(android_id) > 0) {
                    Log.d(TAG, result + " >  " + android_id);
                    intent.putExtra("id", result);
                }else{
                    Log.d(TAG, result + " < " + android_id);
                    intent.putExtra("id",android_id);
                }
                intent.putExtra("androidId",android_id);
                startActivity(intent);

                super.onPostExecute(result);
            }
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "cancelled");
        }


    }
}