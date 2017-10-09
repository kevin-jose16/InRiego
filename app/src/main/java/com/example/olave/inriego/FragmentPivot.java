package com.example.olave.inriego;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.olave.inriego.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentPivot extends Fragment {


    String[] listita={"PIVOT_1","PIVOT_2","PIVOT_3","PIVOT_4"};




      /*  Spinner spin = (Spinner) getActivity().findViewById(R.id.spinner_pivot);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Seleccionaste el pivot"+(position+1), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.select_dialog_multichoice,listita);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);*/



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity ma = (MainActivity) getActivity();
        ma.setItemVisible(1,true);
        ma.setItemVisible(2,true);
        ma.setItemVisible(3,true);
        ma.setItemVisible(4,true);
        return inflater.inflate(R.layout.fragment_pivot, container, false);
    }




}
