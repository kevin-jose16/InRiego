package layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Clases.Establecimiento;
import Clases.Pivot;
import Clases.Riego;
import Persistencia.Json_SQLiteHelper;
import Persistencia.SQLiteHelper;

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
    int farmid= 0;
    String farmdesc= "";
    ListView lv;
    EditText cant_ed;
    Button bt_fecha;
    Button bt_aceptar;
    Button bt_cancelar;
    String token;
    String reference_date;
    AdapterPivots adppivots;


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

        //Obtengo json en formato string de sesion
        String objetos = sp.getString("actual_farm", null);

        JSONObject jsb = null;
        //Convierto a jsonobject lo que obtuve en string
        try {
            jsb = new JSONObject(objetos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type listType = new TypeToken<Establecimiento>(){}.getType();
        Establecimiento farm = new Gson().fromJson(jsb.toString(), listType);
        farmid = farm.getEst_id();
        farmdesc = farm.getDescripcion();

        bt_aceptar = (Button) rootview.findViewById(R.id.btn_agregar_l);
        bt_fecha = (Button) rootview.findViewById(R.id.btn_fecha_lluvia);
        cant_ed = (EditText) rootview.findViewById(R.id.cantidad_lluvia);
        lv = (ListView) rootview.findViewById(R.id.lst_lluvia);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        final ArrayList<Pivot> arrpv = farm.getPivots();
        reference_date = farm.getRef_date();

        adppivots = new AdapterPivots(getActivity(), arrpv);
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


        bt_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (bt_fecha.getText() == "" || cant_ed.getText().toString() == "" || ma.pivots.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Cantidad o fecha no ingresada\no no hay pivots seleccionados")
                        .setTitle("Faltan completar campos");
                builder.setPositiveButton("OK", null);

                builder.create();
                builder.show();
            } else {
                if (cant_ed.getText().toString().length() >5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("La cantidad(mm) debe ser de un maximo de 3 digitos")
                            .setTitle("Cantidad(mm) Incorrecta");
                    builder.setPositiveButton("OK", null);

                    builder.create();
                    builder.show();
                } else{
                    if(Integer.parseInt(cant_ed.getText().toString()) == 0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("La cantidad(mm) debe ser mayor que 0")
                                .setTitle("Cantidad(mm) Incorrecta");
                        builder.setPositiveButton("OK", null);

                        builder.create();
                        builder.show();
                    }
                    else {
                        String[] fechas = bt_fecha.getText().toString().split("/");
                        int year = Integer.parseInt(fechas[2]);
                        int month;
                        String month_f, day_f;
                        int day;
                        if (fechas[1].length() == 1) {
                            month_f = "0" + fechas[1];
                            month = Integer.parseInt(month_f);
                        } else
                            month = Integer.parseInt(fechas[1]);

                        if (fechas[0].length() == 1) {
                            day_f = "0" + fechas[0];
                            day = Integer.parseInt(day_f);
                        } else
                            day = Integer.parseInt(fechas[0]);

                        String fecha = year + "-" + month + "-" + day;

                        ArrayList<Integer> pivotsIds = new ArrayList();
                        for (int i = 0; i < ma.pivots.size(); i++) {
                            String[] ids = ma.pivots.get(i).split(" ");
                            int pivotid = Integer.parseInt(ids[0]);
                            for(int a = 0; a < arrpv.size(); a++){
                                if(arrpv.get(a).getId() == pivotid){
                                    boolean notpresent = true;
                                    for(int rg = 0; rg < arrpv.get(a).getRiegos().size(); rg ++){
                                        if(comparaFechas(CrearFecha(arrpv.get(a).getRiegos().get(rg).getFecha()), CrearFecha(fecha)) ==0){
                                            //Log.d("PV-Ingresado", "pivot " + pivotid);
                                            notpresent=false;
                                            arrpv.get(a).getRiegos().get(rg).setMilimetros(Integer.parseInt(cant_ed.getText().toString()));
                                            arrpv.get(a).getRiegos().get(rg).setTipo("Rain");
                                        }
                                        /*else{
                                            Log.d("Fecha", "fecha:  " + arrpv.get(a).getRiegos().get(rg).getFecha());
                                        }*/
                                    }
                                    if(notpresent){

                                        Riego newriego = new Riego();
                                        newriego.setTipo("Rain");
                                        newriego.setFecha(fecha);
                                        newriego.setMilimetros(Integer.parseInt(cant_ed.getText().toString()));
                                        arrpv.get(a).getRiegos().add(newriego);
                                        Establecimiento newfarm = new Establecimiento(farmid, farmdesc, reference_date);
                                        newfarm.setPivots(arrpv);
                                        SharedPreferences.Editor editor = sp.edit();
                                        String jsonObjetos = new Gson().toJson(newfarm);
                                        editor.putString("actual_farm", jsonObjetos);
                                        editor.commit();
                                    }
                                    else{
                                        Establecimiento newfarm = new Establecimiento(farmid, farmdesc, reference_date);
                                        newfarm.setPivots(arrpv);
                                        SharedPreferences.Editor editor = sp.edit();
                                        String jsonObjetos = new Gson().toJson(newfarm);
                                        editor.putString("actual_farm", jsonObjetos);
                                        editor.commit();
                                    }

                                }
                            }

                            pivotsIds.add(pivotid);
                        }
                        JSONObject irrigation = new JSONObject();
                        Json_SQLiteHelper json_sq = new Json_SQLiteHelper(getActivity(), "DBJsons", null, 1);
                        SQLiteDatabase db = json_sq.getReadableDatabase();
                        SQLiteHelper abd = new SQLiteHelper(db, json_sq);
                        String us = sp.getString("username", null);
                        TextView text_farm = (TextView) getActivity().findViewById(R.id.nav_farm);


                        for (int i = 0; i < pivotsIds.size(); i++) {
                            int pivotid = pivotsIds.get(i);
                            json_sq = new Json_SQLiteHelper(getActivity(), "DBJsons", null, 1);
                            db = json_sq.getReadableDatabase();
                            try {
                                //irrigation.put("Token", token);
                                irrigation = new JSONObject();
                                irrigation.put("IrrigationUnitId", pivotid);
                                irrigation.put("Milimeters", Float.parseFloat(cant_ed.getText().toString()));
                                irrigation.put("Date", fecha);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            abd = new SQLiteHelper(db, json_sq, irrigation.toString(), us, text_farm.getText().toString(), "Irrigation");
                        }

                        db.close();

                        mostrarMsg("Se ha ingresado el registro del lluvia", "Lluvia");
                        cant_ed.setText("");
                        bt_fecha.setText("");
                        lv.setAdapter(adppivots);
                        Fragment fragment = new FragmentPivot();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameprincipal, fragment).commit();

                    }
                }
            }
            }
        });

        bt_cancelar = (Button) rootview.findViewById(R.id.btn_cancelar_l);

        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment= new FragmentPivot();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frameprincipal, fragment).commit();
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
        newFrag.SetearFechas(reference_date);
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
    public Calendar CrearFecha(String fecha) {
        Calendar cal = Calendar.getInstance();
        int year=0,month=0 ,day=0;
        Date fecha_r = new Date();
        year = Integer.parseInt("" + fecha.charAt(0) + fecha.charAt(1) + fecha.charAt(2) + fecha.charAt(3));

        if ("0".equals(fecha.charAt(5)))
            month = Integer.parseInt("" + fecha.charAt(6));

        else{
            if ('-'==fecha.charAt(6))
                month = Integer.parseInt("" + fecha.charAt(5));
            else
            if ('-'!=fecha.charAt(6))
                month = Integer.parseInt("" + fecha.charAt(5) + fecha.charAt(6));
        }



        if ('-'==fecha.charAt(6)){
            if ("0".equals(fecha.charAt(7)))
                day = Integer.parseInt("" + fecha.charAt(8));
            else
                day = Integer.parseInt("" + fecha.charAt(7));
        }
        else {
            if ('-'==fecha.charAt(7)) {
                if ("0".equals(fecha.charAt(8)))
                    day = Integer.parseInt("" + fecha.charAt(9));
                else
                    day = Integer.parseInt("" + fecha.charAt(8) + fecha.charAt(9));
            }
        }

        month=month-1;
        cal.set(year,month,day);
        //int anio= cal.get(Calendar.YEAR);


        return cal;
    }

    public int comparaFechas(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null)
            return -4;//alguna fecha es nula
        else {
            //si son iguales
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE))
                return 0;

            //cal1 menor que cal2

            if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE))
                return -1;// año menor
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) < cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE))
                return -2;//mes menor
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DATE) < cal2.get(Calendar.DATE))
                return -3;//dia menor

            //cal1 mayor que cal2

            if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE))
                return 1;// año mayor
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) > cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE))
                return 2;//mes mayor
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DATE) > cal2.get(Calendar.DATE))
                return 3;//dia mayor

        }


        return 4;

    }

}
