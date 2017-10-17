package com.example.olave.inriego;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by olave on 14/09/2017.
 */

public class DatePickerFragment_Lluvia extends DialogFragment
        //implements DatePickerDialog.OnDateSetListener
{
    Calendar cmin = Calendar.getInstance(),cmax = Calendar.getInstance();
    // year = Calendar.YEAR o año
    int year, yearmin;

    //month = Calendar.MONTH o numeros entre 0 y 11 (los meses van del 0 al 11)
    int month,monthmin;

    //los dias van del 1 al 31
    int day,daymin;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date dtmax = new Date();
        Date dtmin = new Date();

        //String strFecha = "2017-09-24aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        //SetearFechas(strFecha);

        cmin.set(yearmin,monthmin,daymin);
        dtmin = cmin.getTime();
        cmax.set(year,month,day);
        dtmax = cmax.getTime();
        DatePickerDialog dpdialog =

                new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Button tv1= (Button) getActivity().findViewById(R.id.btn_fecha_lluvia);
                        int mes = view.getMonth()+1;
                        tv1.setText(view.getDayOfMonth()+"/"+mes+"/"+view.getYear());
                    }
                }, year,month,day);
        dpdialog.getDatePicker().setMaxDate(dtmax.getTime());
        dpdialog.getDatePicker().setMinDate(dtmin.getTime());
        return dpdialog;

    }


    public void SetearFechas(String fecha) {
        year=Integer.parseInt(""+fecha.charAt(0)+fecha.charAt(1)+fecha.charAt(2)+fecha.charAt(3));

        if("0".equals(fecha.charAt(5)))
            month=Integer.parseInt(""+ fecha.charAt(6));
        else
            month=Integer.parseInt(""+fecha.charAt(5)+fecha.charAt(6));

        if("0".equals(fecha.charAt(8)))
            day = Integer.parseInt(""+fecha.charAt(9));
        else
            day = Integer.parseInt(""+fecha.charAt(8)+fecha.charAt(9));


        boolean bisiesto = false;
        if (year % 4 == 0 & year % 100 != 0 || year % 400 == 0)
            bisiesto = true;
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            if (month == 1) {
                if (day == 1 || day == 2) {
                    yearmin = year - 1;
                    monthmin = 12;
                    if (day == 1)
                        daymin = 30;
                    else
                        daymin = 31;
                }  else {
                    daymin = day - 2;
                    yearmin = year;
                    monthmin = month;

                }
            }
            if (month == 3) {
                yearmin = year;
                if (day == 1 || day == 2) {
                    if (bisiesto)
                        if (day == 1)
                            daymin = 28;
                        else if (day == 2)
                            daymin = 29;
                    if (!bisiesto)
                        if (day == 1)
                            daymin = 27;
                        else if (day == 2)
                            daymin = 28;
                    monthmin = month - 1;
                } else {
                    monthmin = month;
                    daymin = day - 2;
                }
            }

            if (month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                yearmin = year;
                monthmin = month;
                if (day == 1){
                    monthmin = month-1;
                    daymin = 29;
                }
                if (day == 2){
                    monthmin = month-1;
                    daymin = 30;
                }
                if(day>=3 && day<=31)
                    daymin =day-2;
                if(month==8){
                    if (day == 1)
                        daymin = 30;
                    else if (day == 2)
                        daymin = 31;
                }
            }

        } else {
            yearmin = year;
            if (month == 2) {
                monthmin = 2;
                if (bisiesto)
                     if (day >= 3 && day <= 29)
                        daymin = day - 2;

                if (!bisiesto)
                    if (day >= 3 && day <= 29)
                        daymin = day - 2;
            } else {
                if (day >= 3 && day <= 30){
                    daymin = day - 2;
                    monthmin = month;
                }

            }

            if (day == 1){
                monthmin=month-1;
                daymin = 30;
            }

            else if (day == 2){
                monthmin=month-1;
                daymin = 31;
            }

        }
        monthmin--; month--;

    }
}
