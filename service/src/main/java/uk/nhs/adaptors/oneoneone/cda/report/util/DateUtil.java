package uk.nhs.adaptors.oneoneone.cda.report.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

@UtilityClass
public class DateUtil {

    private static final String[] FORMATS = {
            "yyyyMMddHHmmX"
    };

    public static Date parse(String date) {
        try {
            return DateUtils.parseDate(date, FORMATS);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

}
