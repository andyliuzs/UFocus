package www.ufcus.com.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import www.ufcus.com.app.App;


/**
 * Created by dongjunkun on 2016/1/13.
 */
public class DBManager {
    public static final String AUTHORITY = "www.ufcus.com.db";
    public static final String BASE_DIR_TYPE = "vnd.android.cursor.dir/vnd.ufocus";
    public static final String BASE_ITEM_TYPE = "vnd.android.cursor.item/vnd.ufocus";

    private SQLiteDatabase db;
    private MyDBHelper dbHelper;

    public DBManager() {
        dbHelper = new MyDBHelper(App.getInstance());
    }

    /**
     * 插入缓存
     *
     * @param url  地址
     * @param data json数据
     */
    public synchronized void insertData(String url, String data) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(URL, url);
        values.put(DATA, data);
        values.put(TIME, System.currentTimeMillis());
        db.replace(CACHE, null, values);
        db.close();
    }

    /**
     * 根据url获取缓存数据
     *
     * @param url 地址
     * @return 数据
     */
    public synchronized String getData(String url) {
        String result = "";
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CACHE + " WHERE URL = ?", new String[]{url});
        while (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex(DATA));
        }
        cursor.close();
        db.close();
        return result;
    }


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

    class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context) {
            super(context, DBManager.DBNAME, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CACHE_TABLE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
