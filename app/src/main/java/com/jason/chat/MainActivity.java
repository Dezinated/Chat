package com.jason.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

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

        startBtn = (Button) findViewById(R.id.startChat);
        mAuth = FirebaseAuth.getInstance();


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
                    root.child("Queue").child(me.id).onDisconnect().removeValue();
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
                    finish();
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
            btn.setText("Cancel Search");
            root.child("Queue").child(me.id).setValue(me.id);
            ((LinearLayout) findViewById(R.id.searchingContainer)).setVisibility(View.VISIBLE);
        }else {
            btn.setText("Start Chat");
            root.child("Queue").child(me.id).removeValue();
            ((LinearLayout) findViewById(R.id.searchingContainer)).setVisibility(View.INVISIBLE);
        }
    }


}