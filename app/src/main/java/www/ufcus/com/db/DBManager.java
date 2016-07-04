package www.ufcus.com.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import www.ufcus.com.app.App;


/**
 * Created by dongjunkun on 2016/1/13.
 */
public class DBManager {
    public static final String AUTHORITY = "www.ufcus.com.db";
    public static final String BASE_DIR_TYPE = "vnd.android.cursor.dir/vnd.ufocus";
    public static final String BASE_ITEM_TYPE = "vnd.android.cursor.item/vnd.ufocus";

//    private SQLiteDatabase db;
//    private DBHelper dbHelper;
//
//    public DBManager() {
//        dbHelper = new DBHelper(App.getInstance());
//    }
//
//    /**
//     * 插入缓存
//     *
//     * @param url  地址
//     * @param data json数据
//     */
//    public synchronized void insertData(String url, String data) {
//        db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(DBHelper.URL, url);
//        values.put(DBHelper.DATA, data);
//        values.put(DBHelper.TIME, System.currentTimeMillis());
//        db.replace(DBHelper.CACHE, null, values);
//        db.close();
//    }
//
//    /**
//     * 根据url获取缓存数据
//     *
//     * @param url 地址
//     * @return 数据
//     */
//    public synchronized String getData(String url) {
//        String result = "";
//        db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.CACHE + " WHERE URL = ?", new String[]{url});
//        while (cursor.moveToNext()) {
//            result = cursor.getString(cursor.getColumnIndex(DBHelper.DATA));
//        }
//        cursor.close();
//        db.close();
//        return result;
//    }


}
