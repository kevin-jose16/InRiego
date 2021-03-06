package layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olave.inriego.FragmentPivot;
import com.example.olave.inriego.MainActivity;
import com.example.olave.inriego.R;
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

import Adapters.AdapterEstablecimientos;
import Clases.Establecimiento;
import Clases.Pivot;
import Clases.Riego;
import Persistencia.Json_SQLiteHelper;
import Persistencia.SQLiteHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fm_Establecimiento.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fm_Establecimiento#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fm_Establecimiento extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static Adapters.AdapterPivots ap;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<Pivot> estab_pivots = new ArrayList<>();
    ArrayList<Establecimiento> farmslist= new ArrayList<>();
    SharedPreferences sp;
    boolean tiene_pivots=false, error_servidor=false;
    String farmId, farmdesc;

    private OnFragmentInteractionListener mListener;

    public Fm_Establecimiento() {
        // Required empty public constructor
    }
    ProgressDialog progress;  //para mostrar barrita de progreso mientras demora el servidor
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fm_Establecimiento.
     */
    // TODO: Rename and change types and number of parameters
    public static Fm_Establecimiento newInstance(String param1, String param2) {
        Fm_Establecimiento fragment = new Fm_Establecimiento();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_establecimiento, container, false);

        MainActivity ma = (MainActivity) getActivity();
        ma.setItemVisible(1,false);
        ma.setItemVisible(2,false);
        ma.setItemVisible(3,false);
        ma.setItemVisible(4,false);

        //TextView tv = (TextView) getActivity().findViewById(R.id.nav_farm);
        //tv.setText("InRiego");
        getActivity().setTitle("InRiego");
        sp = getActivity().getSharedPreferences("sesion",Context.MODE_PRIVATE);
        Gson gson = new Gson(); //Instancia Gson.
        String objetos = sp.getString("farmslist", null); //Obtiene datos (json)
        String user = sp.getString("username",null);
        //String json = new Gson().toJson(objetos);
        //Convierte json  a JsonArray.
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(objetos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Convierte JSONArray a Lista de Objetos!
        Type listType = new TypeToken<ArrayList<Establecimiento>>(){}.getType();
        farmslist = new Gson().fromJson(jsonArray.toString(), listType);


        ListView lv = (ListView) rootview.findViewById(R.id.lst_establecimientos);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        user = user.substring(0, 1).toUpperCase() + user.substring(1);
        for(int i=0; i<farmslist.size(); i++){
            String completo="";
            if(farmslist.get(i).getDescripcion().contains(user)) {
                completo = farmslist.get(i).getDescripcion();
                String spl[] = completo.split(user);
                farmslist.get(i).setDescripcion(spl[1]);
            }
        }
        AdapterEstablecimientos adpfarms = new AdapterEstablecimientos(getActivity(), farmslist);
        lv.setAdapter(adpfarms);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String selItem = ((TextView)view).getText().toString();
                view.setSelected(true);
                TextView tv = (TextView) getActivity().findViewById(R.id.nav_farm);
                getActivity().setTitle(selItem);
                tv.setText(selItem);
                int estId=0;
                farmdesc=selItem;
                for(int f=0; f<farmslist.size(); f++){
                    if(farmslist.get(i).getDescripcion().equals(selItem))
                        estId= farmslist.get(i).getEst_id();
                }
                new ClaseAsincrona().execute(sp.getString("token",null),String.valueOf(estId));

            }
        });
        return rootview;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        Fragment fragment;
        fragment = new Fm_AgregarRiego();
        if (fragment != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frameprincipal, fragment).commit();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class ClaseAsincrona extends AsyncTask<String,Integer,String> {
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
            Json_SQLiteHelper json_sq = new Json_SQLiteHelper(getActivity(), "DBJsons", null, 1);
            SQLiteDatabase dta_base = json_sq.getReadableDatabase();
            SQLiteHelper abd = new SQLiteHelper(dta_base, json_sq);
            if (result!=null){
                try {
                    JSONObject json = new JSONObject(result);
                    if(json.getBoolean("IsOk")){
                        sp = getActivity().getSharedPreferences("sesion",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        JSONObject jsonData = json.optJSONObject("Data");
                        String ref_date = jsonData.getString("ReferenceDate");

                        //Setear fecha en clase principal y sesion
                        MainActivity mn = (MainActivity) getActivity();
                        mn.reference_date = ref_date;
                        editor.putString("ReferenceDate",ref_date);

                        JSONObject jsobject = jsonData.getJSONObject("Farm");
                        farmdesc = jsobject.getString("Description");

                        JSONArray farm_pivots = jsonData.getJSONArray("IrrigationRows");
                        if(farm_pivots.length()>0) {
                            tiene_pivots=true;
                            for (int i = 0; i <= farm_pivots.length() - 1; i++) {
                                JSONObject pv = farm_pivots.getJSONObject(i);
                                Pivot p = new Pivot(Integer.parseInt(pv.get("IrrigationUnitId").toString()), pv.get("Name").toString(), pv.get("Crop").toString(), pv.get("SowingDate").toString(), pv.get("Phenology").toString());
                                JSONArray pv_riegos = pv.getJSONArray("Advices");
                                for (int r = 0; r <= pv_riegos.length() - 1; r++) {
                                    JSONObject riego = pv_riegos.getJSONObject(r);
                                    String f_riego = riego.get("Date").toString();
                                    Riego rg = new Riego(riego.get("IrrigationType").toString(), f_riego, Float.parseFloat(riego.get("Quantity").toString()));
                                    p.getRiegos().add(rg);
                                }
                                estab_pivots.add(p);

                            }
                            Establecimiento est = new Establecimiento(Integer.parseInt(farmId), farmdesc, ref_date);
                            est.setPivots(estab_pivots);
                            //ArrayList<Establecimiento> es = new ArrayList<>();
                            //es.add(est);
                            String jsonObjetos = new Gson().toJson(est);
                            editor.putString("actual_farm", jsonObjetos);
                            editor.putBoolean("hay_farm", true);
                            editor.putBoolean("sincronizando", false);
                            mn.setToken(sp.getString("token", ""));
                            mn.setActual_farm(Integer.parseInt(farmId));
                            editor.commit();
                            Calendar cal = Calendar.getInstance();
                            abd.insertLog(cal.getTime().toString() + " Se selecciona el establecimiento " + farmdesc + " con respuesta correcta del servidor", sp.getString("username", ""), json_sq);
                            dta_base.close();
                        }
                    }
                    else{
                        error_servidor = true;
                        Calendar cal = Calendar.getInstance();
                        abd.insertLog(cal.getTime().toString() + " ERROR -- Se intento seleccionar el establecimiento " + farmdesc + " con respuesta no exitosa del servidor", sp.getString("username",""),json_sq);
                        dta_base.close();
                        Toast.makeText(getActivity(), "No se pudo seleccionar establecimiento\nError en el Servidor",
                                Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(tiene_pivots) {
                    Fragment fragment = new FragmentPivot();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frameprincipal, fragment).commit();
                    progress.setProgress(100); //progreso culminado
                }
                else{
                    if(!error_servidor) {
                        progress.setProgress(0);
                        Calendar cal = Calendar.getInstance();
                        abd.insertLog(cal.getTime().toString() + " ERROR -- Se intento seleccionar el establecimiento " + farmdesc + " pero éste no tiene pivots", sp.getString("username",""),json_sq);
                        dta_base.close();
                        //mostrarMsg("Seleccione otro", "Establecimiento sin pivots");
                        Toast.makeText(getActivity(), "Establecimiento sin pivots",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                        progress.setProgress(0);
                }

            }
            else{
                progress.setProgress(0);
                Calendar cal = Calendar.getInstance();
                abd.insertLog(cal.getTime().toString() + " ERROR -- No se pudo seleccionar un establecimiento por problemas en el servidor o la conexión a internet", sp.getString("username",""), json_sq);
                dta_base.close();
                Toast.makeText(getActivity(), "No tiene conexión a internet\no hay problemas con el servidor",
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
            progress=new ProgressDialog(getActivity());
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

    public void mostrarMsg(String msg, String titulo){
        TextView myView = new TextView(this.getActivity());
        myView.setText(msg);
        myView.setTextSize(10);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setMessage(msg).setTitle(titulo);
        builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();
    }
}
