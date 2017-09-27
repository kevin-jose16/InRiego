package Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.olave.inriego.R;

import java.util.ArrayList;

import Clases.Riego;

/**
 * Created by olave on 27/09/2017.
 */

public class AdapterRiegos extends BaseAdapter {

    public Activity activity;
    public ArrayList<Riego> riegos;

    public ArrayList<Riego> getRiegos() {
        return riegos;
    }

    public void setRiegos(ArrayList<Riego> riegos) {
        this.riegos = riegos;
    }

    @Override
    public int getCount() {
        return riegos.size();
    }

    @Override
    public Object getItem(int i) {
        return riegos.get(i);
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
            //v = inf.inflate(R.layout.fila_pivot, null);
        }

        return v;
    }
    public AdapterRiegos(Activity activity, ArrayList<Riego> riegos) {
        this.activity = activity;
        this.riegos = riegos;
    }

    public AdapterRiegos(){}
}
