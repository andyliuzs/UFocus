package www.ufcus.com.beans;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

import www.ufcus.com.db.DBManager;

/**
 * 打卡实体类
 * Created by andyliu on 16-7-4.
 */

public class ClockBean {
    public static final String CLOCK_BEAN_TABLE = "clock_bean_table";
    public static final String _ID = "_id";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String CLOCK_TIME = "clock_time";

    public static final String CLOCK_ITEMS_TYPE = DBManager.BASE_DIR_TYPE + ".clockbean";
    public static final String CLOCK_ITEM_TYPE = DBManager.BASE_ITEM_TYPE + ".clockbean";
    public static final String[] PROJECTION = new String[]{_ID, PHONE_NUMBER, CLOCK_TIME};
    public static final Uri ITEMS_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + DBManager.AUTHORITY + "/"
            + CLOCK_BEAN_TABLE);
    public static final Uri ITEM_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + DBManager.AUTHORITY + "/"
            + CLOCK_BEAN_TABLE + "/");

    private int id;
    private String phone;
    private String phoneNumber;
    private long clockTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getClockTime() {
        return clockTime;
    }

    public void setClockTime(long clockTime) {
        this.clockTime = clockTime;
    }


    public static ArrayList<ClockBean> getBeans(Cursor cursor) {
        ArrayList<ClockBean> list = new ArrayList<ClockBean>();
        while (cursor.moveToNext()) {
            ClockBean bean = new ClockBean();
            bean.setId(cursor.getInt(cursor.getColumnIndex(ClockBean._ID)));
            bean.setPhone(cursor.getString(cursor.getColumnIndex(ClockBean.PHONE_NUMBER)));
            bean.setClockTime(cursor.getLong(cursor.getColumnIndex(ClockBean.CLOCK_TIME)));
            list.add(bean);
        }
        return list;
    }

}