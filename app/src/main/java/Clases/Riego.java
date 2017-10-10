package Clases;

import java.util.Date;

/**
 * Created by olave on 26/09/2017.
 */

public class Riego {
    String tipo;
    Date fecha;
    float milimetros;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public float getMilimetros() {
        return milimetros;
    }

    public void setMilimetros(int milimetros) {
        this.milimetros = milimetros;
    }
    public Riego(){}

    public Riego(String tipo, Date fecha, float milimetros) {
        this.tipo = tipo;
        this.fecha = fecha;
        this.milimetros = milimetros;
    }
}
