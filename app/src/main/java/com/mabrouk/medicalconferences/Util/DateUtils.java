package com.mabrouk.medicalconferences.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by User on 12/2/2016.
 */

public class DateUtils {
    public static String getDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        return format.format(date);
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }
}
