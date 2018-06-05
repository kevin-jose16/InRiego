package com.example.olave.inriego;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olave.inriego.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import Clases.Establecimiento;
import Clases.Pivot;
import Clases.Riego;
import layout.Fm_Establecimiento;

import static android.content.ContentValues.TAG;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentPivot extends Fragment {


    JSONArray pivots_est = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    //funciones


    public int year, year1, year7;

    //los meses van del 1 al 12
    public int month, month1,month7;

    public int year2,year4,year5,year6;
    public int month2,month4,month5,month6;
    public int day2,day4,day5,day6;

    //los dias van del 1 al 31
    public static int day, day1,day7;


    public void SetearSieteFechas(String fecha) {
        year=Integer.parseInt(""+fecha.charAt(0)+fecha.charAt(1)+fecha.charAt(2)+fecha.charAt(3));//Sacar año

        if("0".equals(fecha.charAt(5)))//Mes cuando es de 1 digito ej. mes 1
            month=Integer.parseInt(""+ fecha.charAt(6));
        else//Mes de dos digitas ej. mes 10
            month=Integer.parseInt(""+fecha.charAt(5)+fecha.charAt(6));

        if("0".equals(fecha.charAt(8)))//dia de un digito ej. dia 2
            day = Integer.parseInt(""+fecha.charAt(9));
        else//dia de dos digitos ej. dia 20
            day = Integer.parseInt(""+fecha.charAt(8)+fecha.charAt(9));


        boolean bisiesto = false;
        if (year % 4 == 0 & year % 100 != 0 || year % 400 == 0)//Calcular si el año es bisiesto
            bisiesto = true;

        //Meses de 31 dias
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            if (month == 1) {//Enero
                if (day == 1 || day == 2) {// Dia 1 y 2
                    year1 = year - 1;
                    month1= month2= 12;
                    month7 = month4 = month5 = month6 =  1;
                    year7 = year4 = year5 = year6 = year;
                    day7 = day + 4; day4 = day + 1; day5 = day + 2; day6 = day + 3;
                    if (day == 1){
                        day1 = 30; day2= 31;
                        year2=year-1;
                    }
                    else{
                        month2=month;
                        year2=year;
                        day1 = 31; day2= 1;
                    }
                } else if (day == 28 || day == 29 || day == 30 || day == 31) {// dia 28,29,30 y 31
                    year1 = year2 = year4 = year5 = year6 = year;
                    month7 = 2;
                    day1 = day - 2; day2 = day-1;
                    month1 = month2 = month;
                    year7 = year;
                    if (day == 28){
                        day7 = 1;
                        day4= day+1; day5 = day+2; day6 = day+3;
                        month4 = month5 = month6 = month;
                    }
                    if (day == 29){
                        day7 = 2;
                        day4= day+1; day5 = day+2; day6= 1;
                        month4 = month5 = month;
                        month6=2;
                    }
                    if (day == 30){
                        day7 = 3;
                        day4= day+1; day5 = 1; day6= 2;
                        month4 = month;
                        month5 = month6 = 2;
                    }
                    if (day == 31){
                        day7 = 4;
                        day4= 1; day5 = 2; day6= 3;
                        month4 = month5 = month6 = 2;
                    }
                } else {//resto de los dias del mes
                    day1 = day - 2; day2 = day-1;
                    day7 = day + 4; day4 = day+1; day5 = day+2; day6 = day+3;
                    year1 = year2 = year4 = year5 = year6 = year;
                    year7 = year;
                    month1=month;
                    month7 = month2 = month4 = month5 = month6 = month;
                    month7 = month2 = month4 = month5 = month6 = month;
                }
            }
            if (month == 3) {//Marzo
                year7 = year2 = year4 = year5 = year6 = year;
                year1 = year;
                if (day == 28 || day == 29 || day == 30 || day == 31) {//dia 28,29,30 y 31
                    day1 = day - 2;
                    day2 = day-1;
                    month1 = month2 = month;
                    month7 = month + 1;
                    if (day == 28){
                        day7 = 1;
                        day4= day+1; day5 = day+2; day6 = day+3;
                        month4 = month5 = month6 = month;
                    }
                    if (day == 29){
                        day7 = 2;
                        day4= day+1; day5 = day+2; day6= 1;
                        month4 = month5 = month;
                        month6=4;
                    }
                    if (day == 30){
                        day7 = 3;
                        day4= day+1; day5 = 1; day6= 2;
                        month4 = month;
                        month5 = month6 = 4;
                    }
                    if (day == 31){
                        day7 = 4;
                        day4= 1; day5 = 2; day6= 3;
                        month4 = month5 = month6 = 4;
                    }
                } else if (day == 1 || day == 2) {//dia 1 y 2
                    day7 = day + 4; day4 = day+1; day5=day+2; day6=day+3;
                    if (bisiesto)// febrero con año bisiesto
                        if (day == 1){
                            day1 = 28;
                            day2=29;
                            month2=2;
                        }
                        else if (day == 2){
                            day1 = 29;
                            day2=1;
                            month2=3;
                        }
                    if (!bisiesto)// febrero sin año bisiesto
                        if (day == 1){
                            day1 = 27;
                            day2=28;
                            month2=2;
                        }else if (day == 2){
                            day1 = 28;
                            day2=1;
                            month2=3;
                        }
                    month1 = month - 1;
                    month7 = month4= month5= month6= month;
                } else {//resto de los dias del mes
                    month1 = month; month2=month;
                    month7 = month4= month5= month6= month;
                    day1 = day - 2;
                    day2 = day-1;
                    day4= day + 1; day5 = day + 2; day6= day + 3;
                    day7 = day + 4;
                }
            }

            if (month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {//mayo,julio,agosto,octubre,diciembre
                year1= year2= year4= year5= year6= year;
                month1 = month;
                year7=year;

                if (day == 1){//dia 1
                    month1 = month2 = month-1;
                    month7= month4= month5= month6= month;
                    day1 = 29;
                    day2=30;
                    day4= day+1;
                    day5=day+2;
                    day6=day+3;
                    day7=day+4;
                }

                if (day == 2){// dia 2
                    month1 =month-1; month2= month;
                    month7= month4= month5= month6= month;
                    day1 = 30;
                    day2=1;
                    day4=day+1; day5=day+2; day6=day+3;
                    day7=day+4;
                }


                if(day>=3 && day<=31){// dias 3 a 31

                    month7=month2=month;
                    day1 =day-2; day2=day-1;
                    day7 = day+4; day4=day+1; day5=day+2; day6=day+3;
                    month4=month5=month6=month;
                    if(day>=28 && day<=31) {// dias 28 a 31
                        month7=month+1;
                        if (day == 28){
                            month4= month5 = month6= month;
                            day7 = 1;
                            day4=day+1; day5=day+2; day6=day+3;
                        }
                        if (day == 29){
                            day4=day+1; day5=day+2;
                            month4= month5 = month;
                            month6=month+1;
                            day6=1;
                            day7 = 2;
                        }
                        if (day == 30){
                            month4= month;
                            day4=day+1;
                            day5=1;
                            day6=2;
                            month5= month6= month+1;
                            day7 = 3;
                        }
                        if (day == 31){
                            month4=month5=month6=month+1;
                            day4=1; day5=2; day6=3;
                            day7 = 4;
                        }
                    }
                }
                if (month == 12) {//diciembre
                    if(day>=28 && day <=31){//dias 28 a 31
                        year7 = year+1;
                        month7 = 1;

                        if (day == 29){
                            month6=1;
                            year6=year+1;
                        }
                        if (day == 30){
                            month5= month6= 1;
                            year5=year6=year+1;
                        }
                        if (day == 31){
                            month4=month5=month6=1;
                            year4=year5=year6=year+1;
                        }
                    }
                    else{//resto de los dias
                        year7 = year;
                        month7 = month;

                    }

                }
                if(month==8){//agosto
                    if (day == 1){//dia 1
                        day1 = 30;
                        day2=31;
                    }
                    else
                    if (day == 2){// dia 2
                        day2=1;
                        day1 = 31;
                    }
                }
            }

        } else {
            year7 = year;
            year1 = year;
            month2=month;
            day2=day-1;
            year2=year4=year5=year6= year;
            if (month == 2) {//Febrero
                month1 = 2;
                if (bisiesto) {//Febrero con año Bisiesto
                    if (day == 26 || day == 27 || day == 28 || day == 29) {//dias 26 a 29
                        day1 = day - 2;
                        month7 = 3;

                        if (day == 26){
                            day7 = 1;
                            day4=day+1;day5=day+2;day6=day+3;
                            month4=month5=month6=month;
                        }if (day == 27){
                            day4=day+1;day5=day+2;
                            day6=1;
                            month4=month5=month;
                            month6=month+1;
                            day7 = 2;
                        }if (day == 28){
                            day4=day+1;
                            day5=1; day6=2;
                            month4=month;
                            month5=month6=month+1;
                            day7 = 3;
                        }if (day == 29){
                            day4=1;day5=2;day6=3;
                            month4=month5=month6=month+1;
                            day7 = 4;
                        }
                    } else if (day >= 3 && day <= 25) {// dias 3 a 25
                        month4=month5=month6=month;
                        day4=day+1;	day5=day+2;	day6=day+3;
                        day1 = day - 2;
                        month7 = 2;
                        day7 = day + 4;
                    }

                }
                if (!bisiesto) {//febrero sin año bisiesto
                    if (day == 25 || day == 26 || day == 27 || day == 28) {//dias 25 a 28
                        day1 = day - 2;
                        month7 = 3;
                        if (day == 25){
                            day7 = 1;
                            day4=day+1;day5=day+2;day6=day+3;
                            month4=month5=month6=month;
                        }if (day == 26){
                            day4=day+1;day5=day+2;
                            day6=1;
                            month4=month5=month;
                            month6=month+1;
                            day7 = 2;
                        }if (day == 27){
                            day7 = 3;
                            day4=day+1;
                            day5=1; day6=2;
                            month4=month;
                            month5=month6=month+1;
                        }if (day == 28){
                            day7 = 4;
                            day4=1;day5=2;day6=3;
                            month4=month5=month6=month+1;
                        }
                    } else if (day >= 3 && day <= 24) {// dias 3 a 24
                        month1=2;
                        day1 = day - 2;
                        month7 = 2;
                        day7 = day + 4;
                        month4=month5=month6=month;
                        day4=day+1;	day5=day+2;	day6=day+3;
                    }
                }
            } else {//meses abril,junio,setiembre y noviembre
                month1 = month;
                day1 = day - 2;
                if (day == 27 || day == 28 || day == 29 || day == 30) {//dias 27 a 30
                    month7 = month + 1;
                    day4=day+1;
                    month4=month;
                    if (day == 27){
                        day5=day+2; day6=day+3;
                        month5=month6=month;
                        day7 = 1;
                    }if (day == 28){
                        month5=month; month6=month+1;
                        day5=day+2; day6=1;
                        day7 = 2;
                    }if (day == 29){
                        month5=month+1;
                        month6=month+1;
                        day5=1;
                        day6=2;
                        day7 = 3;
                    }if (day == 30){
                        month4=month5=month6=month+1;
                        day4=1; day5=2; day6=3;
                        day7 = 4;
                    }
                }
                if (day >= 3 && day <= 26){//dias 3 a 26
                    month4=month5=month6=month;
                    day4=day+1;	day5=day+2;	day6=day+3;
                    month7 = month;
                    day7 = day + 4;
                }

            }

            if (day == 1){//dia 1
                month7=month;
                day2=31;
                month2=month-1;
                day4=day+1;
                day5=day+2;
                day6=day+3;
                month4=month5=month6=month;
                month1=month-1;
                day7 = day + 4;
                day1 = 30;
            }

            else if (day == 2){//dia 2
                day2=1;
                month2=month;
                day4=day+1;
                day5=day+2;
                day6=day+3;
                month4=month5=month6=month;
                month7=month;
                month1=month-1;
                day7 = day + 4;
                day1 = 31;
            }

        }
        /*int di1, di2,di3,di4,di5,di6,di7;
        int y1,y2,y3,y4,y5,y6,y7;
        int m1,m2,m3,m4,m5,m6,m7;
        m1=month1; m2 = month2; m3=month;  m4=month4; m5=month5; m6= month6; m7=month7;
        di1=day1; di2= day2; di3 = day; di4=day4; di5 = day5; di6= day6; di7 = day7;
        y1= year1; y2=year2;y3=year; y4=year4; y5=year5; y6=year6; y7= year7;
        int ab = day;*/
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
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                    && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE))
                return 0;//si son iguales

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

    public JSONArray cambiaLugar(JSONArray riegos, int pos1, int pos2){


        try {
            JSONObject primero= riegos.getJSONObject(pos1);
            JSONObject segundo= riegos.getJSONObject(pos2);
            riegos.put(pos1,segundo);
            riegos.put(pos2,primero);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return riegos;
    }

    public String imagenTipo(Calendar fecha_ref, Calendar fecha_riego, String tipo_r){

        if(comparaFechas(fecha_riego,fecha_ref)>0 &&comparaFechas(fecha_riego,fecha_ref)!=-4
                && comparaFechas(fecha_riego,fecha_ref)!=4){//fecha_riego>fecha_ref y no es nulo
            if(tipo_r== "Irrigation"){
                return "@drawable/proxriego";
            }

        }
        if(comparaFechas(fecha_riego,fecha_ref)<0 &&comparaFechas(fecha_riego,fecha_ref)!=-4
                && comparaFechas(fecha_riego,fecha_ref)!=4){//fecha_riego<fecha_ref y no es nulo
            if (tipo_r=="Irrigation"){
                return "@drawable/riego";
            }
            if (tipo_r=="Rain"){
                return "@drawable/ic_cloud";
            }

        }
        return "@drawable/noriego";

    }
    public static Date sumaDias(Date fecha, int dias){
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.DAY_OF_YEAR, dias);
        return cal.getTime();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_pivot1, container, false);
        ArrayList<String> listita = new ArrayList<>();
        SharedPreferences sp;
        sp = getActivity().getSharedPreferences("sesion",Context.MODE_PRIVATE);
        final String ref_date= sp.getString("Fecha_ref", "No hay fecha");
        final Calendar ref_d = CrearFecha(ref_date);
        //String anio_rf= Integer.toString(ref_d.get(Calendar.YEAR));
        //String mes_rf= Integer.toString(ref_d.get(Calendar.MONTH)+1);
       // String dia_rf =Integer.toString(ref_d.get(Calendar.DATE));


        try {
            String farm_pivots= sp.getString("actual_farm", "no hay actual farm");
            JSONArray todo = new JSONArray(farm_pivots) ;
            JSONObject establecimiento = todo.getJSONObject(0);
            pivots_est = establecimiento.getJSONArray("pivots");

            for(int i=0; i<pivots_est.length(); i++){
                JSONObject pv = pivots_est.getJSONObject(i);
                String nombrecom= pv.getString("nombre");
                String[] nompiv = new String[0];
                if( nombrecom.contains("pivot")) {
                    nompiv = nombrecom.split("pivot");
                }
                if (nombrecom.contains("Pivot")){
                    nompiv=nombrecom.split("Pivot");
                };
                listita.add("Pivot"+ nompiv[1]);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Spinner spi = (Spinner) v.findViewById(R.id.spinner_pivot);

        ArrayAdapter aa = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, listita);

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner

        spi.setAdapter(aa);


        spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               // Toast.makeText(getActivity(), "Seleccionaste el pivot" + (position + 1), Toast.LENGTH_LONG).show();
                try {
                    JSONObject pv = pivots_est.getJSONObject(position);
                    TextView cultivo = (TextView) getActivity().findViewById(R.id.piv_cultivo);
                    cultivo.setText(pv.getString("cultivo"));
                    TextView fecha = (TextView) getActivity().findViewById(R.id.fecha_cultivo);
                    Calendar f_riego = CrearFecha(pv.getString("siembra"));
                    String anio= Integer.toString(f_riego.get(Calendar.YEAR));
                    String mes= Integer.toString(f_riego.get(Calendar.MONTH)+1);
                    String dia =Integer.toString(f_riego.get(Calendar.DATE));
                    fecha.setText(dia + "/"+mes+ "/" +anio);
                    TextView fenologia = (TextView) getActivity().findViewById(R.id.fenologia);
                    fenologia.setText(pv.getString("fenologia"));
                    JSONArray riegos_traidos =null;
                    riegos_traidos= pv.getJSONArray("riegos");

                    ArrayList<TextView> fechas_riegos = new ArrayList<TextView>();
                    ArrayList<TextView> mm_riegos = new ArrayList<TextView>();
                    ArrayList<ImageView> tipos_riegos= new ArrayList<ImageView>();

                    fechas_riegos.add((TextView) getActivity().findViewById(R.id.tabla_fecha_1));
                    fechas_riegos.add((TextView) getActivity().findViewById(R.id.tabla_fecha_2));
                    fechas_riegos.add((TextView) getActivity().findViewById(R.id.tabla_fecha_3));
                    fechas_riegos.add((TextView) getActivity().findViewById(R.id.tabla_fecha_4));
                    fechas_riegos.add((TextView) getActivity().findViewById(R.id.tabla_fecha_5));
                    fechas_riegos.add((TextView) getActivity().findViewById(R.id.tabla_fecha_6));
                    fechas_riegos.add((TextView) getActivity().findViewById(R.id.tabla_fecha_7));
                    fechas_riegos.get(0).setText("");
                    fechas_riegos.get(1).setText("");
                    fechas_riegos.get(2).setText("");
                    fechas_riegos.get(3).setText("");
                    fechas_riegos.get(4).setText("");
                    fechas_riegos.get(5).setText("");
                    fechas_riegos.get(6).setText("");

                    mm_riegos.add((TextView) getActivity().findViewById(R.id.tabla_mm_1));
                    mm_riegos.add((TextView) getActivity().findViewById(R.id.tabla_mm_2));
                    mm_riegos.add((TextView) getActivity().findViewById(R.id.tabla_mm_3));
                    mm_riegos.add((TextView) getActivity().findViewById(R.id.tabla_mm_4));
                    mm_riegos.add((TextView) getActivity().findViewById(R.id.tabla_mm_5));
                    mm_riegos.add((TextView) getActivity().findViewById(R.id.tabla_mm_6));
                    mm_riegos.add((TextView) getActivity().findViewById(R.id.tabla_mm_7));
                    mm_riegos.get(0).setText("");
                    mm_riegos.get(1).setText("");
                    mm_riegos.get(2).setText("");
                    mm_riegos.get(3).setText("");
                    mm_riegos.get(4).setText("");
                    mm_riegos.get(5).setText("");
                    mm_riegos.get(6).setText("");




                    tipos_riegos.add((ImageView) getActivity().findViewById(R.id.tabla_estado_1));
                    tipos_riegos.add((ImageView) getActivity().findViewById(R.id.tabla_estado_2));
                    tipos_riegos.add((ImageView) getActivity().findViewById(R.id.tabla_estado_3));
                    tipos_riegos.add((ImageView) getActivity().findViewById(R.id.tabla_estado_4));
                    tipos_riegos.add((ImageView) getActivity().findViewById(R.id.tabla_estado_5));
                    tipos_riegos.add((ImageView) getActivity().findViewById(R.id.tabla_estado_6));
                    tipos_riegos.add((ImageView) getActivity().findViewById(R.id.tabla_estado_7));


                    tipos_riegos.get(0).setImageDrawable(getActivity().getDrawable(R.drawable.no_riego));
                    tipos_riegos.get(0).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));
                    tipos_riegos.get(1).setImageDrawable(getActivity().getDrawable(R.drawable.no_riego));
                    tipos_riegos.get(1).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));
                    tipos_riegos.get(2).setImageDrawable(getActivity().getDrawable(R.drawable.no_riego));
                    tipos_riegos.get(2).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));
                    tipos_riegos.get(3).setImageDrawable(getActivity().getDrawable(R.drawable.no_riego));
                    tipos_riegos.get(3).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));
                    tipos_riegos.get(4).setImageDrawable(getActivity().getDrawable(R.drawable.no_riego));
                    tipos_riegos.get(4).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));
                    tipos_riegos.get(5).setImageDrawable(getActivity().getDrawable(R.drawable.no_riego));
                    tipos_riegos.get(5).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));
                    tipos_riegos.get(6).setImageDrawable(getActivity().getDrawable(R.drawable.no_riego));
                    tipos_riegos.get(6).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));
                    SetearSieteFechas(ref_date);


                    ArrayList<String> dias_validos = new ArrayList<String>();
                    ArrayList<String> meses_validos = new ArrayList<String>();
                    ArrayList<String> anios_validos = new ArrayList<String>();

                    //setea los dias
                    if(Integer.toString(day1).length()==1)
                        dias_validos.add(0+Integer.toString(day1));
                    else
                        dias_validos.add(Integer.toString(day1));
                    if(Integer.toString(day2).length()==1)
                        dias_validos.add(0+Integer.toString(day2));
                    else
                        dias_validos.add(Integer.toString(day2));
                    if(Integer.toString(day).length()==1)
                        dias_validos.add(0+Integer.toString(day));
                    else
                        dias_validos.add(Integer.toString(day));
                    if(Integer.toString(day4).length()==1)
                        dias_validos.add(0+Integer.toString(day4));
                    else
                        dias_validos.add(Integer.toString(day4));
                    if(Integer.toString(day5).length()==1)
                        dias_validos.add(0+Integer.toString(day5));
                    else
                        dias_validos.add(Integer.toString(day5));
                    if(Integer.toString(day6).length()==1)
                        dias_validos.add(0+Integer.toString(day6));
                    else
                        dias_validos.add(Integer.toString(day6));
                    if(Integer.toString(day7).length()==1)
                        dias_validos.add(0+Integer.toString(day7));
                    else
                        dias_validos.add(Integer.toString(day7));

                    //setea los meses
                    if(Integer.toString(month1).length()==1)
                        meses_validos.add(0+Integer.toString(month1));
                    else
                        meses_validos.add(Integer.toString(month1));
                    if(Integer.toString(month2).length()==1)
                        meses_validos.add(0+Integer.toString(month2));
                    else
                        meses_validos.add(Integer.toString(month2));
                    if(Integer.toString(month).length()==1)
                        meses_validos.add(0+Integer.toString(month));
                    else
                        meses_validos.add(Integer.toString(month));
                    if(Integer.toString(month4).length()==1)
                        meses_validos.add(0+Integer.toString(month4));
                    else
                        meses_validos.add(Integer.toString(month4));
                    if(Integer.toString(month5).length()==1)
                        meses_validos.add(0+Integer.toString(month5));
                    else
                        meses_validos.add(Integer.toString(month5));
                    if(Integer.toString(month6).length()==1)
                        meses_validos.add(0+Integer.toString(month6));
                    else
                        meses_validos.add(Integer.toString(month6));
                    if(Integer.toString(month7).length()==1)
                        meses_validos.add(0+Integer.toString(month7));
                    else
                        meses_validos.add(Integer.toString(month7));

                    //setea los años
                    anios_validos.add(Integer.toString(year1));
                    anios_validos.add(Integer.toString(year2));
                    anios_validos.add(Integer.toString(year));
                    anios_validos.add(Integer.toString(year4));
                    anios_validos.add(Integer.toString(year5));
                    anios_validos.add(Integer.toString(year6));
                    anios_validos.add(Integer.toString(year7));


                    JSONArray riegos= new JSONArray();
                    if(riegos_traidos.isNull(0)){
                        for(int i =0; i<7;i++){
                            String fec =anios_validos.get(i)+"-"+meses_validos.get(i)+"-"+dias_validos.get(i);
                            Calendar fecha_valida = CrearFecha(fec);
                            String anio_v= Integer.toString(fecha_valida.get(Calendar.YEAR));
                            String mes_v= Integer.toString(fecha_valida.get(Calendar.MONTH)+1);
                            String dia_v =Integer.toString(fecha_valida.get(Calendar.DATE));
                            fechas_riegos.get(i).setText(dia_v+"/"+mes_v+"/"+anio_v);
                            mm_riegos.get(i).setText("-");
                            tipos_riegos.get(i).setImageDrawable(getActivity().getDrawable(R.drawable.no_riego));
                            tipos_riegos.get(i).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));
                        }
                    }

                    for(int a =0; a<7;a++){
                        for(int b=0; b<riegos_traidos.length();b++){

                            JSONObject riegotraido = riegos_traidos.getJSONObject(b);
                            Calendar fecha_rtraido= CrearFecha(riegotraido.getString("fecha"));
                           // String abc = riegotraido.getString("fecha");
                           // Date fec = new Date(Integer.valueOf(anios_validos.get(a)),Integer.valueOf(meses_validos.get(a)),Integer.valueOf(dias_validos.get(a)));
                            String fec =anios_validos.get(a)+"-"+meses_validos.get(a)+"-"+dias_validos.get(a);
                            Calendar fecha_valida = CrearFecha(fec);
                            if(comparaFechas(fecha_valida,fecha_rtraido)==0)
                                    riegos.put(riegos_traidos.getJSONObject(b));
                        }
                    }


                    for(int i =0; i<riegos.length();i++){
                        if(riegos.get(i)!= ""){
                            JSONObject riego = riegos.getJSONObject(i);
                            Calendar fecha_rt= CrearFecha(riego.getString("fecha"));

                            for(int j=0; j<riegos.length();j++){
                                JSONObject riego1 = riegos.getJSONObject(j);
                                Calendar fecha_riego= CrearFecha(riego1.getString("fecha"));
                                int comp= comparaFechas(fecha_rt, fecha_riego);
                                if(comp>3){
                                    riegos= cambiaLugar(riegos,i,j);

                                }

                            }
                        }
                    }






                    for(int i=0; i<7;i++){
                        for(int j=0; j<riegos.length(); j++){
                            String fec =anios_validos.get(i)+"-"+meses_validos.get(i)+"-"+dias_validos.get(i);
                            Calendar fecha_valida = CrearFecha(fec);


                            JSONObject riego = riegos.getJSONObject(j);
                           /* JSONObject riego1 = riegos.getJSONObject(0);
                            JSONObject riego2 = riegos.getJSONObject(1);
                            JSONObject riego3 = riegos.getJSONObject(2);
                            Calendar fecha_riego1= CrearFecha(riego1.getString("fecha"));
                            Calendar fecha_riego2= CrearFecha(riego2.getString("fecha"));
                            Calendar fecha_riego3= CrearFecha(riego3.getString("fecha"));
                            int d1,d2,d3,m1,m2,m3,y1,y2,y3;
                            d1= fecha_riego1.get(Calendar.DATE); m1= fecha_riego1.get(Calendar.MONTH); y1= fecha_riego1.get(Calendar.YEAR);
                            d2= fecha_riego2.get(Calendar.DATE); m2= fecha_riego2.get(Calendar.MONTH); y2= fecha_riego2.get(Calendar.YEAR);
                            d3= fecha_riego3.get(Calendar.DATE); m3= fecha_riego3.get(Calendar.MONTH); y3= fecha_riego3.get(Calendar.YEAR);
                            int rc = 2;*/

                            Calendar fecha_riego= CrearFecha(riego.getString("fecha"));
                                //Calendar fecha_valida = CrearFecha(anios_validos.get(i)+meses_validos.get(i)+ anios_validos.get(i));

                            if(comparaFechas(fecha_riego,fecha_valida)!=0){
                                String anio_v= Integer.toString(fecha_valida.get(Calendar.YEAR));
                                String mes_v= Integer.toString(fecha_valida.get(Calendar.MONTH)+1);
                                String dia_v =Integer.toString(fecha_valida.get(Calendar.DATE));
                                fechas_riegos.get(i).setText(dia_v+"/"+mes_v+"/"+anio_v);
                                if(mm_riegos.get(i).getText().equals("-") || mm_riegos.get(i).getText().equals(""))
                                    mm_riegos.get(i).setText("-");
                                //if(!tipos_riegos.get(i).equals(R.drawable.riego) || tipos_riegos.get(i) == null)
                                //tipos_riegos.get(i).setImageDrawable(getActivity().getDrawable(R.drawable.no_riego));
                                tipos_riegos.get(i).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));
                                tipos_riegos.get(i).setMaxWidth(10);
                                tipos_riegos.get(i).setMaxHeight(10);
                                for(int n=riegos.length()-1;n>i;n--){
                                   riegos=cambiaLugar(riegos,n,n-1);
                                }
                            }
                            else{
                                String anio_r= Integer.toString(fecha_riego.get(Calendar.YEAR));
                                String mes_r= Integer.toString(fecha_riego.get(Calendar.MONTH)+1);
                                String dia_r =Integer.toString(fecha_riego.get(Calendar.DATE));
                                fechas_riegos.get(i).setText(dia_r+"/"+mes_r+"/"+anio_r);
                                String mm_riego= riego.getString("milimetros");
                                mm_riegos.get(i).setText(mm_riego);
                                String tipo= riego.getString("tipo");
                                if(comparaFechas(fecha_riego,ref_d)>0 &&comparaFechas(fecha_riego,ref_d)!=-4
                                        && comparaFechas(fecha_riego,ref_d)!=4){//fecha_riego>fecha_ref y no es nulo
                                    if(tipo.equals("Irrigation")){
                                        tipos_riegos.get(i).setImageDrawable(getActivity().getDrawable(R.drawable.proxriego));

                                    }
                                    if (tipo.equals("Rain")) {

                                        tipos_riegos.get(i).setImageDrawable(getActivity().getDrawable(R.drawable.no_riego));
                                        tipos_riegos.get(i).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));
                                        String mmvacio= "-";
                                        mm_riegos.get(i).setText(mmvacio);
                                    }

                                }
                                if(comparaFechas(fecha_riego,ref_d)<0 &&comparaFechas(fecha_riego,ref_d)!=-4
                                        && comparaFechas(fecha_riego,ref_d)!=4){//fecha_riego<fecha_ref y no es nulo
                                    if (tipo.equals("Irrigation")){
                                        tipos_riegos.get(i).setImageDrawable(getActivity().getDrawable(R.drawable.riego));

                                    }
                                    if (tipo.equals("Rain")){
                                        tipos_riegos.get(i).setImageDrawable(getActivity().getDrawable(R.drawable.ic_cloud));
                                        //tipos_riegos.get(i).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));
                                    }

                                }
                                //int ab = comparaFechas(fecha_riego,ref_d);
                                if(comparaFechas(fecha_riego,ref_d)==0) { //&&comparaFechas(fecha_riego,ref_d)!=-4
                                    //&& comparaFechas(fecha_riego,ref_d)!=4){
                                    if (tipo.equals("Irrigation")) {
                                        tipos_riegos.get(i).setImageDrawable(getActivity().getDrawable(R.drawable.riego));
                                        tipos_riegos.get(i).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));

                                    }
                                    if (tipo.equals("Rain")) {
                                        tipos_riegos.get(i).setImageDrawable(getActivity().getDrawable(R.drawable.ic_cloud));
                                        tipos_riegos.get(i).setColorFilter(getActivity().getResources().getColor(R.color.colorNoriego));

                                    }
                                }
                               // }

                            }



                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        MainActivity ma = (MainActivity) getActivity();
        ma.setItemVisible(1,true);
        ma.setItemVisible(2,true);
        ma.setItemVisible(3,true);
        ma.setItemVisible(4,true);

        return v;
    }




}
