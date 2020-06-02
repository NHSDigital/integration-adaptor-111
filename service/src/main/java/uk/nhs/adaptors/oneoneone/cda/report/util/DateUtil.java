package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.text.ParseException;
import java.util.Date;

import lombok.experimental.UtilityClass;

import org.apache.commons.lang3.time.DateUtils;

@UtilityClass
public class DateUtil {

  private static final String[] FORMATS = {
      "yyyyMMddHHmmssZ",
      "yyyyMMddHHmmX",
      "yyyyMMdd"
  };

  public static Date parse(String date) {
    try {
      return DateUtils.parseDate(date, FORMATS);
    } catch (ParseException e) {
      throw new IllegalStateException(e);
    }
  }

}
