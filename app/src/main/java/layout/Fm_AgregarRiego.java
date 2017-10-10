package layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import Adapters.AdapterPivots;
import com.example.olave.inriego.DatePickerFragment_Riego;
import com.example.olave.inriego.MainActivity;
import com.example.olave.inriego.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import Clases.Establecimiento;
import Clases.Pivot;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fm_AgregarRiego.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fm_AgregarRiego#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fm_AgregarRiego extends Fragment {
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
    ListView lv;


    private OnFragmentInteractionListener mListener;

    public Fm_AgregarRiego() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fm_AgregarRiego.
     */
    // TODO: Rename and change types and number of parameters
    public static Fm_AgregarRiego newInstance(String param1, String param2) {
        Fm_AgregarRiego fragment = new Fm_AgregarRiego();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ma = (MainActivity) getActivity();
        ma.pivots.clear();
        View rootview = inflater.inflate(R.layout.fragment_agregar_riego, container, false);

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

        lv = (ListView) rootview.findViewById(R.id.lst_riego);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        ArrayList<Pivot> arrpv = farmslist.get(0).getPivots();

        /*Pivot pv1 = new Pivot(); pv1.setNombre("P1");
        Pivot pv2 = new Pivot(); pv2.setNombre("P2");
        Pivot pv3 = new Pivot(); pv3.setNombre("P3");
        Pivot pv4 = new Pivot(); pv4.setNombre("P4");
        Pivot pv5 = new Pivot(); pv5.setNombre("P5");
        arrpv.add(pv1); arrpv.add(pv2); arrpv.add(pv3); arrpv.add(pv4); arrpv.add(pv5);*/
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

        // Inflate the layout for this fragment
        return  rootview;
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
    public void showDatePickerDialog_Riego(View v) {
        DatePickerFragment_Riego newFrag = new DatePickerFragment_Riego();
        getActivity().getBaseContext();
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

    public class ClaseAsincrona extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {


            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 200) {
                getFragmentManager().popBackStack();
                Toast.makeText(Fm_AgregarRiego.this.getActivity(), "Agregado",
                        Toast.LENGTH_LONG).show();

            } else
                Toast.makeText(Fm_AgregarRiego.this.getActivity(), "Error",
                        Toast.LENGTH_LONG).show();
        }
    }

}
