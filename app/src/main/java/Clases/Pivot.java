package Clases;


import java.util.ArrayList;
import java.util.Date;

/**
 * Created by olave on 26/09/2017.
 */

public class Pivot {


    String nombre;
    String cultivo;
    String siembra;
    String fenologia;
    ArrayList<Riego> riegos;


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

    public String getSiembra() {
        return siembra;
    }

    public void setSiembra(String siembra) {
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

    public Pivot(String nombre, String cultivo, String siembra, String fenologia) {
        this.nombre = nombre;
        this.cultivo = cultivo;
        this.siembra = siembra;
        this.fenologia = fenologia;
        this.riegos = new ArrayList<>();
    }
}
