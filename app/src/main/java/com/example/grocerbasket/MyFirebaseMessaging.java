package com.example.grocerbasket;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    static final String NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATIONS_CHANNEL_ID";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        String type = remoteMessage.getData().get("type");
        if (type.equals("NewOrder")) {
            String orderby=remoteMessage.getData().get("orderby");
            String orderto=remoteMessage.getData().get("orderto");
            String orderid = remoteMessage.getData().get("order");
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");

            if(firebaseUser != null && firebaseAuth.getCurrentUser().getPhoneNumber().equals(orderto)) {
                showNotification(orderid, title, message, type);
            }
        }
        if (type.equals("OrderStatus")) {

            String orderid = remoteMessage.getData().get("order");
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");

            showNotification(orderid, title, message, type);

        }
    }

    void showNotification(String orderid, String title, String message, String type) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationid = new Random().nextInt(3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupForNotificationChannel(notificationManager);
        }

        Intent intent = null;
        if (type.equals("NewOrder")) {
            intent = new Intent(this, FavouriteActivity.class);
            intent.putExtra("orderid", orderid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }
        else if (type.equals("OrderStatus")) {
            intent = new Intent(this, AdminShowOrderedProducts.class);
            intent.putExtra("orderid", orderid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        Uri notiUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logo)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(notiUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationid, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupForNotificationChannel(NotificationManager notificationManager) {
        CharSequence channel = "Some Sample Text";
        String channelDescription = "Channel Description Here";

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channel, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
