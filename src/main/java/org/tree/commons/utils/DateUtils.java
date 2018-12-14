package org.tree.commons.utils;

import java.util.Date;

/**
 * @author er_dong_chen
 * @date 18-12-14
 */
public class DateUtils {
    public static String getStandardFormat(Date date) {
        return String.format("%tF %tT", date, date);
    }

    public static void main(String args[]) {
        System.out.println(getStandardFormat(new Date()));
    }
}
