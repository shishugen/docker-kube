package com.jeesite.modules.kube.utlis;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtlis {

    private static final String FORMAT ="yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式字符串转换成时间戳
     * @param date_str 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String dateTimeStamp(String date_str,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime()/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
/**
     * 日期格式字符串转换成时间戳
     * @param date_str 字符串日期
     * @return
     */
    public static Date dateTimeStamp(String date_str){
        try {
            return dateTimeStamp1(date_str,FORMAT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期格式字符串转换成时间戳
     * @param date_str 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date dateTimeStamp1(String date_str, String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date_str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
