package www.ufcus.com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.orhanobut.logger.Logger;

import www.ufcus.com.beans.ClockBean;

/**
 * Created by dongjunkun on 2016/1/12.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 2;


    private final static String TABLE_CLOCK = "CREATE TABLE IF NOT EXISTS " +
            ClockBean.CLOCK_BEAN_TABLE + " (" +
            ClockBean._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ClockBean.PHONE_NUMBER + " TEXT," +
            ClockBean.CLOCK_TIME + " INTEGER," +
            ClockBean.GROUP_BY + " TEXT"
            + ");";

    public DBHelper(Context context) {
        super(context, DBManager.AUTHORITY, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CLOCK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                addColumn(db, ClockBean.CLOCK_BEAN_TABLE, ClockBean.GROUP_BY, "TEXT");
                Logger.v(ClockBean.CLOCK_BEAN_TABLE + "add column " + ClockBean.GROUP_BY);
            } catch (SQLiteException e) {
                Logger.v(ClockBean.CLOCK_BEAN_TABLE + "add column " + ClockBean.GROUP_BY + e.getLocalizedMessage());
            }
        }
        onCreate(db);
    }

    private static void addColumn(SQLiteDatabase db, String table, String field, String type) {
        db.execSQL("ALTER TABLE " + table + " ADD " + field + " " + type);
    }

    @Override
    public synchronized void close() {
        super.close();
    }
}
