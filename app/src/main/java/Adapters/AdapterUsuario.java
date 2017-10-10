package Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import Clases.Usuario;

/**
 * Created by olave on 27/09/2017.
 */

public class AdapterUsuario extends BaseAdapter{

    Activity activity;
    ArrayList<Usuario> usuarios;
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
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

    public AdapterUsuario(){}

    public AdapterUsuario(Activity activity, ArrayList<Usuario> usuarios){
        this.activity = activity;
        this.usuarios = usuarios;
    }
}
