package com.example.olave.inriego;

/**
 * Created by olave on 23/10/2017.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;

import com.example.olave.inriego.MainActivity;
import com.example.olave.inriego.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;
import java.util.regex.Pattern;

import Persistencia.Json_SQLiteHelper;
import Persistencia.SQLiteHelper;

public class AlarmReceiverMail extends BroadcastReceiver {
    MediaPlayer mMediaPlayer;
    public boolean procedencia;
    public int  n;
    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotificationManager;
    public String possibleEmail;
    Random rand;
    Calendar cal = Calendar.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Calendar ca = Calendar.getInstance();
            ca.set(Calendar.HOUR_OF_DAY, 21);
            ca.set(Calendar.MINUTE, 28);
            ca.set(Calendar.SECOND, 0);
            if(ca.compareTo(cal) <=0)
                ca.add(Calendar.DATE,1);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, AlarmReceiverMail.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, 0);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        Calendar cal = Calendar.getInstance();
        if(calendar.compareTo(cal) <=0)
            calendar.add(Calendar.DATE,1);
        SharedPreferences sharedPref = context.getSharedPreferences(
                "sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("repetitivo", true);
        editor.putLong("hora_mail", calendar.getTimeInMillis());
        editor.commit();
        Intent intent_mail = new Intent(context, ServicioMail.class);
        context.startService(intent_mail);

        /*Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int color = context.getResources().getColor(R.color.colornotif);
        mBuilder = new NotificationCompat.Builder(context)
            .setSound(uri)
            .setPriority(Notification.PRIORITY_MAX)
            .setSmallIcon(R.drawable.logoinriego)
            .setColor(color);

        Intent resultIntent = new Intent(context, Login.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 4, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);

        // Gets an instance of the NotificationManager service//
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        rand = new Random();

        n = 0;


        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//

        //Obtengo mail del celular
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
        if(accounts.length > 0){
            possibleEmail = accounts[0].name;
        }


        //mNotificationManager.notify(n, mBuilder.build());
        Json_SQLiteHelper json_sq= new Json_SQLiteHelper(context, "DBJsons", null, 1);
        SQLiteDatabase dta_base = json_sq.getReadableDatabase();
        SQLiteHelper abd = new SQLiteHelper(dta_base, json_sq);
        Cursor result= abd.obtener();

        if(probarConn(context)) {
            //Mail para datos no sincronizados
            if (result.getCount() >= 1)
                this.mailSincronizar(context);

            //Mail para los logs del dia
            this.mailLog(context);
            SharedPreferences sharedPref = context.getSharedPreferences(
                    "sesion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.putBoolean("mail_fallido", false);
            editor.commit();

            Intent i = new Intent(context, Login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }
        else{

            //Esto lo tengo comentado para enviar la primer version desarrollada
           if(cal.get(Calendar.HOUR_OF_DAY) < 22 ) {
                Calendar ca = Calendar.getInstance();
                ca.setTimeInMillis(ca.getTimeInMillis() + 900000); //Le agrego a la hora actual 15 minutos (en milisegundos)

                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, AlarmReceiverMail.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, 0);
                manager.setExact(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(), pendingIntent);

                resultIntent = new Intent(context, MainActivity.class);
                resultPendingIntent = PendingIntent.getActivity(context, 4, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
                String hora, minuto;
                if(Integer.toString(ca.get(Calendar.HOUR_OF_DAY)).length()==1)
                    hora = 0 + Integer.toString(ca.get(Calendar.HOUR_OF_DAY));
                else
                    hora = Integer.toString(ca.get(Calendar.HOUR_OF_DAY));
                if(Integer.toString(ca.get(Calendar.MINUTE)).length()==1)
                    minuto = 0 + Integer.toString(ca.get(Calendar.MINUTE));
                else
                    minuto = Integer.toString(ca.get(Calendar.MINUTE));
                mBuilder.setContentIntent(resultPendingIntent)
                        .setContentTitle("Envio de Mails FALLIDO - No tiene conexión")
                        .setContentText("Se van a enviar los mails a las " + hora + ":" + minuto + " hs");

                n= rand.nextInt(999) + 1;
                mNotificationManager.notify(n, mBuilder.build());
            }
            else{
                Calendar ca = Calendar.getInstance();
                ca.set(Calendar.HOUR_OF_DAY, 21);
                ca.set(Calendar.MINUTE, 30);
                ca.set(Calendar.SECOND, 0);
                if(ca.compareTo(cal) <=0)
                    ca.add(Calendar.DATE,1);

                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, AlarmReceiverMail.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, 0);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                resultIntent = new Intent(context, Login.class);
                resultPendingIntent = PendingIntent.getActivity(context, 4, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent)
                        .setContentTitle("Envío de Mails FALLIDO")
                        //.setContentText("Los mails se enviarán durante el próximo Inicio de Sesión");
                        .setContentText("Los mails no se enviaron por falta de conexión");
                n= rand.nextInt(999) + 1;
                mNotificationManager.notify(n, mBuilder.build());

                SharedPreferences sharedPref = context.getSharedPreferences(
                        "sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.putBoolean("mail_fallido",true);
                editor.commit();

                Intent i = new Intent(context, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        }*/
    }


    public void mailSincronizar(Context contexto){

        Json_SQLiteHelper json_sq= new Json_SQLiteHelper(contexto, "DBJsons", null, 1);
        SQLiteDatabase dta_base = json_sq.getReadableDatabase();
        SQLiteHelper abd = new SQLiteHelper(dta_base, json_sq);
        Cursor result= abd.obtener();

        String message = "<html>\n" +
                "<body>\n";
        String subject = "El usuario ";

        if(result.getCount()>=1){
            result.moveToFirst();
            if ( result.getCount()>0) {
                int cant_registrosbd = result.getCount()-1;

                message = message + "<p>USUARIO: " + result.getString(3) + "</p>";
                subject = subject + result.getString(3) + " ha agregado los siguientes registros de riego/lluvia";
                message = message + "<p>EMAIL: " + possibleEmail + "</p><hr>";

                for(int i=0; i<=cant_registrosbd; i++) {

                    try {
                        JSONObject json = new JSONObject(result.getString(1));
                        message = message + "<p>Establecimiento: " + result.getString(4) + "</p>";
                        message = message + "<p>Milimetros: " + json.get("Milimeters").toString() + "</p>";
                        message = message + "<p>Fecha: " + json.get("Date").toString() + "</p>";
                        message = message + "<p>Pivots: " + json.get("IrrigationUnitId").toString() + "</p>";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    message = message + "<p>Tipo riego: " + result.getString(5) + "</p><hr><hr></body></html>";

                    result.moveToNext();
                }
           }
        }
        dta_base.close();

        String email = "josekevin15@gmail.com"; //destinatario (va mail de PGG)

        //Creating SendMail object

        SendMail sm = new SendMail(contexto, email, subject, message);

        //Executing sendmail to send email
        sm.execute();

        mBuilder.setContentTitle("Mail de registros de Riego/Lluvia")
                .setContentText("Se envia un mail con los registros de riegos/lluvias ingresados que no se habian sincronizado");
        n = rand.nextInt(999) + 1;
        mNotificationManager.notify(n, mBuilder.build());

    }

    public void mailLog(Context contexto){
        Json_SQLiteHelper json_sq= new Json_SQLiteHelper(contexto, "DBJsons", null, 1);
        SQLiteDatabase dta_base = json_sq.getReadableDatabase();
        SQLiteHelper abd = new SQLiteHelper(dta_base, json_sq);
        Cursor result= abd.obtenerLog();

        String message = "<html>\n" +

                "<body>\n";

        String subject = "LOG total de la jornada";

        if(result.getCount()>=1){
            result.moveToFirst();
            if ( result.getCount()>0) {
                int cant_registrosbd = result.getCount()-1;

                for(int i=0; i<=cant_registrosbd; i++) {
                    message = message + "<p>" + result.getString(1) + "</p></br>";

                    result.moveToNext();
                }
                message = message + "</body></html>";
            }
        }
        else
            message = "Hoy no se han ingresado o sincronizado datos";

        dta_base.close();

        String email = "josekevin15@gmail.com"; //destinatario (va mail de PGG)
        //Creating SendMail object

        SendMail sm = new SendMail(contexto, email, subject, message);

        //Executing sendmail to send email
        sm.execute();

        mBuilder.setContentTitle("Mail de Logs")
                .setContentText("Se envió un mail con los Logs de la jornada.");

        n = rand.nextInt(999) + 1;
        mNotificationManager.notify(n, mBuilder.build());
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


}