package com.example.olave.inriego;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.olave.inriego.MainActivity;
import com.example.olave.inriego.R;

import java.util.Calendar;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer mMediaPlayer;
    public boolean procedencia;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (this.probarConn(context)) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            int color = context.getResources().getColor(R.color.colornotif);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSound(uri)
                            .setPriority(Notification.PRIORITY_MAX)
                            .setSmallIcon(R.drawable.logoinriego)
                            .setContentTitle("Informacion sin Sincronizar")
                            .setColor(color)
                            .setContentText("Tienes N datos sin sincronizar");

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

            int n = rand.nextInt(999) + 1;
            //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//
            mNotificationManager.notify(n, mBuilder.build());
            // For our recurring task, we'll just display a message
            //Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        }
        else{
            this.mostrarMsg(context,"No tiene conexion, intente mas tarde");
            /*Calendar ca = Calendar.getInstance();
            ca.setTimeInMillis(ca.getTimeInMillis()+120000);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            this.startVariable(context,manager,pendingIntent,ca);*/
        }
    }
    public boolean probarConn(Context cont){

        ConnectivityManager cm = (ConnectivityManager) cont.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoNet = cm.getActiveNetworkInfo();

        if(infoNet != null){
            if(infoNet.isConnected()){
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }

    public void mostrarMsg(Context cont, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
        builder.setMessage(msg).setTitle("Conexion a Internet");
        builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();
    }
    public void startVariable(Context cont ,AlarmManager man, PendingIntent pend, Calendar cal) {
        AlarmManager manager = man;
        manager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pend);
        Toast.makeText(cont, "Next Alarm Set", Toast.LENGTH_SHORT).show();
    }


}