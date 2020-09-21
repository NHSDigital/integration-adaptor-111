package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtil {

    static final String ISO_DATETIME_FORMAT = "yyyyMMddHHmmX";
    static final String ISO_DATE_FORMAT = "yyyyMMdd";

    private static final String[] FORMATS = {
        "yyyyMMddHHmmX",
        "yyyyMMddHHmmZ",
        "yyyyMMdd",
    };

    public static Date parseISODate(String date) {
        return parseDate(date, ISO_DATE_FORMAT);
    }

    private Date parseDate(String date, String format) {
        try {
            return DateUtils.parseDate(date, format);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Date parseISODateTime(String date) {
        return parseDate(date, ISO_DATETIME_FORMAT);
    }

    public static Date parse(String date) {
        try {
            return DateUtils.parseDate(date, FORMATS);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    private static final String INPUT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
    private static final String OUTPUT_PATTERN = "yyyyMMdd";

    public static Date parsePathwaysDate(String dateStr) {
        Date finalDate = null;
        if (StringUtils.isNotBlank(dateStr)) {
            SimpleDateFormat sdfInput = new SimpleDateFormat(INPUT_PATTERN);
            SimpleDateFormat sdfOutput = new SimpleDateFormat(OUTPUT_PATTERN);

            try {
                Date date = sdfInput.parse(dateStr);
                String value = sdfOutput.format(date);
                finalDate = sdfOutput.parse(value);
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }
        return finalDate;
    }
}
