package Persistencia;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by olave on 10/10/2017.
 */

public class SQLiteHelper extends Activity {

    public static Json_SQLiteHelper jsonsdb;

    public SQLiteHelper(SQLiteDatabase db, Json_SQLiteHelper usu, String json,String usuario,String establecimiento,String tipo_riego)
    {


        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        jsonsdb = usu;

        //String dbname = usdbh.getDatabaseName();

        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            //Insertamos 5 usuarios de ejemplo

            Calendar cal = Calendar.getInstance();
            //cal.getTime().toString()
                //Insertamos los datos en la tabla Usuarios
                db.execSQL("INSERT INTO Jsons (codigo, sv_json, fecha_ins,usuario,establecimiento, tipo_riego) " +
                        "VALUES (1,'" + json + "', '" + tipo_riego +"', '"+ usuario +"', '"+ establecimiento +"', '"+ tipo_riego +"')");
            //Cerramos la base de datos
            db.close();
        }

    }

    public SQLiteHelper(SQLiteDatabase db, Json_SQLiteHelper usu){
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        jsonsdb = usu;
    }

    public Cursor obtener(){

        SQLiteDatabase db = jsonsdb.getReadableDatabase();
        String query = "SELECT sv_json, fecha_ins FROM Jsons";
        Cursor cursor = db.rawQuery(query, null);

        /*if (cursor != null)
            cursor.moveToFirst();*/
        return cursor;
    }

    public void borrar(SQLiteDatabase db, Json_SQLiteHelper usu){
        if(db != null) {
            db.execSQL("DELETE FROM Jsons");
            db.close();
        }
    }
}
