package Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.olave.inriego.R;

import java.util.ArrayList;

import Clases.Establecimiento;

/**
 * Created by olave on 27/09/2017.
 */

public class AdapterEstablecimientos extends BaseAdapter {

    Activity activity;
    ArrayList<Establecimiento> establecimientos;

    public ArrayList<Establecimiento> getEstablecimientos() {
        return establecimientos;
    }

    public void setEstablecimientos(ArrayList<Establecimiento> establecimientos) {
        this.establecimientos = establecimientos;
    }

    @Override
    public int getCount() {
        return establecimientos.size();
    }

    @Override
    public Object getItem(int i) {
        return establecimientos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return establecimientos.get(i).getEst_id();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = view;
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.fila_establecimiento, null);
        }
        Establecimiento est = establecimientos.get(i);
        TextView text = (TextView) v.findViewById(R.id.item_est);
        text.setText(est.getDescripcion());
        return v;
    }

    public AdapterEstablecimientos(){}

    public AdapterEstablecimientos(Activity activity, ArrayList<Establecimiento> establecimientos){
        this.activity = activity;
        this.establecimientos = establecimientos;
    }
}
