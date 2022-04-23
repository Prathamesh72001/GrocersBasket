package com.example.grocerbasket;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.grocerbasket.Session.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class Settings extends AppCompatActivity {

    SwitchCompat fcmSwitch;
    TextView notText;
    static final String enabled = "Enabled";
    static final String disabled = "Disabled";
    static final String topic = "PUSH_NOTIFICATION";
    static final String key = "AAAA8uRayzA:APA91bEykbsck74MlrrSTo07wd84IXg2kHSFua6IAYrZbCK9LKgjDbaRse9O36zrReUVy6qAGv9W-RxmS8N9-xl9lY75c9UYI1sh8zih6Clq5Jdj9rjBGNFpcUYzOKN3IKEVq2nKVHbP";
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        fcmSwitch = findViewById(R.id.fcmSwitch);
        notText = findViewById(R.id.notStatus);
        firebaseAuth = FirebaseAuth.getInstance();

        //Sessions
        SessionManager sessionManager = new SessionManager(Settings.this, SessionManager.SESSION_ADDRESS);
        boolean isSubscribed=sessionManager.checkIsSubscribed();
        if(isSubscribed){
            notText.setText(enabled);
            fcmSwitch.setChecked(true);
        }
        else{
            notText.setText(disabled);
            fcmSwitch.setChecked(false);
        }

        //switch
        fcmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    subscribeToTopic();
                }
                else {
                    unsubscribeToTopic();
                }
            }
        });
    }

    void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        SessionManager sessionManager = new SessionManager(Settings.this, SessionManager.SESSION_ADDRESS);
                        sessionManager.Subscribed();
                        notText.setText(enabled);
                        Toast.makeText(Settings.this,enabled,Toast.LENGTH_LONG).show();

                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Settings.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    void unsubscribeToTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        SessionManager sessionManager = new SessionManager(Settings.this, SessionManager.SESSION_ADDRESS);
                        sessionManager.UnSubscribed();
                        notText.setText(disabled);
                        Toast.makeText(Settings.this,disabled,Toast.LENGTH_LONG).show();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Settings.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }
}