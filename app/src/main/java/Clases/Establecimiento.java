package Clases;

import java.util.ArrayList;

/**
 * Created by olave on 26/09/2017.
 */

public class Establecimiento {

    int est_id;
    String descripcion;
    ArrayList<Pivot> pivots;

    public int getEst_id() {
        return est_id;
    }

    public void setEst_id(int est_id) {
        this.est_id = est_id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<Pivot> getPivots() {
        return pivots;
    }

    public void setPivots(ArrayList<Pivot> pivots) {
        this.pivots = pivots;
    }

    public Establecimiento(){}

    public Establecimiento(int est_id, String descripcion) {
        this.est_id = est_id;
        this.descripcion = descripcion;
        this.pivots = new ArrayList<>();
    }
}
