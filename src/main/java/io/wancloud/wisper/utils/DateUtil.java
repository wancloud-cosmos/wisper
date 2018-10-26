package io.wancloud.wisper.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static final String SIMPLE = "yyyy-MM-dd HH:mm:ss";

    private static final DateFormat getFormat(String format) {
        return new SimpleDateFormat(format);
    }

    public static final String formatDate(Date date, String formater) {
        if (date == null || formater == null) {
            throw new IllegalArgumentException(String.format("illegal argument date:%s with formater:%s", date, formater));
        }

        return getFormat(formater).format(date);
    }

    public static final Date parseDate(String date, String formater) {
        if (date == null || formater == null) {
            throw new IllegalArgumentException(String.format("illegal argument date:%s with formater:%s", date, formater));
        }

        try {
            return getFormat(formater).parse(date);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("date parse error, illegal argument date:%s with formater:%s", date, formater), e);
        }
    }

}
