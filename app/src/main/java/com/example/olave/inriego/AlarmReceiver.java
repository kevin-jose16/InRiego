package com.example.olave.inriego;

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
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.olave.inriego.MainActivity;
import com.example.olave.inriego.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;

import Persistencia.Json_SQLiteHelper;
import Persistencia.SQLiteHelper;

public class AlarmReceiver extends BroadcastReceiver {
    MainActivity ma;
    Json_SQLiteHelper json_sq;
    SQLiteDatabase dta_base;
    SharedPreferences sp;
    SQLiteHelper abd;
    Context contexto;
    int advice_cod;
    boolean esriego; //Chequear si es riego o lluvia
    String reg_riego;
    MediaPlayer mMediaPlayer;
    public boolean procedencia;
    @Override
    public void onReceive(Context context, Intent intent) {
        ma = (MainActivity) new MainActivity();
        sp = context.getSharedPreferences("sesion",Context.MODE_PRIVATE);
        contexto = context;
        if (this.probarConn(context)) {

            //this.mostrarMsg(context,"ALARMA CANCELADA");
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            int color = context.getResources().getColor(R.color.colornotif);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSound(uri)
                            .setPriority(Notification.PRIORITY_MAX)
                            .setSmallIcon(R.drawable.logoinriego)
                            .setContentTitle("Informacion sin Sincronizar")
                            .setColor(color)
                            .setContentText("Tienes datos sin sincronizar");

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
            json_sq= new Json_SQLiteHelper(context, "DBJsons", null, 1);
            dta_base = json_sq.getReadableDatabase();
            abd = new SQLiteHelper(dta_base, json_sq);
            Cursor result= abd.obtener();



            //if(result.getCount()>=1){
                boolean esalarma= true;
                //ma.Sincronizar(json_sq, esalarma);
                //new SincronizarDatos().execute();
                Intent nwintent = new Intent(context, MainActivity.class);
                PendingIntent sender = PendingIntent.getBroadcast(context, 0, nwintent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(sender);
                Toast.makeText(context, "Datos sincronizados", Toast.LENGTH_LONG).show();
            /*}
            else{
                //Toast.makeText(context, "No hay datos que sincronizar", Toast.LENGTH_LONG).show();
                mNotificationManager.notify(n, mBuilder.build());
                Intent nwintent = new Intent(context, MainActivity.class);
                PendingIntent sender = PendingIntent.getBroadcast(context, 0, nwintent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(sender);
            }*/
            //mNotificationManager.notify(n, mBuilder.build());
            // For our recurring task, we'll just display a message
            //Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();*/
        }
        else{
            //this.mostrarMsg(context,"No tiene conexion, intente mas tarde");
            Calendar ca = Calendar.getInstance();
            ca.setTimeInMillis(ca.getTimeInMillis() + 120000);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(),
                    120000, pendingIntent);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            int color = context.getResources().getColor(R.color.colornotif);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSound(uri)
                            .setPriority(Notification.PRIORITY_MAX)
                            .setSmallIcon(R.drawable.logoinriego)
                            .setContentTitle("CONEXION NULA")
                            .setColor(color)
                            .setContentText("No tenes conexion");

            Intent resultIntent = new Intent(context, MainActivity.class);

            // Because clicking the notification opens a new ("special") activity, there's
            // no need to create an artificial back stack.
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            4,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);

            // Gets an instance of the NotificationManager service//
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Random rand = new Random();

            int n = rand.nextInt(999) + 1;

            mNotificationManager.notify(n, mBuilder.build());




            /*Intent nwintent = new Intent(context, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, nwintent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);*/
            //this.startVariable(context,manager,pendingIntent,ca);
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
    public class SincronizarDatos extends AsyncTask<Void, Void, String> {

        String res;
        @Override
        protected String doInBackground(Void... voids) {

            Cursor result= abd.obtener();
            if(result.getCount()>=1){
                result.moveToFirst();
                if(result.getCount()>0) {
                    int cant_registrosbd = result.getCount()-1;

                    for(int i= 0; i<= cant_registrosbd; i++){
                        advice_cod = result.getInt(0);

                        try {
                            URL url = new URL("http://iradvisor.pgwwater.com.uy:9080/api/IrrigationData/AddIrrigation");
                            if(result.getString(5).equals("Irrigation")) {
                                url = new URL("http://iradvisor.pgwwater.com.uy:9080/api/IrrigationData/AddIrrigation");
                                esriego=true;
                            }
                            else {
                                if (result.getString(5).equals("Rain")) {
                                    url = new URL("http://iradvisor.pgwwater.com.uy:9080/api/IrrigationData/AddRain");
                                    esriego=false;
                                }
                            }
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            BufferedReader br;
                            InputStreamReader input;

                            StringBuffer response;
                            try {
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                                JSONObject obj = new JSONObject(result.getString(1));
                                reg_riego = result.getString(1);
                                out.write(String.valueOf(obj));
                                out.close();
                                //String msg = conn.getResponseMessage();
                                //int rsp =conn.getResponseCode();
                                input = new InputStreamReader(conn.getInputStream());
                                br =new BufferedReader(input);

                                String inputLine;
                                response = new StringBuffer();

                                while ((inputLine = br.readLine()) != null) {
                                    response.append(inputLine);
                                }
                                br.close();
                            }
                            finally {
                                conn.disconnect();
                            }
                            res=response.toString();
                            return res;

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(!result.isLast())
                            result.moveToNext();
                        else{
                            break;
                        }
                    }
                }
                else{

                }
                return res;
            }
            else
                return "no_hay_datos";
        }

        @Override
        protected void onPostExecute(String result) {
            Json_SQLiteHelper json_sq= new Json_SQLiteHelper(ma, "DBJsons", null, 1);
            SQLiteDatabase db = json_sq.getReadableDatabase();
            SQLiteHelper abd = new SQLiteHelper(db,json_sq);

            if (result!=null){
                if(result.equals("no_hay_datos"))
                    Toast.makeText(ma, "No hay datos que sincronizar", Toast.LENGTH_LONG).show();
                else {
                    try {
                        JSONObject json = new JSONObject(result);
                        Boolean isok = json.getBoolean("IsOk");
                        Calendar cal = Calendar.getInstance();
                        //preguntar isOk
                        if (isok) {
                            abd.borrar_regRiego(db, advice_cod);
                            if (esriego) {
                                abd.insertLog(cal.getTime() + " -- Sincronización exitosa del suiguiente registro de riego:\n" + reg_riego,sp.getString("username",""), json_sq);
                            } else {
                                abd.insertLog(cal.getTime() + " -- Sincronización exitosa del suiguiente registro de lluvia:\n" + reg_riego, sp.getString("username",""), json_sq);
                            }
                            Toast.makeText(ma, " -- Sincronización exitosa -- ",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            abd.insertLog(cal.getTime() + " -- Sincronización no exitosa, ERROR: " + json.getString("ErrorMessage"), sp.getString("username",""), json_sq);
                            Toast.makeText(ma, " -- Sincronización no exitosa, ERROR:\n" + json.getString("ErrorMessage"),
                                    Toast.LENGTH_LONG).show();
                        }

                        db.close();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(contexto, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    contexto.startActivity(i);
                }
            }
            else{
                Calendar cal = Calendar.getInstance();
                abd.insertLog(cal.getTime() +  "ERROR. No se sincronizaron los datos:\n"+reg_riego + "\nproblemas con la conexión a internet", sp.getString("username",""), json_sq);
                Toast.makeText(ma, "Se perdio la conexión a internet\no hay problemas con el servidor",
                        Toast.LENGTH_LONG).show();
                db.close();
            }
        }
    }

}