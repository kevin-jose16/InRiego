package Clases;

import java.util.ArrayList;

/**
 * Created by olave on 26/09/2017.
 */

public class Establecimiento {

    int est_id;
    String descripcion;
    String ref_date;
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

    public String getRef_date() {
        return ref_date;
    }

    public void setRef_date(String ref_date) {
        this.ref_date = ref_date;
    }

    public ArrayList<Pivot> getPivots() {
        return pivots;
    }

    public void setPivots(ArrayList<Pivot> pivots) {
        this.pivots = pivots;
    }

    public Establecimiento(){}

    public Establecimiento(int est_id, String descripcion, String ref_date) {
        this.est_id = est_id;
        this.descripcion = descripcion;
        this.ref_date = ref_date;
        this.pivots = new ArrayList<>();
    }
}
