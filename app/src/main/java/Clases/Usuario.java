package Clases;

import java.util.ArrayList;

/**
 * Created by olave on 27/09/2017.
 */

public class Usuario {

    String nick;
    String password;
    ArrayList<Establecimiento> establecimientos;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Establecimiento> getEstablecimientos() {
        return establecimientos;
    }

    public void setEstablecimientos(ArrayList<Establecimiento> establecimientos) {
        this.establecimientos = establecimientos;
    }

    public Usuario(){}

    public Usuario(String nick, String password) {
        this.nick = nick;
        this.password = password;
        this.establecimientos = new ArrayList<>();
    }
}
