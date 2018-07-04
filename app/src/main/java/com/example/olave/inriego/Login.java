package com.example.olave.inriego;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import Clases.Establecimiento;
import Persistencia.Json_SQLiteHelper;
import Persistencia.SQLiteHelper;
import layout.Fm_Establecimiento;
import layout.Fm_agregarLluvia;

public class Login extends AppCompatActivity {

    EditText pass;
    EditText user;
    Button boton;
    public String possibleEmail;

    ArrayList<Establecimiento> farms = new ArrayList<Establecimiento>();

    private ProgressDialog progress;  //para mostrar barrita de progreso mientras demora el servidor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        boton = (Button) findViewById(R.id.button);
        pass = (EditText) findViewById(R.id.password);
        user = (EditText) findViewById(R.id.usuario);
        SharedPreferences sharedPref = this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        String us = sharedPref.getString("username", null );
        String passw = sharedPref.getString("password", null);
        if(us!=null && passw!=null){
            Intent i = new Intent(Login.this,MainActivity.class);
            startActivity(i);
            //user.setText(us);
            //pass.setText(passw);
        }
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ClaseAsincrona().execute(user.getText().toString(),pass.getText().toString());
            }
        });

        Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
        if(accounts.length > 0){
            possibleEmail = accounts[0].name;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mostrarMsg("Aplicación desarrollada por:\n\nNadia Cabrera\nnadiacabrerayahn@gmail.com\n\n" +
                    "Kevin José\njosekevin15@gmail.com\n\nCarina Maerro\nCMaerro@gmail.com", "Información");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public class ClaseAsincrona extends AsyncTask<String,Integer,String> {
        String res;
        String username;
        String password;
        @Override
        protected String doInBackground(String... params) {

            try {
                username = params[0];
                password = params[1];

                URL url = new URL("http://iradvisor.pgwwater.com.uy:9080/api/Auth/userName/"+username +"/password/" + password);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                res=response.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Json_SQLiteHelper json_sq = new Json_SQLiteHelper(Login.this, "DBJsons", null, 1);
            SQLiteDatabase dta_base = json_sq.getReadableDatabase();
            SQLiteHelper abd = new SQLiteHelper(dta_base, json_sq);
            if (result!=null){
                try {
                    JSONObject json = new JSONObject(result);

                    if(json.getBoolean("IsOk")){
                        SharedPreferences sp = Login.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("username",username);
                        editor.putString("password",password);
                        editor.putBoolean("hay_farm", false);
                        farms.clear();
                        JSONObject jsonData = json.optJSONObject("Data");
                        editor.putString("token",jsonData.get("Token").toString());
                        JSONArray estab = jsonData.getJSONArray("Farms");
                        for(int i=0;i<=estab.length()-1;i++){
                            JSONObject es = estab.getJSONObject(i);
                            Establecimiento e = new Establecimiento(Integer.parseInt(es.get("FarmId").toString()),es.get("Description").toString(),"");
                            farms.add(e);
                        }
                        String jsonObjetos = new Gson().toJson(farms);
                        editor.putString("farmslist", jsonObjetos);
                        editor.commit();

                        progress.setProgress(100); //progreso culminado

                        Calendar cal = Calendar.getInstance();

                        abd.insertLog(cal.getTime().toString() + " -- El usuario " + username + " ha ingresado en el sistema", username,json_sq);
                        Cursor cur = abd.obtenerLog();
                        cur.moveToFirst();
                        cur.getString(1);
                        dta_base.close();
                        //iniciar 2da activity despues del login
                        Intent i = new Intent(Login.this,MainActivity.class);
                        startActivity(i);
                    }
                    else{
                        Calendar cal = Calendar.getInstance();
                        abd.insertLog(cal.getTime().toString() + " -- El usuario " + username + " no existente, intento ingresar al sistema", username, json_sq);
                        dta_base.close();
                        progress.setProgress(0);
                        Toast.makeText(Login.this, "El nombre de usuario o la contraseña son incorrectos",
                                Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            else{
                Calendar cal = Calendar.getInstance();
                abd.insertLog(cal.getTime().toString() + " -- El usuario " + username + " no pudo ingresar al sistema por problemas en el servidor o la conexión a internet",username, json_sq);
                dta_base.close();
                Toast.makeText(Login.this, "Problema con servidor o conexión a internet", Toast.LENGTH_LONG).show();
                progress.setProgress(0);
            }
            progress.dismiss();
        }

        //procedimientos para ir actualizando y mostrar la barra de progreso
        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            progress=new ProgressDialog(Login.this);
            progress.setMessage("Procesando...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setProgress(0);
            progress.setMax(100);
            progress.show();
        }
    }

    public void mailSincronizar(){

        Json_SQLiteHelper json_sq= new Json_SQLiteHelper(this, "DBJsons", null, 1);
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
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
        /*n = rand.nextInt(999) + 1;
        mNotificationManager.notify(n, mBuilder.build());*/


    }

    public void mailLog(){
        Json_SQLiteHelper json_sq= new Json_SQLiteHelper(this, "DBJsons", null, 1);
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

        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
        /*n = rand.nextInt(999) + 1;
        mNotificationManager.notify(n, mBuilder.build());*/
    }

    public void mostrarMsg(String msg, String titulo){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setTitle(titulo);
        builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();
    }

}
