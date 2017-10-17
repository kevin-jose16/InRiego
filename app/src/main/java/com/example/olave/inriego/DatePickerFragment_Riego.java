package com.example.olave.inriego;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import layout.Fm_AgregarRiego;

/**
 * Created by olave on 14/09/2017.
 */

public class DatePickerFragment_Riego extends DialogFragment
        //implements DatePickerDialog.OnDateSetListener
{
    Calendar cmin = Calendar.getInstance(),cmax = Calendar.getInstance(), cal = Calendar.getInstance();
    // year = Calendar.YEAR o año
    int year, yearmax, yearmin;

    //month = Calendar.MONTH o numeros entre 0 y 11 (los meses van del 0 al 11)
    int month, monthmax,monthmin;

    //los dias van del 1 al 31
    int day, daymax,daymin;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date dtmax = new Date();
        Date dtmin = new Date();

        //String strFecha = "2017-09-24aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        //SetearFechas(strFecha);

        cmin.set(yearmin,monthmin,daymin);
        dtmin = cmin.getTime();
        cmax.set(yearmax,monthmax,daymax);
        dtmax = cmax.getTime();
        DatePickerDialog dpdialog =

        new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Button tv1= (Button) getActivity().findViewById(R.id.btn_fecha_riego);
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
                    monthmax = 1;
                    yearmax = year;
                    daymax = day + 4;
                    if (day == 1)
                        daymin = 30;
                    else
                        daymin = 31;
                } else if (day == 28 || day == 29 || day == 30 || day == 31) {
                    yearmin = year;
                    monthmax = 2;
                    daymin = day - 2;
                    monthmin = month;
                    yearmax = year;
                    if (day == 28)
                        daymax = 1;
                    if (day == 29)
                        daymax = 2;
                    if (day == 30)
                        daymax = 3;
                    if (day == 31)
                        daymax = 4;
                } else {
                    daymin = day - 2;
                    daymax = day + 4;
                    yearmin = year;
                    yearmax = year;
                    monthmin = month;
                    monthmax = month;
                }
            }
            if (month == 3) {
                yearmax = year;
                yearmin = year;
                if (day == 28 || day == 29 || day == 30 || day == 31) {
                    daymin = day - 2;
                    monthmin = month;
                    monthmax = month + 1;
                    if (day == 28)
                        daymax = 1;
                    if (day == 29)
                        daymax = 2;
                    if (day == 30)
                        daymax = 3;
                    if (day == 31)
                        daymax = 4;
                } else if (day == 1 || day == 2) {
                    daymax = day + 4;
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
                    monthmax = month;
                } else {
                    monthmin = month;
                    monthmax = month;
                    daymin = day - 2;
                    daymax = day + 4;
                }
            }

            if (month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                yearmin = year;
                monthmin = month;
                yearmax=year;

                if (day == 1){
                    monthmin = month-1;
                    monthmax=month;
                    daymin = 29;
                    daymax=day+4;
                }

                if (day == 2){
                    monthmin = month-1;
                    monthmax=month;
                    daymin = 30;
                    daymax=day+4;
                }


                if(day>=3 && day<=31){

                    monthmax=month;
                    daymin =day-2;
                    daymax = day+4;
                    if(day>=28 && day<=31) {
                        monthmax=month+1;
                        if (day == 28)
                            daymax = 1;
                        if (day == 29)
                            daymax = 2;
                        if (day == 30)
                            daymax = 3;
                        if (day == 31)
                            daymax = 4;
                    }
                }
                if (month == 12) {
                    if(day>=28 && day <=31){
                        yearmax = year+1;
                        monthmax = 1;
                    }
                    else{
                        yearmax = year;
                        monthmax = month;
                    }

                }
                if(month==8){
                    if (day == 1)
                        daymin = 30;
                    else if (day == 2)
                        daymin = 31;
                }
            }

        } else {
            yearmax = year;
            yearmin = year;
            if (month == 2) {
                monthmin = 2;
                if (bisiesto) {
                    if (day == 26 || day == 27 || day == 28 || day == 29) {
                        daymin = day - 2;
                        monthmax = 3;
                        if (day == 26)
                            daymax = 1;
                        if (day == 27)
                            daymax = 2;
                        if (day == 28)
                            daymax = 3;
                        if (day == 29)
                            daymax = 4;
                    } else if (day >= 3 && day <= 25) {
                        daymin = day - 2;
                        monthmax = 2;
                        daymax = day + 4;
                    }

                }
                if (!bisiesto) {
                    if (day == 25 || day == 26 || day == 27 || day == 28) {
                        daymin = day - 2;
                        monthmax = 3;
                        if (day == 25)
                            daymax = 1;
                        if (day == 26)
                            daymax = 2;
                        if (day == 27)
                            daymax = 3;
                        if (day == 28)
                            daymax = 4;
                    } else if (day >= 3 && day <= 24) {
                        monthmin=2;
                        daymin = day - 2;
                        monthmax = 2;
                        daymax = day + 4;
                    }
                }
            } else {
                monthmin = month;
                daymin = day - 2;
                if (day == 27 || day == 28 || day == 29 || day == 30) {
                    monthmax = month + 1;
                    if (day == 27)
                        daymax = 1;
                    if (day == 28)
                        daymax = 2;
                    if (day == 29)
                        daymax = 3;
                    if (day == 30)
                        daymax = 4;
                }
                if (day >= 3 && day <= 26){
                    monthmax = month;
                    daymax = day + 4;
                }

            }

            if (day == 1){
                monthmax=month;
                monthmin=month-1;
                daymax = day + 4;
                daymin = 30;
            }

            else if (day == 2){
                monthmax=month;
                monthmin=month-1;
                daymax = day + 4;
                daymin = 31;
            }

        }
        monthmin--; month--; monthmax--;

    }
}