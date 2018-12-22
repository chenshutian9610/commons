package org.tree.commons.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author er_dong_chen
 * @date 18-12-14
 */
public class DateUtils {
    private static DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getStandardFormat(Date date) {
//        return String.format("%tF %tT", date, date);
        return formatter.format(date);
    }

    public static Date getByString(String dateString) {
        Date date=null;
        try{
            date=formatter.parse(dateString);
        }finally {
            return date;
        }
    }

    public static void main(String args[]){
        System.out.println(getStandardFormat(new Date()));
        System.out.println(getStandardFormat(getByString("1996-11-24 03:03:05.4")));
    }
}
