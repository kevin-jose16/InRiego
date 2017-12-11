package Persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
/**
 * Created by olave on 10/10/2017.
 */

public class Json_SQLiteHelper extends SQLiteOpenHelper {

    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate = "CREATE TABLE Jsons (codigo INTEGER PRIMARY KEY," +
            " sv_json TEXT," +
            " fecha_ins TEXT," +
            " usuario TEXT," +
            " establecimiento TEXT," +
            " tipo_riego TEXT )";

    //String sql = "DROP TABLE IF EXISTS log";
    String sqlLog = "CREATE TABLE log (cod INTEGER PRIMARY KEY, salida TEXT, user TEXT)";

    public Json_SQLiteHelper(Context contexto, String nombre,
                                 CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sqlLog);
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Jsons");

        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlLog);
    }
}
