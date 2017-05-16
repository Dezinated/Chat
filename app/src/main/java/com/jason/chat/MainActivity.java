package com.jason.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.jason.chat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.google.firebase.iid.FirebaseInstanceId;

import android.provider.Settings.Secure;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseReference root;
    private FirebaseAuth mAuth;

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

        if(FirebaseRemoteConfig.getInstance().getDouble("forceVersion") != 1.0) {
            showUpdateDialog();
        }


        startBtn = (Button) findViewById(R.id.startChat);
        mAuth = FirebaseAuth.getInstance();

        //Print Token
        Log.d(TAG, "Printing fcm token");
        Log.d(TAG,  FirebaseInstanceId.getInstance().getToken());
        //Finish Print Token


        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success");
                            android_id = mAuth.getCurrentUser().getUid();
                            me = new User(android_id);
                            me.fcm = FirebaseInstanceId.getInstance().getToken();
                            root = Utils.getDatabase().getReference();
                            root.child("Users").child(me.id).setValue(me);
                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            System.exit(0);
                        }
                    }
                });
    }

    private void showUpdateDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("A New Update is Available");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.jason.chat"));
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, 0);
                }else {
                    dialog.dismiss();
                    finish();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startChat(View v) {
        Button btn = (Button) findViewById(R.id.startChat);
        if(btn.getText().toString().equals("Start Chat")){
            me.avaliable = true;
            btn.setText("Cancel Search");
            root.child("Users").child(me.id).setValue(me);
            ((LinearLayout) findViewById(R.id.searchingContainer)).setVisibility(View.VISIBLE);

        }else{
            me.avaliable = false;
            btn.setText("Start Chat");
            root.child("Users").child(me.id).setValue(me);
            ((LinearLayout) findViewById(R.id.searchingContainer)).setVisibility(View.INVISIBLE);
        }


    }


}