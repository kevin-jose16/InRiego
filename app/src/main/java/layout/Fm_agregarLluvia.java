package layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import Adapters.AdapterPivots;
import com.example.olave.inriego.DatePickerFragment_Lluvia;
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
import java.util.Date;

import Clases.Establecimiento;
import Clases.Pivot;
import Clases.Riego;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fm_agregarLluvia.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fm_agregarLluvia#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fm_agregarLluvia extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MainActivity ma = null;
    SharedPreferences sp;
    static final int DATE_DIALOG_ID = 0;
    TextView tvdate;
    ListView lv;
    String token;


    private OnFragmentInteractionListener mListener;

    public Fm_agregarLluvia() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fm_agregarLluvia.
     */
    // TODO: Rename and change types and number of parameters
    public static Fm_agregarLluvia newInstance(String param1, String param2) {
        Fm_agregarLluvia fragment = new Fm_agregarLluvia();
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
        ma = (MainActivity) getActivity();
        ma.pivots.clear();
        View rootview = inflater.inflate(R.layout.fragment_agregar_lluvia, container, false);

        sp = getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
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
        //Convierte JSONArray a Lista de Objetos!
        Type listType = new TypeToken<ArrayList<Establecimiento>>(){}.getType();
        ArrayList <Establecimiento> farmslist = new Gson().fromJson(jsonArray.toString(), listType);

        Button bt_fecha = (Button) getActivity().findViewById(R.id.btn_fecha_lluvia);
        EditText cant_ed = (EditText) getActivity().findViewById(R.id.cantidad_lluvia);
        lv = (ListView) rootview.findViewById(R.id.lst_lluvia);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        ArrayList<Pivot> arrpv = farmslist.get(0).getPivots();

        AdapterPivots adppivots = new AdapterPivots(getActivity(), arrpv);
        lv.setAdapter(adppivots);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selItem = ((CheckedTextView)view).getText().toString();
                view.setSelected(true);
                if(ma.pivots.contains(selItem))
                    ma.pivots.remove(selItem);
                else
                    ma.pivots.add(selItem);
            }
        });
        token = sp.getString("token",null);
        for(int i = 0; i<ma.pivots.size(); i++){
            String pivotid = ma.pivots.get(i).substring(6);
            new ClaseAsincrona().execute(token,pivotid, cant_ed.getText().toString(),bt_fecha.getText().toString());
        }

        // Inflate the layout for this fragment
        return  rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public void showDatePickerDialog_Lluvia(View v) {
        DatePickerFragment_Lluvia newFrag = new DatePickerFragment_Lluvia();
        this.getActivity().getBaseContext();
        newFrag.show(getActivity().getSupportFragmentManager(), "datePicker");
        newFrag.SetearFechas("2017-09-24");
    }
    public void showSelectedItems(View view){
        String items = "";
        for(String item: ma.pivots){
            items+="-"+item+"\n";
        }
        Toast.makeText(getActivity(),"Seleccionados\n"+items,Toast.LENGTH_SHORT).show();
    }
    public class ClaseAsincrona extends AsyncTask<String, Void, String> {

        String res;


        @Override
        protected String doInBackground(String... params) {

            try {

                URL url = new URL("http://iradvisor.pgwwater.com.uy:9080/api/IrrigationData/AddIrrigation");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
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
                    JSONObject jsonData = json.optJSONObject("Data");

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
}
