package Clases;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by olave on 26/09/2017.
 */

public class Pivot {

    int pv_id;
    String nombre;
    String cultivo;
    Timestamp siembra;
    String fenologia;
    ArrayList<Riego> riegos;

    public int getId() {
        return pv_id;
    }

    public void setId(int pv_id) {
        this.pv_id = pv_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCultivo() {
        return cultivo;
    }

    public void setCultivo(String cultivo) {
        this.cultivo = cultivo;
    }

    public Timestamp getSiembra() {
        return siembra;
    }

    public void setSiembra(Timestamp siembra) {
        this.siembra = siembra;
    }

    public String getFenologia() {
        return fenologia;
    }

    public void setFenologia(String fenologia) {
        this.fenologia = fenologia;
    }

    public ArrayList<Riego> getRiegos() {
        return riegos;
    }

    public void setRiegos(ArrayList<Riego> riegos) {
        this.riegos = riegos;
    }

    public Pivot(){}

    public Pivot(int pv_id, String nombre, String cultivo, Timestamp siembra, String fenologia) {
        this.pv_id = pv_id;
        this.nombre = nombre;
        this.cultivo = cultivo;
        this.siembra = siembra;
        this.fenologia = fenologia;
        this.riegos = new ArrayList<>();
    }
}
