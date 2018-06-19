package com.example.olave.inriego;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import Persistencia.Json_SQLiteHelper;
import Persistencia.SQLiteHelper;

public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer mMediaPlayer;
    public boolean procedencia;
    boolean activado=false;
    @Override
    public void onReceive(Context context, Intent intent) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int color = context.getResources().getColor(R.color.colornotif);
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 3, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSound(uri)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.logoinriego)
                        .setContentTitle("Informacion sin Sincronizar")
                        .setColor(color)
                        .setContentText("Tienes datos sin sincronizar")
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random rand = new Random();
        int n= 0;

        if (this.probarConn(context)) {

            //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//
            Json_SQLiteHelper json_sq= new Json_SQLiteHelper(context, "DBJsons", null, 1);
            SQLiteDatabase dta_base = json_sq.getReadableDatabase();
            SQLiteHelper abd = new SQLiteHelper(dta_base, json_sq);
            Cursor result= abd.obtener();

            if(result.getCount()>=1){
                mBuilder.setContentTitle("Informacion sin Sincronizar");
                mBuilder.setContentText("Tienes datos sin sincronizar");
                n = rand.nextInt(999) + 1;
                mNotificationManager.notify(n, mBuilder.build());}
            else{
                mBuilder.setContentTitle("Todo Sincronizado");
                mBuilder.setContentText("No hay datos sin sincronizar");
                n = rand.nextInt(999) + 1;
                mNotificationManager.notify(n, mBuilder.build());
            }
            // For our recurring task, we'll just display a message
            //Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        }
        else{

            if(Calendar.HOUR_OF_DAY == 21 && Calendar.MINUTE ==15) {
                Calendar ca = Calendar.getInstance();
                ca.setTimeInMillis(ca.getTimeInMillis() + 120000);
                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2, alarmIntent, 0);
                manager.setExact(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(), pendingIntent);
                mBuilder.setContentTitle("Sincronizacion Postergada");
                mBuilder.setContentText("La sincronizacion sera postergada");
                activado =true;

                n = rand.nextInt(999) + 1;
                mNotificationManager.notify(n, mBuilder.build());
            }

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
