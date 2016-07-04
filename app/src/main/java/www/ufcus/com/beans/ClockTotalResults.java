package www.ufcus.com.beans;

import android.content.ContentResolver;
import android.net.Uri;

import www.ufcus.com.db.DBManager;

/**
 * 打卡显示类
 * Created by andyliu on 16-7-4.
 */

public class ClockTotalResults {

    public int id;
    public long firstTime;
    public long lastTime;
    public long workTime;
    public String phoneNumber;
    /**
     * 工作状态
     * 1.正常
     * 2.不正常
     */
    public int workStatus;

    //12日
    public String day;
    //星期一
    public String week;

    public ClockTotalResults() {

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

    public long getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(long firstTime) {
        this.firstTime = firstTime;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(long workTime) {
        this.workTime = workTime;
    }

    public int getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(int workStatus) {
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

}