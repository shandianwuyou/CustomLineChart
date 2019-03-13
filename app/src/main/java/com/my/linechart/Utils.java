package com.my.linechart;

import android.content.Context;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 创建者 ：赵鹏   时间：2019/3/13
 */
public class Utils {
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String formatTimeToMD(long time){
        Date date = new Date(time * 1000);
        SimpleDateFormat sd = new SimpleDateFormat("MM-dd");
        return sd.format(date);
    }

    public static String stringYMD2(String str){
        if(!TextUtils.isEmpty(str)){
            long time = Long.parseLong(str);
            Date date = new Date(time * 1000);
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            return sd.format(date);
        }else{
            return "";
        }
    }

    public static int switchIntValue(String value) {
        try {
            return Integer.valueOf(value);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static long switchLongValue(String value) {
        try {
            return Long.valueOf(value);
        } catch (Exception ex) {
            return 0L;
        }
    }

}
