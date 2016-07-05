package www.ufcus.com.beans;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.xiaopan.java.util.DateTimeUtils;
import www.ufcus.com.app.App;
import www.ufcus.com.utils.PreUtils;
import www.ufcus.com.utils.Utils;

/**
 * 打卡显示类
 * Created by andyliu on 16-7-4.
 */

public class ClockDetailResult {

    public static final String AS_FIRST_TIME = "as_first_time";

    public static final String AS_LAST_TIME = "as_last_time";

    public int id;
    public String firstTime;
    public String lastTime;
    public String workTime;
    public String phoneNumber;
    /**
     * 工作状态
     * 1.正常
     * 2.异常
     */
    public String workStatus;

    //12日
    public String day;
    //星期一
    public String week;

    public ClockDetailResult() {

    }


    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public static ArrayList<ClockDetailResult> getBeans(Cursor cursor) {
        ArrayList<ClockDetailResult> clockTotalResultsList = new ArrayList<ClockDetailResult>();
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                ClockDetailResult cdr = new ClockDetailResult();
                int id = cursor.getInt(cursor.getColumnIndex(ClockBean._ID));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ClockBean.PHONE_NUMBER));
                long firstTime = cursor.getLong(cursor.getColumnIndex(ClockDetailResult.AS_FIRST_TIME));
                long lastTime = cursor.getLong(cursor.getColumnIndex(ClockDetailResult.AS_LAST_TIME));

                int day = DateTimeUtils.getDay(lastTime);
                String dayStr = (Utils.getNumberDigit(day) == 1 ? "0" + day : String.valueOf(day)) + "日";

                //计算星期
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(lastTime));
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                String week = "星期" + DateTimeUtils.getWeekChineseName(dayOfWeek);

                double workHours = (lastTime - firstTime) / 1000 / 60 / 60;
                cdr.setId(id);
                cdr.setPhoneNumber(phoneNumber);
                cdr.setFirstTime(Utils.getTime(firstTime));
                cdr.setLastTime(Utils.getTime(lastTime));
                cdr.setDay(dayStr);
                cdr.setWeek(week);
                int nomalWorkHours = PreUtils.getInt(App.getInstance(), "work_time", 0);
                if (nomalWorkHours < workHours) {
                    cdr.setWorkStatus("正常");
                } else {
                    cdr.setWorkStatus("异常");
                }
                cdr.setWorkTime(workHours + "小时");
                clockTotalResultsList.add(cdr);
            }
        }

        return clockTotalResultsList;
    }

}