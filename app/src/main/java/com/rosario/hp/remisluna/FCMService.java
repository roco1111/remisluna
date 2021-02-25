package com.rosario.hp.remisluna;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String titulo;
    private String texto;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Create and show notification

        sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"));
        sendNewPromoBroadcast(remoteMessage);
    }

    private void sendNewPromoBroadcast(RemoteMessage remoteMessage) {
        Intent intent = new Intent(MainViaje.ACTION_NOTIFY_NEW_PROMO);
        titulo = remoteMessage.getData().get("title");
        texto = remoteMessage.getData().get("body");

        intent.putExtra("title", titulo);
        intent.putExtra("description", texto);


        //guardar_notificacion();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }

    private void sendNotification(String messageBody, String title) {

        Intent intent = new Intent(this, MainViaje.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            notificationManager.createNotificationChannel(notificationChannel);

            builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
            //builder = new NotificationCompat.Builder(getApplicationContext());
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        builder = builder
                .setSmallIcon(R.drawable.icono_toolbar)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, builder.build());
    }






}