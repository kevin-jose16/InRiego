package com.example.olave.inriego;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Clases.Establecimiento;
import Clases.Pivot;
import Clases.Riego;
import layout.Fm_AgregarRiego;
import layout.Fm_Establecimiento;
import layout.Fm_agregarLluvia;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<String> pivots = new ArrayList<>();
    public ArrayList<Pivot> estab_pivots = new ArrayList<>();
    SharedPreferences sp;
    String farmId, farmdesc;
    ArrayList<Establecimiento> farmslist;

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        if(sp.getBoolean("hay_farm",false)){
            Gson gson = new Gson(); //Instancia Gson.
            //Obtiene datos (json)
            String objetos = sp.getString("actual_farm", null);
            //Convierte json  a JsonArray.
            //String json = new Gson().toJson(objetos);
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
            Fragment fragment= new FragmentPivot();
            FragmentManager fragmentManager =MainActivity.this.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frameprincipal, fragment).commit();
        }
        else{
            Gson gson = new Gson(); //Instancia Gson.
            //Obtiene datos (json)
            String objetos = sp.getString("farmslist", null);
            //Convierte json  a JsonArray.
            //String json = new Gson().toJson(objetos);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(objetos);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                String token = sp.getString("token",null);
                new ClaseAsincrona().execute(token,String.valueOf(farmslist.get(0).getEst_id()));
            }
        }


        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

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
           start();
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPref = getSharedPreferences(
                    "sesion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();
            finish();
            Intent i = new Intent(MainActivity.this,Login.class);
            startActivity(i);
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
        newFrag.SetearFechas("2017-09-24");
    }
    public void showDatePickerDialog_Lluvia(View v) {
        DatePickerFragment_Lluvia newFrag = new DatePickerFragment_Lluvia();
        newFrag.show(getSupportFragmentManager(), "datePicker");
        newFrag.SetearFechas("2017-09-24");
    }
    public void showSelectedItems(View view){
        String items = "";
        for(String item: pivots){
            items+="-"+item+"\n";
        }
        Toast.makeText(this,"Seleccionados \n"+items,Toast.LENGTH_LONG).show();
    }
    public class ClaseAsincrona extends AsyncTask<String,Void,String> {
        String res;
        String token;

        @Override
        protected String doInBackground(String... params) {

            try {
                token = params[0];
                farmId = params[1];
                URL url = new URL("http://iradvisor.pgwwater.com.uy:9080/api/IrrigationData/token/"+token +"/farmId/" + farmId);
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
            if (result!=null){
                try {
                    JSONObject json = new JSONObject(result);
                    sp = getSharedPreferences("sesion",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    JSONObject jsonData = json.optJSONObject("Data");
                    JSONArray farm_pivots = jsonData.getJSONArray("IrrigationRows");
                    for(int i=0;i<=farm_pivots.length()-1;i++){
                        JSONObject pv = farm_pivots.getJSONObject(i);
                        Pivot p = new Pivot(pv.get("Name").toString(), pv.get("Crop").toString(), pv.get("HarvestDate").toString(), pv.get("Phenology").toString());
                        JSONArray pv_riegos = pv.getJSONArray("Advices");
                        for(int r=0;r<=pv_riegos.length()-1;r++){
                            JSONObject riego = pv_riegos.getJSONObject(r);
                            Date f_riego = CrearFecha(riego.get("Date").toString());
                            Riego rg = new Riego(riego.get("IrrigationType").toString(), f_riego,Float.parseFloat(riego.get("Quantity").toString()));
                            p.getRiegos().add(rg);
                        }
                        estab_pivots.add(p);
                    }
                    Establecimiento est = new Establecimiento(Integer.parseInt(farmId),farmdesc);
                    est.setPivots(estab_pivots);
                    farmslist.clear();
                    farmslist.add(est);
                    String jsonObjetos = new Gson().toJson(farmslist);
                    editor.putString("actual_farm", jsonObjetos);
                    editor.putBoolean("hay_farm", true);
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Fragment fragment= new FragmentPivot();
                FragmentManager fragmentManager =MainActivity.this.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frameprincipal, fragment).commit();

            }
            else{
                Toast.makeText(MainActivity.this, "Pivots para el establecimiento no traidos correctamente",
                        Toast.LENGTH_LONG).show();
            }

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

    public void start() {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 60000, pendingIntent);
        //manager.setTime(74340000);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }
    public void startAt20() {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        /* Set the alarm to start at 20:00 hs */
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        /* Repeating on every one day interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
