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
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.olave.inriego.R;

import java.util.Calendar;
import java.util.Random;

import Persistencia.Json_SQLiteHelper;
import Persistencia.SQLiteHelper;

public class AlarmReceiver extends BroadcastReceiver {
    MediaPlayer mMediaPlayer;
    public boolean procedencia;
    boolean activado=false;
    Calendar cal = Calendar.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Calendar ca = Calendar.getInstance();
            ca.set(Calendar.HOUR_OF_DAY, 20);
            ca.set(Calendar.MINUTE, 0);
            ca.set(Calendar.SECOND, 0);
            if(ca.compareTo(cal) <=0)
                ca.add(Calendar.DATE,1);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int color = context.getResources().getColor(R.color.colornotif);
        //Intent resultIntent = new Intent(context, MainActivity.class);
        //PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 3, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSound(uri)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.logoinriego)
                        .setContentTitle("Informacion sin Sincronizar")
                        .setColor(color)
                        .setContentText("Tienes datos sin sincronizar")
                        //.setContentIntent(resultPendingIntent)
                        .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random rand = new Random();
        int n= 0;

        Json_SQLiteHelper json_sq= new Json_SQLiteHelper(context, "DBJsons", null, 1);
        SQLiteDatabase dta_base = json_sq.getReadableDatabase();
        SQLiteHelper abd = new SQLiteHelper(dta_base, json_sq);
        Cursor result= abd.obtener();

        if(result.getCount()>=1){
            if(cal.get(Calendar.HOUR_OF_DAY) == 21 && cal.get(Calendar.MINUTE) >=15) {
                    mBuilder.setContentTitle("Se enviarán mails a las 21:30 hs");
                    mBuilder.setContentText("Verifique tener conexión a esa hora");
                    n = rand.nextInt(999) + 1;
                    mNotificationManager.notify(n, mBuilder.build());

                    Calendar ca = Calendar.getInstance();
                    ca.set(Calendar.HOUR_OF_DAY, 20);
                    ca.set(Calendar.MINUTE, 0);
                    ca.set(Calendar.SECOND, 0);
                    if(ca.compareTo(cal) <=0)
                        ca.add(Calendar.DATE,1);

                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                    manager.setRepeating(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, pendingIntent);
            }

            else{
                //Codigo comentado para la entrega del módulo 2
                Calendar ca = Calendar.getInstance();
                ca.setTimeInMillis(ca.getTimeInMillis() + 900000); //Le agrego a la hora actual 15 minutos (en milisegundos)
                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2, alarmIntent, 0);
                manager.setExact(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(), pendingIntent);

                mBuilder.setContentTitle("Informacion sin Sincronizar");
                mBuilder.setContentText("Tienes datos sin sincronizar, recuerde tener conexión");
                n = rand.nextInt(999) + 1;
                mNotificationManager.notify(n, mBuilder.build());
            }
        }

        else{
            if(cal.get(Calendar.HOUR_OF_DAY) == 21 && cal.get(Calendar.MINUTE) >=15) {
                mBuilder.setContentTitle("Se enviarán mails a las 21:30 hs");
                mBuilder.setContentText("Verifique tener conexión a esa hora");
                n = rand.nextInt(999) + 1;
                mNotificationManager.notify(n, mBuilder.build());

                Calendar ca = Calendar.getInstance();
                ca.set(Calendar.HOUR_OF_DAY, 20);
                ca.set(Calendar.MINUTE, 0);
                ca.set(Calendar.SECOND, 0);
                if(ca.compareTo(cal) <=0)
                    ca.add(Calendar.DATE,1);

                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            else{
                Calendar ca = Calendar.getInstance();
                ca.set(Calendar.HOUR_OF_DAY, 21);
                ca.set(Calendar.MINUTE, 15);
                ca.set(Calendar.SECOND, 0);
                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2, alarmIntent, 0);
                manager.setExact(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(), pendingIntent);
            }
        }

    }

}
