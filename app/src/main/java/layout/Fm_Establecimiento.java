package layout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.solver.SolverVariable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import java.util.List;

import Adapters.AdapterEstablecimientos;
import Clases.Establecimiento;
import Clases.Pivot;
import Clases.Riego;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<Pivot> estab_pivots = new ArrayList<>();
    ArrayList<Establecimiento> farmslist= new ArrayList<>();
    SharedPreferences sp;
    String farmId, farmdesc;

    private OnFragmentInteractionListener mListener;

    public Fm_Establecimiento() {
        // Required empty public constructor
    }

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
        TextView tv = (TextView) getActivity().findViewById(R.id.nav_farm);
        tv.setText("InRiego");
        getActivity().setTitle("InRiego");
        sp = getActivity().getSharedPreferences("sesion",Context.MODE_PRIVATE);
        Gson gson = new Gson(); //Instancia Gson.
        String objetos = sp.getString("farmslist", null); //Obtiene datos (json)
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
                    sp = getActivity().getSharedPreferences("sesion",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    JSONObject jsonData = json.optJSONObject("Data");
                    JSONArray farm_pivots = jsonData.getJSONArray("IrrigationRows");
                    for(int i=0;i<=farm_pivots.length()-1;i++){
                        JSONObject pv = farm_pivots.getJSONObject(i);
                        //Integer.parseInt(pv.get("IrrigationId").toString()),
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
                    ArrayList<Establecimiento> es = new ArrayList<>();
                    es.add(est);
                    String jsonObjetos = new Gson().toJson(es);
                    editor.putString("actual_farm", jsonObjetos);
                    editor.putBoolean("hay_farm",true);
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Fragment fragment= new FragmentPivot();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frameprincipal, fragment).commit();

            }
            else{
                Toast.makeText(getActivity(), "Pivots para el establecimiento no traidos correctamente",
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
}
