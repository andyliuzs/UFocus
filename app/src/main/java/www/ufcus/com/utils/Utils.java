package www.ufcus.com.utils;

import me.xiaopan.java.util.DateTimeUtils;

/**
 * Created by andyliu on 16-7-5.
 */
public class Utils {

    /**
     * 获取指定日期
     * 返回 类似  2013年11月12日
     *
     * @return
     */
    public static String getDate(long dateTime) {

        int month = DateTimeUtils.getMonth(dateTime);
        String monthStr = getNumberDigit(month) == 1 ? "0" + month : String.valueOf(month);

        int day = DateTimeUtils.getDay(dateTime);
        String dayStr = getNumberDigit(day) == 1 ? "0" + day : String.valueOf(day);

        return DateTimeUtils.getYear(dateTime) + "年" + monthStr + "月" + dayStr + "日";
    }

    /***
     * 获取指定时间
     * 返回类似 11：22：32
     *
     * @return
     */
    public static String getTime(long dateTime) {
        int hour = DateTimeUtils.getHourBy24(dateTime);
        String hourStr = getNumberDigit(hour) == 1 ? "0" + hour : String.valueOf(hour);

        int minute = DateTimeUtils.getMinute(dateTime);
        String minuteStr = getNumberDigit(minute) == 1 ? "0" + minute : String.valueOf(minute);

        int second = DateTimeUtils.getSecond(dateTime);
        String secondStr = getNumberDigit(second) == 1 ? "0" + second : String.valueOf(second);

        return   hourStr + ":" + minuteStr + ":" + secondStr;
    }

    /**
     * 判断是几位数
     *
     * @param number
     * @return
     */
    public static int getNumberDigit(int number) {
        int n = 0;
        while (number != 0) {
            number /= 10;
            n++;
        }

        return n;
    }
}
