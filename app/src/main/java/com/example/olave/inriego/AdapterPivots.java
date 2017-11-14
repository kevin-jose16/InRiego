package com.example.olave.inriego;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by olave on 14/09/2017.
 */

public class AdapterPivots extends BaseAdapter {

    protected Activity activity;

    //protected ArrayList<Pivot> Pivots;
    public static ArrayList<String> Pivots;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    //public ArrayList<Pivot> getPivots() {return Pivots;}
    public ArrayList<String> getPivots() {
        return Pivots;
    }

    //public void setPivots(ArrayList<Pivot> pivots) {Pivots = pivots;}
    public void setPivots(ArrayList<String> pivots) {
        Pivots = pivots;
    }

    @Override
    public int getCount() {
        return Pivots.size();
    }

    @Override
    public Object getItem(int i) {
        return Pivots.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_pivot, null);
        }
        //Pivot pv = Pivots.get(i);
        String pv = Pivots.get(i);

        TextView pivot = (TextView) v.findViewById(R.id.it_pivot);
        pivot.setText(pv);
        return v;
    }

    public AdapterPivots() {
        this.activity = activity;

    }
}
