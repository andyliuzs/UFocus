package www.ufcus.com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import www.ufcus.com.beans.ClockBean;

/**
 * Created by dongjunkun on 2016/1/12.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "djk";
    public static final int version = 1;
    public static final String CACHE = "cache";
    public static final String ID = "_id";
    public static final String URL = "url";
    public static final String DATA = "data";
    public static final String TIME = "time";

    public static final String CACHE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "
            + CACHE + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + URL + " TEXT, "
            + TIME + " TEXT, "
            + DATA + " TEXT)";

    private final static String TABLE_CLOCK = "CREATE TABLE IF NOT EXISTS " +
            ClockBean.CLOCK_BEAN_TABLE + " (" +
            ClockBean._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ClockBean.PHONE_NUMBER + " TEXT," +
            ClockBean.CLOCK_TIME + " INTEGER"
            + ");";

    public DBHelper(Context context) {
        super(context, DBNAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CACHE_TABLE_SQL);
        db.execSQL(TABLE_CLOCK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public synchronized void close() {
        super.close();
    }
}
