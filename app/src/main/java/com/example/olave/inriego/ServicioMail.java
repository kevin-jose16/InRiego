package com.example.olave.inriego;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Patterns;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import Persistencia.Json_SQLiteHelper;
import Persistencia.SQLiteHelper;

/**
 * Created by olave on 19/07/2018.
 */

public class ServicioMail extends Service {

    public int  n;
    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotificationManager;
    public Context contexto;
    public String possibleEmail;
    Random rand;
    public Calendar cal = Calendar.getInstance();
    Calendar hora_actual = Calendar.getInstance();
    boolean repetitivo = false;
    private boolean isConnected = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        //Obtengo mail del celular
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType("com.google");
        if(accounts.length > 0){
            possibleEmail = accounts[0].name;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flag ,int idProcess ) {

        NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        contexto=getApplicationContext();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        hora_actual = Calendar.getInstance();
        contexto = getApplicationContext();

        if(calendar.compareTo(hora_actual) <=0)
            calendar.add(Calendar.DATE,1);
        SharedPreferences sp = getSharedPreferences(
                "sesion", Context.MODE_PRIVATE);
        cal.setTimeInMillis(sp.getLong("hora_mail",calendar.getTimeInMillis()));
        repetitivo = sp.getBoolean("repetitivo",false);
        long delay = cal.getTimeInMillis()-hora_actual.getTimeInMillis();
       /* Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int color = getApplicationContext().getResources().getColor(R.color.colornotif);
        Intent resultIntent = new Intent(getApplicationContext(), getApplicationContext().getClass());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 4, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
       /* NotificationCompat.Builder notif_previa = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Sincronizacion")
                .setContentText("Se sincronizara en algunos minutos")
                .setAutoCancel(false)
                //.setSound(uri)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.logoinriego)
                .setColor(color)
                .setContentIntent(resultPendingIntent);


        mNotifyMgr.notify(1, notif_previa.build());*/

        //conexion = probarConn();

        if(repetitivo){
            //Toast.makeText(getApplicationContext(), "Se enviaran los mails", Toast.LENGTH_LONG).show();
            Timer tm = new Timer();
            tm.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                envio_mails();
                            }
                        }
                    , delay, 86400000);
        }
        else{
            //Toast.makeText(getApplicationContext(), "Envio de mails", Toast.LENGTH_LONG).show();
            Timer tm = new Timer();
            tm.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                envio_mails();
                            }
                        }
                    , cal.getTime());
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

    }

    public boolean probarConn(){

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoNet = cm.getActiveNetworkInfo();

        if(infoNet != null){
            if(infoNet.isConnected()){
                return true;
            }
            else{
                return false;
            }
        }

        /*if (cm != null) {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected) {
                            isConnected = true;
                        }
                        return true;
                    }
                }
            }
        }
        isConnected = false;*/
        return false;
    }
    public void envio_mails(){
        Looper.prepare();
        Intent resultIntent = new Intent(getApplicationContext(), Login.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 4, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int color = getApplicationContext().getResources().getColor(R.color.colornotif);
        mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSound(uri)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.logoinriego)
                .setColor(color)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        // Gets an instance of the NotificationManager service//
        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        rand = new Random();
        n = 0;

        if(probarConn()) {

            //Mail para los registros de riego/lluvia no sincronizados
            mailSincronizar();
            //Mail para los logs del dia
            mailLog();

            SharedPreferences sharedPref = contexto.getSharedPreferences(
                    "sesion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.putBoolean("mail_fallido", false);
            editor.commit();

            Intent i = new Intent(contexto, Login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contexto.startActivity(i);

        }
        else{
            hora_actual = Calendar.getInstance();
            if(hora_actual.get(Calendar.HOUR_OF_DAY) < 22 ){
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(calendar.getTimeInMillis() + 900000); //Le agrego a la hora actual 15 minutos (en milisegundos)
                String hora, minuto;
                if(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)).length()==1)
                    hora = 0 + Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
                else
                    hora = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
                if(Integer.toString(calendar.get(Calendar.MINUTE)).length()==1)
                    minuto = 0 + Integer.toString(calendar.get(Calendar.MINUTE));
                else
                    minuto = Integer.toString(calendar.get(Calendar.MINUTE));
                resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 4, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentTitle("Envio de Mails FALLIDO - No tiene conexión")
                        .setContentText("Se van a enviar los mails a las " + hora + ":" + minuto + " hs")
                        .setContentIntent(resultPendingIntent);
                n= rand.nextInt(999) + 1;
                mNotificationManager.notify(n, mBuilder.build());
                SharedPreferences sp = getSharedPreferences(
                        "sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("repetitivo", false);
                editor.putLong("hora_mail", calendar.getTimeInMillis());
                editor.commit();
                startService(new Intent(getApplicationContext(), ServicioMail.class));

            }
            else{
                n= rand.nextInt(999) + 1;
                mBuilder.setContentTitle("Envío de Mails FALLIDO")
                        //.setContentText("Los mails se enviarán durante el próximo Inicio de Sesión");
                        .setContentText("Los mails no se enviaron por falta de conexión");
                mNotificationManager.notify(n, mBuilder.build());
                SharedPreferences sp = getSharedPreferences(
                        "sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.putBoolean("mail_fallido", true);
                editor.commit();
                //startService(new Intent(getApplicationContext(), ServicioMail.class));
                Intent i = new Intent(contexto, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                contexto.startActivity(i);
            }

        }
    }
    public void mailSincronizar(){
        //Looper.prepare();
        Json_SQLiteHelper json_sq= new Json_SQLiteHelper(contexto, "DBJsons", null, 1);
        SQLiteDatabase dta_base = json_sq.getReadableDatabase();
        SQLiteHelper abd = new SQLiteHelper(dta_base, json_sq);
        Cursor result= abd.obtener();

        if(result.getCount()>=1){
            String message = "<html>\n" +
                    "<body>\n";
            String subject = "El usuario ";
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
            String email = "josekevin15@gmail.com"; //destinatario (va mail de PGG)

            //Creating SendMail object

            SendMail sm = new SendMail(contexto, email, subject, message);

            //Executing sendmail to send email
            sm.execute();

            /*mBuilder.setContentTitle("Mail de registros de Riego/Lluvia")
                    .setContentText("Se envia un mail con los registros de riegos/lluvias ingresados que no se habian sincronizado");
            n = rand.nextInt(999) + 1;
            mNotificationManager.notify(n, mBuilder.build());*/
        }
        dta_base.close();

    }

    public void mailLog(){
        //Looper.prepare();
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

        /*mBuilder.setContentTitle("Mail de Logs")
                .setContentText("Se envió un mail con los Logs de la jornada.");

        n = rand.nextInt(999) + 1;
        mNotificationManager.notify(n, mBuilder.build());*/
    }
    /*
    public class Mail_sincronizacion extends AsyncTask<Void, Integer, Cursor>{
        Json_SQLiteHelper json_sq;
        SQLiteDatabase dta_base;
        SQLiteHelper abd;
        @Override
        protected Cursor doInBackground(Void... voids) {
            Looper.prepare();
            json_sq= new Json_SQLiteHelper(contexto, "DBJsons", null, 1);
            dta_base = json_sq.getReadableDatabase();
            abd = new SQLiteHelper(dta_base, json_sq);
            Cursor result= abd.obtener();
            return result;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            if(result.getCount()>=1){
                String message = "<html>\n" +
                        "<body>\n";
                String subject = "El usuario ";
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
                dta_base.close();

                String email = "josekevin15@gmail.com"; //destinatario (va mail de PGG)

                //Creating SendMail object

                SendMail sm = new SendMail(getApplicationContext(), email, subject, message);

                //Executing sendmail to send email
                sm.execute();

                mBuilder.setContentTitle("Mail con registros de Riego/Lluvia no sincronizados")
                        .setContentText("Se envió un mail con estos datos");
                n = rand.nextInt(999) + 1;
                mNotificationManager.notify(n, mBuilder.build());
            }

        }
    }

    public class Mail_logs extends AsyncTask<Void, Integer, Cursor>{
        Json_SQLiteHelper json_sq;
        SQLiteDatabase dta_base;
        SQLiteHelper abd;
        @Override
        protected Cursor doInBackground(Void... voids) {
            Looper.prepare();
            json_sq= new Json_SQLiteHelper(contexto, "DBJsons", null, 1);
            dta_base = json_sq.getReadableDatabase();
            abd = new SQLiteHelper(dta_base, json_sq);
            Cursor result= abd.obtenerLog();
            return result;
        }
        @Override
        protected void onPostExecute(Cursor result) {
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

            SendMail sm = new SendMail(getApplicationContext(), email, subject, message);

            //Executing sendmail to send email
            sm.execute();
            //Toast.makeText(contexto, "Hay conexion", Toast.LENGTH_LONG).show();
            mBuilder.setContentTitle("Mail de Logs")
                    .setContentText("Se envió un mail con los Logs de la jornada.");

            n = rand.nextInt(999) + 1;
            mNotificationManager.notify(n, mBuilder.build());
        }

    }

    */
}
