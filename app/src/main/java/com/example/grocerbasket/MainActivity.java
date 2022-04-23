package com.example.grocerbasket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    ImageView logo,logoname;
    LottieAnimationView anim;
    Thread Timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hooks
        logo=findViewById(R.id.logo);
        logoname=findViewById(R.id.logoname);
        anim=findViewById(R.id.anim);


        //animation
        logo.animate().translationX(1400).setDuration(1000).setStartDelay(2000);
        logoname.animate().translationX(1400).setDuration(1000).setStartDelay(2000);
        anim.animate().translationY(1400).setDuration(1000).setStartDelay(2000);

        //CheckInternetConnection
        if (!isConnectedToInternet(this)) {
            startActivity(new Intent(MainActivity.this,NoInternet.class));
            finish();
        }
        else
        {
            Timer= new Thread(){
                @Override
                public void run() {
                    try{
                        synchronized (this){
                            wait(2750);
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }finally {
                        Intent intent=new Intent(MainActivity.this,Dashboard.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };

            Timer.start();
        }



    }

    private boolean isConnectedToInternet(MainActivity mainActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobilecon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wificon != null && wificon.isConnected()) || (mobilecon != null && mobilecon.isConnected())) {
            return true;
        } else {
            return false;
        }
    }
}