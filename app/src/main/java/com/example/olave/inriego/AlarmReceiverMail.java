package com.example.olave.inriego;

/**
 * Created by olave on 23/10/2017.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
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
import android.util.Patterns;

import com.example.olave.inriego.MainActivity;
import com.example.olave.inriego.R;

import org.json.JSONException;
import org.json.JSONObject;

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
    public Context contexto;
    public String possibleEmail;
    @Override
    public void onReceive(Context context, Intent intent) {

        contexto = context;
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int color = context.getResources().getColor(R.color.colornotif);
         mBuilder =
                new NotificationCompat.Builder(context)
                        .setSound(uri)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.logoinriego)
                        .setContentTitle("MAIL PARA SINCRONIZAR DATOS")
                        .setColor(color)
                        .setContentText("Se envio un mail con los datos que quedaron pendientes o no se pudieron sincronizar a tiempo");

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
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Random rand = new Random();

        n = rand.nextInt(999) + 1;
        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//

        //Obtengo mail del celular


        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
        if(accounts.length > 0){
            possibleEmail = accounts[0].name;
        }

        //mNotificationManager.notify(n, mBuilder.build());
        Json_SQLiteHelper json_sq= new Json_SQLiteHelper(contexto, "DBJsons", null, 1);
        SQLiteDatabase dta_base = json_sq.getReadableDatabase();
        SQLiteHelper abd = new SQLiteHelper(dta_base, json_sq);
        Cursor result= abd.obtener();

        //if(result.getCount()>=1)
            //this.mailSincronizar();

        this.mailLog();

        // For our recurring task, we'll just display a message
        //Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
    }


    public void mailSincronizar(){

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
                subject = subject + result.getString(3) + "ha agregado los siguientes registros de riego/lluvia";
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

        String email = "nadiacabrerayahn@gmail.com"; //destinatario (va mail de PGG)



        //Creating SendMail object

        SendMail sm = new SendMail(contexto, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
        mNotificationManager.notify(n, mBuilder.build());


    }

    public void mailLog(){
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
                message = message + "<body><html>";
            }
        }
        else
            message = "Hoy no se han ingresado o sincrinizado datos";
        dta_base.close();

        String email = "nadiacabrerayahn@gmail.com"; //destinatario (va mail de PGG)
        //Creating SendMail object

        SendMail sm = new SendMail(contexto, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }

}