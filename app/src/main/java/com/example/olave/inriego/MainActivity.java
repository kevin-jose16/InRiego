package com.example.olave.inriego;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import Clases.Establecimiento;
import Clases.Pivot;
import Clases.Riego;
import Persistencia.Json_SQLiteHelper;
import Persistencia.SQLiteHelper;
import layout.Fm_AgregarRiego;
import layout.Fm_Establecimiento;
import layout.Fm_agregarLluvia;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<String> pivots = new ArrayList<>();
    public ArrayList<Pivot> estab_pivots = new ArrayList<>();
    ArrayList<Establecimiento> farmslist;
    Json_SQLiteHelper json_sq;
    SQLiteDatabase dta_base;
    SharedPreferences sp;
    int actual_farm;
    String token;
    String farmId, farmdesc;
    boolean tiene_pivots = false, error_servidor = false;
    public String reference_date;
    int advice_cod;
    Calendar cal = Calendar.getInstance();
    boolean esriego; //Chequear si es riego o lluvia
    String reg_riego; //Json de registro de riego/lluvia pasado a string
    private PendingIntent pendingIntent = null;
    private PendingIntent pending = null;
    private AlarmManager manager;
    SQLiteHelper abd;
    ProgressDialog progress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setTitle("InRiego");


        setContentView(R.layout.main_activity);

        //Barra menu superior
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Pantalla que incluye el menu lateral y todo
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Barra Menu Lateral
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Sesion
        sp = getSharedPreferences("sesion",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //Preguntar si hay establecimiento seleccionado
        if(sp.getBoolean("hay_farm",false)){

            //Setear atributo de fecha de referencia
            reference_date= sp.getString("ReferenceDate",null);
            //Obtiene datos (json)
            String objetos = sp.getString("actual_farm", null);
            //Convierte json  a JsonArray.
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(objetos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Type listType = new TypeToken<ArrayList<Establecimiento>>(){}.getType();
            ArrayList<Establecimiento> hay_farm = new Gson().fromJson(jsonArray.toString(), listType);
            TextView tv_est = (TextView) findViewById(R.id.nav_farm);
            tv_est.setText(hay_farm.get(0).getDescripcion());
            setTitle(hay_farm.get(0).getDescripcion());
            actual_farm = hay_farm.get(0).getEst_id();
            farmdesc = hay_farm.get(0).getDescripcion();
            token = sp.getString("token", null);
            Fragment fragment= new FragmentPivot();
            FragmentManager fragmentManager =MainActivity.this.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frameprincipal, fragment).commit();
        }
        else{
            //Obtiene datos (json)
            String objetos = sp.getString("farmslist", null);
            //Convierte json  a JsonArray.
            JSONArray jsonArray = new JSONArray();
            try {
                jsonArray = new JSONArray(objetos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Preguntar si son mas de uno los establecimientos para darle a seleccionar o entrar directo a la app
            if(jsonArray.length()>1){
                Fragment fragment= new Fm_Establecimiento();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frameprincipal, fragment).commit();
            }
            else{
                //Convierte JSONArray a Lista de Objetos!
                Type listType = new TypeToken<ArrayList<Establecimiento>>(){}.getType();
                farmslist = new Gson().fromJson(jsonArray.toString(), listType);
                TextView tv_est = (TextView) findViewById(R.id.nav_farm);
                tv_est.setText(farmslist.get(0).getDescripcion());
                setTitle(farmslist.get(0).getDescripcion());
                actual_farm = farmslist.get(0).getEst_id();
                farmdesc = farmslist.get(0).getDescripcion();
                token = sp.getString("token",null);
                new ClaseAsincrona().execute(token,String.valueOf(farmslist.get(0).getEst_id()));
            }
        }

        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        Intent alarmIntent_mail = new Intent(MainActivity.this, AlarmReceiverMail.class);

        if(pendingIntent==null){
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            startAt20();
        }

        if(pending==null){
            pending = PendingIntent.getBroadcast(this, 1, alarmIntent_mail, 0);
            startAt2130();
        }

    }


    public void setItemVisible(int index, boolean inv){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(index).setVisible(inv);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = new Fm_Establecimiento();
        } else if (id == R.id.nav_riego) {
            fragment = new Fm_AgregarRiego();
        } else if (id == R.id.nav_lluvia) {
            fragment = new Fm_agregarLluvia();
        } else if (id == R.id.nav_verinfo) {
            fragment = new FragmentPivot();
        } else if (id == R.id.nav_sincronice){
            if(probarConn()) {
                /*json_sq= new Json_SQLiteHelper(MainActivity.this, "DBJsons", null, 1);
                dta_base = json_sq.getReadableDatabase();
                abd = new SQLiteHelper(dta_base,json_sq);
                new SincronizarDatos().execute();*/
                mostrarMsg("HAY CONEXION", "Conexión a Internet");
            }
            else{
                mostrarMsg("NO tiene conexion, intente mas tarde", "Conexión a Internet");
            }




        } else if (id == R.id.nav_logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("¿Desea Cerrar Sesión?");
            builder.setPositiveButton("SI",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPref = getSharedPreferences(
                            "sesion", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.clear();
                    editor.commit();
                    finish();
                    Intent i = new Intent(MainActivity.this,Login.class);
                    startActivity(i);
                }
            });
            builder.setNegativeButton("NO",null);
            builder.create();
            builder.show();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frameprincipal, fragment).commit();
        }


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showDatePickerDialog_Riego(View v) {
        DatePickerFragment_Riego newFrag = new DatePickerFragment_Riego();
        newFrag.show(this.getSupportFragmentManager(), "datePicker");
        newFrag.SetearFechas(reference_date);
    }
    public void showDatePickerDialog_Lluvia(View v) {
        DatePickerFragment_Lluvia newFrag = new DatePickerFragment_Lluvia();
        newFrag.show(getSupportFragmentManager(), "datePicker");
        newFrag.SetearFechas(reference_date);
    }

    public class ClaseAsincrona extends AsyncTask<String,Integer,String> {
        String res;
        String token_ca;

        @Override
        protected String doInBackground(String... params) {

            try {
                token_ca = params[0];
                farmId = params[1];
                URL url = new URL("http://iradvisor.pgwwater.com.uy:9080/api/IrrigationData/token/"+token_ca +"/farmId/" + farmId);
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
            Json_SQLiteHelper json_sq = new Json_SQLiteHelper(MainActivity.this, "DBJsons", null, 1);
            SQLiteDatabase dta_base = json_sq.getReadableDatabase();
            SQLiteHelper abd = new SQLiteHelper(dta_base, json_sq);
            if (result!=null){
                try {
                    JSONObject json = new JSONObject(result);
                    boolean b = json.getBoolean("IsOk");
                    if(json.getBoolean("IsOk")){
                        sp = getSharedPreferences("sesion",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        JSONObject jsonData = json.optJSONObject("Data");

                        //Setear fecha de referencia en atributo y sesion
                        reference_date = jsonData.get("ReferenceDate").toString();
                        editor.putString("ReferenceDate",jsonData.get("ReferenceDate").toString());

                        //JSONObject jsobject = jsonData.getJSONObject("Farm");
                        //farmdesc = jsobject.getString("Description");

                        JSONArray farm_pivots = jsonData.getJSONArray("IrrigationRows");
                        if(farm_pivots.length()>0) {
                            tiene_pivots = true;
                            for (int i = 0; i <= farm_pivots.length() - 1; i++) {
                                JSONObject pv = farm_pivots.getJSONObject(i);
                                //Integer.parseInt(pv.get("IrrigationId").toString()),
                                Pivot p = new Pivot(Integer.parseInt(pv.get("IrrigationUnitId").toString()), pv.get("Name").toString(), pv.get("Crop").toString(), pv.get("HarvestDate").toString(), pv.get("Phenology").toString());
                                JSONArray pv_riegos = pv.getJSONArray("Advices");

                                for (int r = 0; r <= pv_riegos.length() - 1; r++) {
                                    JSONObject riego = pv_riegos.getJSONObject(r);
                                    String f_riego = riego.get("Date").toString();
                                    Riego rg = new Riego(riego.get("IrrigationType").toString(), f_riego, Float.parseFloat(riego.get("Quantity").toString()));
                                    p.getRiegos().add(rg);
                                }
                                estab_pivots.add(p);

                            }

                            Establecimiento est = new Establecimiento(Integer.parseInt(farmId), farmdesc, reference_date);
                            est.setPivots(estab_pivots);
                            if(farmslist!= null)
                                farmslist.clear();
                            else
                                farmslist = new ArrayList<>();
                            farmslist.add(est);
                            String jsonObjetos = new Gson().toJson(farmslist);
                            editor.putString("actual_farm", jsonObjetos);
                            editor.putBoolean("hay_farm", true);
                            editor.commit();
                            Calendar cal = Calendar.getInstance();
                            abd.insertLog(cal.getTime().toString() + " Se selecciona el establecimiento " + farmdesc + " con respuesta correcta del servidor", sp.getString("username", ""), json_sq);
                            dta_base.close();
                        }
                    }
                    else{
                        error_servidor = true;
                        Calendar cal = Calendar.getInstance();
                        abd.insertLog(cal.getTime().toString() + " Se intento seleccionar el establecimiento " + farmdesc + " con respuesta no exitosa del servidor", sp.getString("username",""),json_sq);
                        dta_base.close();
                        Toast.makeText(MainActivity.this, "No se pudo seleccionar establecimiento\nError en el Servidor",
                                Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if(tiene_pivots) {
                    Fragment fragment= new FragmentPivot();
                    FragmentManager fragmentManager =MainActivity.this.getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frameprincipal, fragment).commit();
                    progress.setProgress(100); //progreso culminado
                }
                else{
                    if(!error_servidor) {
                        progress.setProgress(0);
                        Calendar cal = Calendar.getInstance();
                        abd.insertLog(cal.getTime().toString() + " Se intento seleccionar el establecimiento " + farmdesc + " pero éste no tiene pivots", sp.getString("username",""),json_sq);
                        dta_base.close();
                        Toast.makeText(MainActivity.this, "Establecimiento sin pivots\nSeleccione otro",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                        progress.setProgress(0);
                }

            }
            else{
                progress.setProgress(0);
                Calendar cal = Calendar.getInstance();
                abd.insertLog(cal.getTime().toString() + " No se pudo seleccionar un establecimiento por problemas en el servidor o la conexión a internet",sp.getString("username",""), json_sq);
                dta_base.close();
                Toast.makeText(MainActivity.this, "No tiene conexión a Internet",
                        Toast.LENGTH_LONG).show();
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
            progress=new ProgressDialog(MainActivity.this);
            progress.setMessage("Procesando...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setProgress(0);
            progress.setMax(100);
            progress.show();
        }

    }

    public Date CrearFecha(String fecha) {
        Calendar cal = Calendar.getInstance();
        int year,month,day;
        Date fecha_r = new Date();
        year = Integer.parseInt("" + fecha.charAt(0) + fecha.charAt(1) + fecha.charAt(2) + fecha.charAt(3));

        if ("0".equals(fecha.charAt(5)))
            month = Integer.parseInt("" + fecha.charAt(6));
        else
            month = Integer.parseInt("" + fecha.charAt(5) + fecha.charAt(6));

        if ("0".equals(fecha.charAt(8)))
            day = Integer.parseInt("" + fecha.charAt(9));
        else
            day = Integer.parseInt("" + fecha.charAt(8) + fecha.charAt(9));

        cal.set(year,month,day);
        return fecha_r= cal.getTime();
    }

    //Esta funcion se hace para el modulo 1
    public void cambiofragment(){
        Fragment fragment = new FragmentPivot();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameprincipal, fragment).commit();
    }

    public boolean probarConn(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public void mostrarMsg(String msg, String titulo){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setTitle(titulo);
        builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();
    }

    public void startAt2130() {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        /* Set the alarm to start at 21:30 hs */
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        if(cal.get(Calendar.HOUR_OF_DAY) > calendar.get(Calendar.HOUR_OF_DAY) || cal.get(Calendar.MINUTE) >= calendar.get(Calendar.MINUTE))
            calendar.add(Calendar.DATE,1);
        /* Repeating on every one day interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pending);
    }


    public void startAt20() {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        /* Set the alarm to start at 20:00 hs */
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if(cal.get(Calendar.HOUR_OF_DAY) > calendar.get(Calendar.HOUR_OF_DAY) || cal.get(Calendar.MINUTE) >= calendar.get(Calendar.MINUTE))
            calendar.add(Calendar.DATE,1);
        /* Repeating on every one day interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    public class SincronizarDatos extends AsyncTask<Void, Integer, String> {

        String res;

        NotificationCompat.Builder notificationBuilder;
        NotificationManager notificationManager;
        Random rand = new Random();
        int n = rand.nextInt(999) + 1;

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
            Json_SQLiteHelper json_sq= new Json_SQLiteHelper(MainActivity.this, "DBJsons", null, 1);
            SQLiteDatabase db = json_sq.getReadableDatabase();
            SQLiteHelper abd = new SQLiteHelper(db,json_sq);

            if (result!=null){
                if(result.equals("no_hay_datos"))
                    Toast.makeText(MainActivity.this, "No hay datos que sincronizar", Toast.LENGTH_LONG).show();
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
                            //Toast.makeText(MainActivity.this, " -- Sincronización exitosa -- ", Toast.LENGTH_LONG).show();
                            notificationBuilder.setContentTitle("¡Sincronización completada!").setAutoCancel(true).setProgress(0,0,false);
                            notificationManager.notify(n, notificationBuilder.build());
                        } else {
                            abd.insertLog(cal.getTime() + " -- Sincronización no exitosa, ERROR: " + json.getString("ErrorMessage"), sp.getString("username",""), json_sq);
                            //Toast.makeText(MainActivity.this, " -- Sincronización no exitosa, ERROR:\n" + json.getString("ErrorMessage"),Toast.LENGTH_LONG).show();
                            notificationBuilder.setAutoCancel(true).setProgress(0,0,false).setContentText("¡Hubo un error en la sincronización!");
                            notificationManager.notify(n, notificationBuilder.build());
                        }

                        db.close();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Esto es para modulo 1
                    cambiofragment();
                    //new ClaseAsincrona().execute(token,String.valueOf(actual_farm));
                }
            }
            else{
                Calendar cal = Calendar.getInstance();
                abd.insertLog(cal.getTime() +  "ERROR. No se sincronizaron los datos:\n"+reg_riego + "\nproblemas con la conexión a internet", sp.getString("username",""), json_sq);
                Toast.makeText(MainActivity.this, "Se perdio la conexión a internet\no hay problemas con el servidor",
                        Toast.LENGTH_LONG).show();
                db.close();
                //notificationBuilder.setContentText("¡Hubo un error en la sincronización!");
                notificationManager.notify(n, notificationBuilder.build());
            }
        }

        @Override
        protected void onPreExecute() {
            Intent resultIntent = new Intent(getBaseContext(), MainActivity.class);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(getBaseContext(),0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationBuilder  = new NotificationCompat.Builder(getApplicationContext())
                    .setSound(uri)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setProgress(100,0,true)
                    .setContentTitle("Sincronizando datos . . .")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(false);

            notificationManager.notify(n,notificationBuilder.build());
        }
        /*@Override
        protected void onProgressUpdate(Integer... progress) {

            notificationBuilder.setContentText(""+progress[0]+"%");
            notificationBuilder.setProgress(100, progress[0],false);
            notificationManager.notify(n, notificationBuilder.build());

        }*/
    }

}


