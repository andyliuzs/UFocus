package www.ufcus.com.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Binder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.database.DatabaseUtilsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import www.ufcus.com.app.App;
import www.ufcus.com.beans.ClockBean;

/**
 * Created by andyliu on 16-7-4.
 */
public class DBProvider extends ContentProvider {


    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private static final int CLOCKS = 1, CLOCK_ID = 2;

    private static final UriMatcher URI_MATCHER;
    private static final String UNKNOWN_URI_LOG = "Unknown URI ";

    static {
        // Create and initialize URI matcher.
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        URI_MATCHER.addURI(DBManager.AUTHORITY, ClockBean.CLOCK_BEAN_TABLE, CLOCKS);
        URI_MATCHER.addURI(DBManager.AUTHORITY, ClockBean.CLOCK_BEAN_TABLE + "/#", CLOCK_ID);
    }

    public static final String[] CLOCK_PROJECTION = new String[]{ClockBean._ID, ClockBean.PHONE_NUMBER, ClockBean.CLOCK_TIME};

    public DBProvider() {
        if (dbHelper == null)
            dbHelper = new DBHelper(App.getInstance());
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
        values.put(DBHelper.URL, url);
        values.put(DBHelper.DATA, data);
        values.put(DBHelper.TIME, System.currentTimeMillis());
        db.replace(DBHelper.CACHE, null, values);
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
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.CACHE + " WHERE URL = ?", new String[]{url});
        while (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex(DBHelper.DATA));
        }
        cursor.close();
        db.close();
        return result;
    }

    @Override
    public boolean onCreate() {
        if (dbHelper == null)
            dbHelper = new DBHelper(App.getInstance());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String finalSortOrder = sortOrder;
        String[] finalSelectionArgs = selectionArgs;
        String finalGrouping = null;
        String finalHaving = null;
        int type = URI_MATCHER.match(uri);

        Uri regUri = uri;

        // Security check to avoid project of invalid fields or lazy projection
        List<String> possibles = getPossibleFieldsForType(type);
        if (possibles == null) {
            throw new SecurityException("You are asking wrong values " + type);
        }
        checkProjection(possibles, projection);
        checkSelection(possibles, selection);

        Cursor c;
        long id;
        switch (type) {
            case CLOCKS:
                qb.setTables(ClockBean.CLOCK_BEAN_TABLE);
                if (sortOrder == null) {
                    finalSortOrder = ClockBean.CLOCK_TIME + " ASC";
                }
                break;
            case CLOCK_ID:
                qb.setTables(ClockBean.CLOCK_BEAN_TABLE);
                qb.appendWhere(ClockBean._ID + "=?");
                finalSelectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs,
                        new String[]{uri.getLastPathSegment()});
                break;

            default:
                throw new IllegalArgumentException("UNKNOW " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        c = qb.query(db, projection, selection, finalSelectionArgs, finalGrouping, finalHaving, finalSortOrder);

        c.setNotificationUri(getContext().getContentResolver(), regUri);
        return c;
    }


    private static List<String> getPossibleFieldsForType(int type) {
        List<String> possibles = null;
        switch (type) {
            case CLOCKS:
            case CLOCK_ID:
                possibles = Arrays.asList(CLOCK_PROJECTION);
                break;

            default:
        }
        return possibles;
    }

    private static void checkSelection(List<String> possibles, String selection) {
        if (selection != null) {
            String cleanSelection = selection.toLowerCase();
            for (String field : possibles) {
                cleanSelection = cleanSelection.replace(field, "");
            }

            cleanSelection = cleanSelection.replaceAll("not like", "");
            cleanSelection = cleanSelection.replaceAll("like", "");

            cleanSelection = cleanSelection.replaceAll(" in \\([0-9 ,]+\\)", "");
            cleanSelection = cleanSelection.replaceAll(" and ", "");
            cleanSelection = cleanSelection.replaceAll(" or ", "");
            cleanSelection = cleanSelection.replaceAll("[0-9]+", "");
            cleanSelection = cleanSelection.replaceAll("[=? ]", "");
            cleanSelection = cleanSelection.replaceAll(",", "");
            cleanSelection = cleanSelection.replaceAll("notin", "");

            cleanSelection = cleanSelection.replaceAll("\\(", "");
            cleanSelection = cleanSelection.replaceAll("\\)", "");

            if (cleanSelection.length() > 0) {
                throw new SecurityException("You are selecting wrong thing " + cleanSelection);
            }
        }
    }

    private static void checkProjection(List<String> possibles, String[] projection) {
        if (projection != null) {
            // Ensure projection is valid
//			for (String proj : projection) {
//				proj = proj.replaceAll(" AS [a-zA-Z0-9_]+$", "");
//				if (!possibles.contains(proj)) {
//					throw new SecurityException("You are asking wrong values " + proj);
//				}
//			}
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case CLOCKS:
                return ClockBean.CLOCK_ITEMS_TYPE;
            case CLOCK_ID:
                return ClockBean.CLOCK_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        int matched = URI_MATCHER.match(uri);
        String matchedTable = null;
        Uri baseInsertedUri = null;
        switch (matched) {
            case CLOCKS:
            case CLOCK_ID:
                matchedTable = ClockBean.CLOCK_BEAN_TABLE;
                baseInsertedUri = ClockBean.ITEM_URI;
                break;
            default:
                break;
        }

        if (matchedTable == null) {
            throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }

        ContentValues values;

        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long rowId = db.insert(matchedTable, null, values);
// If the insert succeeded, the row ID exists.
        if (rowId >= 0) {
            // TODO : for inserted account register it here

            Uri retUri = ContentUris.withAppendedId(baseInsertedUri, rowId);
            getContext().getContentResolver().notifyChange(retUri, null);
            return retUri;
        }

        throw new SQLException("Failed to insert row into " + uri);

    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String finalWhere;
        int count = 0;
        int matched = URI_MATCHER.match(uri);
        Uri regUri = uri;

        List<String> possibles = getPossibleFieldsForType(matched);
        checkSelection(possibles, where);

        ArrayList<Long> oldRegistrationsAccounts = null;

        switch (matched) {
            case CLOCKS:
                count = db.delete(ClockBean.CLOCK_BEAN_TABLE, where, whereArgs);
                break;
            case CLOCK_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(ClockBean._ID + " = " + ContentUris.parseId(uri),
                        where);
                count = db.delete(ClockBean.CLOCK_BEAN_TABLE, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }
        getContext().getContentResolver().notifyChange(regUri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        String finalWhere;
        int matched = URI_MATCHER.match(uri);

        List<String> possibles = getPossibleFieldsForType(matched);
        checkSelection(possibles, where);

        switch (matched) {
            case CLOCKS:
                count = db.update(ClockBean.CLOCK_BEAN_TABLE, values, where, whereArgs);
                break;
            case CLOCK_ID:
                finalWhere = DatabaseUtilsCompat.concatenateWhere(ClockBean._ID + " = " + ContentUris.parseId(uri),
                        where);
                count = db.update(ClockBean.CLOCK_BEAN_TABLE, values, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
