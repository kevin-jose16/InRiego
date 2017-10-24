package com.example.olave.inriego;

/**
 * Created by olave on 23/10/2017.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.olave.inriego.MainActivity;
import com.example.olave.inriego.R;

import java.util.Random;

public class AlarmReceiverMail extends BroadcastReceiver {
    MediaPlayer mMediaPlayer;
    public boolean procedencia;
    @Override
    public void onReceive(Context context, Intent intent) {

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int color = context.getResources().getColor(R.color.colornotif);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSound(uri)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.logoinriego)
                        .setContentTitle("MAIL SIN ENVIAR")
                        .setColor(color)
                        .setContentText("Tienes MAILS");

        Intent resultIntent = new Intent(context, MainActivity.class);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);

        // Gets an instance of the NotificationManager service//
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random rand = new Random();

        int  n = rand.nextInt(999) + 1;
        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//


        String email = "josekevin15@gmail.com";
        String subject = "Prueba proyecto";
        String message = "<html>\n" +
                "<title>HTML Tutorial</title>\n" +
                "<body>\n" +
                "\n" +
                "<h1>This is a heading</h1>\n" +
                "<p>This is a paragraph.</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        //Creating SendMail object

        SendMail sm = new SendMail(context, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
        mNotificationManager.notify(n, mBuilder.build());
        // For our recurring task, we'll just display a message
        //Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
    }


}