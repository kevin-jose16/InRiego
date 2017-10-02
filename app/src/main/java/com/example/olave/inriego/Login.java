package com.example.olave.inriego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import Clases.Establecimiento;
import layout.Fm_Establecimiento;
import layout.Fm_agregarLluvia;

public class Login extends AppCompatActivity {

    EditText pass;
    EditText user;
    Button boton;

    ArrayList<Establecimiento> farms = new ArrayList<Establecimiento>();


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
    }



    public class ClaseAsincrona extends AsyncTask<String,Void,String> {
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
            if (result!=null){
                try {
                    JSONObject json = new JSONObject(result);
                    SharedPreferences sp = Login.this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username",username);
                    editor.putString("password",password);

                    JSONObject jsonData = json.optJSONObject("Data");
                    editor.putString("token",jsonData.get("Token").toString());
                    JSONArray estab = jsonData.getJSONArray("Farms");
                    for(int i=0;i<=estab.length()-1;i++){
                        JSONObject es = estab.getJSONObject(i);
                        Establecimiento e = new Establecimiento(Integer.parseInt(es.get("FarmId").toString()),es.get("Description").toString());
                        farms.add(e);
                    }
                    String jsonObjetos = new Gson().toJson(farms);
                    editor.putString("farmslist", jsonObjetos);
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //iniciar 2da activity despues del login
                Intent i = new Intent(Login.this,MainActivity.class);
                startActivity(i);

            }
            else{
                Toast.makeText(Login.this, "Tu nombre de usuario o la contraseña son incorrectos",
                        Toast.LENGTH_LONG).show();
            }

        }

    }

}
