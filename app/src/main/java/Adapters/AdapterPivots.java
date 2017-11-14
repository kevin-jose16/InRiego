package Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.olave.inriego.R;

import java.util.ArrayList;

import Clases.Pivot;

/**
 * Created by olave on 14/09/2017.
 */

public class AdapterPivots extends BaseAdapter {

    public Activity activity;
    public ArrayList<Pivot> pivots;

    public ArrayList<Pivot> getPivots() {
        return pivots;
    }

    public void setPivots(ArrayList<Clases.Pivot> pivotsr) {

        pivots = pivotsr;
    }

    public AdapterPivots(){}

    @Override
    public int getCount() {
        return pivots.size();
    }

    @Override
    public Object getItem(int i) {
        return pivots.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        if (view == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.fila_pivot, null);
        }
        Pivot pv = pivots.get(i);

        Spinner spi = (Spinner) v.findViewById(R.id.spinner_pivot);
        ArrayList <String> listita = new ArrayList<>();
        listita.add("hola");
        ArrayAdapter aa = new ArrayAdapter(activity,android.R.layout.select_dialog_multichoice,listita);
         aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner

        spi.setAdapter(aa);

        CheckedTextView chtv = (CheckedTextView) v.findViewById(R.id.check_text);
        //pv.getId() + " - " +
        chtv.setText( pv.getId() + " - " + pv.getNombre());

        return v;
    }

    public AdapterPivots(Activity activity, ArrayList<Pivot> pivots) {
        this.activity = activity;
        this.pivots = pivots;
    }
}