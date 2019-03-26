package org.tree.commons.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author er_dong_chen
 * @date 18-12-14
 */
public class DateUtils {
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String format(Date date) {
        return formatter.format(date);
    }

    public static Date parse(String dateString) {
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
}
