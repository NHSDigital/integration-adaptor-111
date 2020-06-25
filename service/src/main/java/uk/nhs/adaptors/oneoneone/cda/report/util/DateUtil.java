package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.text.ParseException;
import java.util.Date;

import lombok.experimental.UtilityClass;

import org.apache.commons.lang3.time.DateUtils;

@UtilityClass
public class DateUtil {

  static final String ISO_DATETIME_FORMAT = "yyyyMMddHHmmX";
  static final String ISO_DATE_FORMAT     = "yyyyMMdd";

  private static final String[] FORMATS = {
      "yyyyMMddHHmmX",
          "yyyyMMdd"
  };

  public static Date parseISODate(String date) {
    return parseDate(date, ISO_DATE_FORMAT);
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

  private Date parseDate(String date, String format) {
    try {
      return DateUtils.parseDate(date, format);
    } catch (ParseException e) {
      throw new IllegalStateException(e);
    }
  }
}
