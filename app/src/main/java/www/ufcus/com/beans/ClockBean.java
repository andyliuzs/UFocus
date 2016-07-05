package www.ufcus.com.beans;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import me.xiaopan.java.util.DateTimeUtils;
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
    public static final String GROUP_BY = "group_by";
    public static final String CLOCK_ITEMS_TYPE = DBManager.BASE_DIR_TYPE + ".clockbean";
    public static final String CLOCK_ITEM_TYPE = DBManager.BASE_ITEM_TYPE + ".clockbean";
    public static final String[] PROJECTION = new String[]{_ID, PHONE_NUMBER, CLOCK_TIME, GROUP_BY};
    public static final Uri ITEMS_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + DBManager.AUTHORITY + "/"
            + CLOCK_BEAN_TABLE);
    public static final Uri ITEM_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + DBManager.AUTHORITY + "/"
            + CLOCK_BEAN_TABLE + "/");

    private int id;
    private String phoneNumber;
    private long clockTime;
    //为分组 格式(2014年11月11日)
    private String groupBy;

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }


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


    public long getClockTime() {
        return clockTime;
    }

    public void setClockTime(long clockTime) {
        this.clockTime = clockTime;
    }


    public static ArrayList<ClockBean> getBeans(Cursor cursor) {
        ArrayList<ClockBean> list = new ArrayList<ClockBean>();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            ClockBean bean = new ClockBean();
            bean.setId(cursor.getInt(cursor.getColumnIndex(ClockBean._ID)));
            bean.setPhoneNumber(cursor.getString(cursor.getColumnIndex(ClockBean.PHONE_NUMBER)));
            bean.setClockTime(cursor.getLong(cursor.getColumnIndex(ClockBean.CLOCK_TIME)));
            bean.setGroupBy(cursor.getString(cursor.getColumnIndex(ClockBean.GROUP_BY)));
            list.add(bean);
//            Logger.v("bean max" + cursor.getLong(cursor.getColumnIndex("max")) + "," + "min" + cursor.getLong(cursor.getColumnIndex("min")) + ",maxtime" + cursor.getLong(cursor.getColumnIndex("maxtime"))/1000/60/60);
            Logger.v("bean" + bean.toString());
        }

        return list;
    }

    @Override
    public String toString() {
        return "id" + id + ",phone->" + phoneNumber + ",time" + clockTime + ",group by" + groupBy;
    }
}