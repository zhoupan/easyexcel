package com.alibaba.excel.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Date utils
 *
 * @author Jiaju Zhuang
 **/
public class DateUtils {
    /**
     * Is a cache of dates
     */
    private static final ThreadLocal<Map<Integer, Boolean>> DATE_THREAD_LOCAL =
        new ThreadLocal<Map<Integer, Boolean>>();
    /**
     * Is a cache of dates
     */
    private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_FORMAT_THREAD_LOCAL =
        new ThreadLocal<Map<String, SimpleDateFormat>>();

    /**
     * The following patterns are used in {@link #isADateFormat(Integer, String)}
     */
    private static final Pattern date_ptrn1 = Pattern.compile("^\\[\\$\\-.*?\\]");
    private static final Pattern date_ptrn2 = Pattern.compile("^\\[[a-zA-Z]+\\]");
    private static final Pattern date_ptrn3a = Pattern.compile("[yYmMdDhHsS]");
    // add "\u5e74 \u6708 \u65e5" for Chinese/Japanese date format:2017 \u5e74 2 \u6708 7 \u65e5
    private static final Pattern date_ptrn3b =
        Pattern.compile("^[\\[\\]yYmMdDhHsS\\-T/\u5e74\u6708\u65e5,. :\"\\\\]+0*[ampAMP/]*$");
    // elapsed time patterns: [h],[m] and [s]
    private static final Pattern date_ptrn4 = Pattern.compile("^\\[([hH]+|[mM]+|[sS]+)\\]");
    // for format which start with "[DBNum1]" or "[DBNum2]" or "[DBNum3]" could be a Chinese date
    private static final Pattern date_ptrn5 = Pattern.compile("^\\[DBNum(1|2|3)\\]");
    // for format which start with "年" or "月" or "日" or "时" or "分" or "秒" could be a Chinese date
    private static final Pattern date_ptrn6 = Pattern.compile("(年|月|日|时|分|秒)+");

    public static final String DATE_FORMAT_10 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_14 = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_17 = "yyyyMMdd HH:mm:ss";
    public static final String DATE_FORMAT_19 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_19_FORWARD_SLASH = "yyyy/MM/dd HH:mm:ss";
    private static final String MINUS = "-";

    private DateUtils() {}

    /**
     * convert string to date
     *
     * @param dateString
     * @param dateFormat
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateString, String dateFormat) throws ParseException {
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = switchDateFormat(dateString);
        }
        return getCacheDateFormat(dateFormat).parse(dateString);
    }

    /**
     * convert string to date
     *
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateString) throws ParseException {
        return parseDate(dateString, switchDateFormat(dateString));
    }

    /**
     * switch date format
     *
     * @param dateString
     * @return
     */
    private static String switchDateFormat(String dateString) {
        int length = dateString.length();
        switch (length) {
            case 19:
                if (dateString.contains(MINUS)) {
                    return DATE_FORMAT_19;
                } else {
                    return DATE_FORMAT_19_FORWARD_SLASH;
                }
            case 17:
                return DATE_FORMAT_17;
            case 14:
                return DATE_FORMAT_14;
            case 10:
                return DATE_FORMAT_10;
            default:
                throw new IllegalArgumentException("can not find date format for：" + dateString);
        }
    }

    /**
     * Format date
     * <p>
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, null);
    }

    /**
     * Format date
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(Date date, String dateFormat) {
        if (date == null) {
            return "";
        }
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = DATE_FORMAT_19;
        }
        return getCacheDateFormat(dateFormat).format(date);
    }

    private static DateFormat getCacheDateFormat(String dateFormat) {
        Map<String, SimpleDateFormat> dateFormatMap = DATE_FORMAT_THREAD_LOCAL.get();
        if (dateFormatMap == null) {
            dateFormatMap = new HashMap<String, SimpleDateFormat>();
            DATE_FORMAT_THREAD_LOCAL.set(dateFormatMap);
        } else {
            SimpleDateFormat dateFormatCached = dateFormatMap.get(dateFormat);
            if (dateFormatCached != null) {
                return dateFormatCached;
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        dateFormatMap.put(dateFormat, simpleDateFormat);
        return simpleDateFormat;
    }

    /**
     * Determine if it is a date format.
     *
     * @param formatIndex
     * @param formatString
     * @return
     */
    public static boolean isADateFormat(Integer formatIndex, String formatString) {
        if (formatIndex == null) {
            return false;
        }
        Map<Integer, Boolean> isDateCache = DATE_THREAD_LOCAL.get();
        if (isDateCache == null) {
            isDateCache = new HashMap<Integer, Boolean>();
            DATE_THREAD_LOCAL.set(isDateCache);
        } else {
            Boolean isDateCachedData = isDateCache.get(formatIndex);
            if (isDateCachedData != null) {
                return isDateCachedData;
            }
        }
        boolean isDate = isADateFormatUncached(formatIndex, formatString);
        isDateCache.put(formatIndex, isDate);
        return isDate;
    }

    /**
     * Determine if it is a date format.
     *
     * @param formatIndex
     * @param formatString
     * @return
     */
    public static boolean isADateFormatUncached(Integer formatIndex, String formatString) {
        // First up, is this an internal date format?
        if (isInternalDateFormat(formatIndex)) {
            return true;
        }
        if (StringUtils.isEmpty(formatString)) {
            return false;
        }
        String fs = formatString;
        final int length = fs.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = fs.charAt(i);
            if (i < length - 1) {
                char nc = fs.charAt(i + 1);
                if (c == '\\') {
                    switch (nc) {
                        case '-':
                        case ',':
                        case '.':
                        case ' ':
                        case '\\':
                            // skip current '\' and continue to the next char
                            continue;
                    }
                } else if (c == ';' && nc == '@') {
                    i++;
                    // skip ";@" duplets
                    continue;
                }
            }
            sb.append(c);
        }
        fs = sb.toString();

        // short-circuit if it indicates elapsed time: [h], [m] or [s]
        if (date_ptrn4.matcher(fs).matches()) {
            return true;
        }
        // If it starts with [DBNum1] or [DBNum2] or [DBNum3]
        // then it could be a Chinese date
        fs = date_ptrn5.matcher(fs).replaceAll("");
        // If it starts with [$-...], then could be a date, but
        // who knows what that starting bit is all about
        fs = date_ptrn1.matcher(fs).replaceAll("");
        // If it starts with something like [Black] or [Yellow],
        // then it could be a date
        fs = date_ptrn2.matcher(fs).replaceAll("");
        // You're allowed something like dd/mm/yy;[red]dd/mm/yy
        // which would place dates before 1900/1904 in red
        // For now, only consider the first one
        final int separatorIndex = fs.indexOf(';');
        if (0 < separatorIndex && separatorIndex < fs.length() - 1) {
            fs = fs.substring(0, separatorIndex);
        }

        // Ensure it has some date letters in it
        // (Avoids false positives on the rest of pattern 3)
        if (!date_ptrn3a.matcher(fs).find()) {
            return false;
        }

        // If we get here, check it's only made up, in any case, of:
        // y m d h s - \ / , . : [ ] T
        // optionally followed by AM/PM
        boolean result = date_ptrn3b.matcher(fs).matches();
        if (result) {
            return true;
        }
        result = date_ptrn6.matcher(fs).find();
        return result;
    }

    /**
     * Given a format ID this will check whether the format represents an internal excel date format or not.
     *
     * @see #isADateFormat(Integer, String)
     */
    public static boolean isInternalDateFormat(int format) {
        switch (format) {
            // Internal Date Formats as described on page 427 in
            // Microsoft Excel Dev's Kit...
            // 14-22
            case 0x0e:
            case 0x0f:
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
            case 0x14:
            case 0x15:
            case 0x16:
                // 27-36
            case 0x1b:
            case 0x1c:
            case 0x1d:
            case 0x1e:
            case 0x1f:
            case 0x20:
            case 0x21:
            case 0x22:
            case 0x23:
            case 0x24:
                // 45-47
            case 0x2d:
            case 0x2e:
            case 0x2f:
                // 50-58
            case 0x32:
            case 0x33:
            case 0x34:
            case 0x35:
            case 0x36:
            case 0x37:
            case 0x38:
            case 0x39:
            case 0x3a:
                return true;
        }
        return false;
    }

    public static void removeThreadLocalCache() {
        DATE_THREAD_LOCAL.remove();
        DATE_FORMAT_THREAD_LOCAL.remove();
    }

    /**
     * Adds a number of years to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     */
    public static Date addYears(final Date date, final int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Adds a number of months to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     */
    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Adds a number of weeks to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     */
    public static Date addWeeks(final Date date, final int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Adds a number of days to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     */
    public static Date addDays(final Date date, final int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Adds a number of hours to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     */
    public static Date addHours(final Date date, final int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Adds a number of minutes to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     */
    public static Date addMinutes(final Date date, final int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Adds a number of seconds to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     */
    public static Date addSeconds(final Date date, final int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Adds a number of milliseconds to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     */
    public static Date addMilliseconds(final Date date, final int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Adds to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param calendarField
     *            the calendar field to add to
     * @param amount
     *            the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     */
    private static Date add(final Date date, final int calendarField, final int amount) {
        validateDateNotNull(date);
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    // -----------------------------------------------------------------------
    /**
     * Sets the years field to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to set
     * @return a new {@code Date} set with the specified value
     * @since 2.4
     */
    public static Date setYears(final Date date, final int amount) {
        return set(date, Calendar.YEAR, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Sets the months field to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to set
     * @return a new {@code Date} set with the specified value
     * @since 2.4
     */
    public static Date setMonths(final Date date, final int amount) {
        return set(date, Calendar.MONTH, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Sets the day of month field to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to set
     * @return a new {@code Date} set with the specified value
     * @since 2.4
     */
    public static Date setDays(final Date date, final int amount) {
        return set(date, Calendar.DAY_OF_MONTH, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Sets the hours field to a date returning a new object. Hours range from 0-23. The original
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to set
     * @return a new {@code Date} set with the specified value {@code Date} is unchanged.
     * @since 2.4
     */
    public static Date setHours(final Date date, final int amount) {
        return set(date, Calendar.HOUR_OF_DAY, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Sets the minute field to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to set
     * @return a new {@code Date} set with the specified value
     * @since 2.4
     */
    public static Date setMinutes(final Date date, final int amount) {
        return set(date, Calendar.MINUTE, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Sets the seconds field to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to set
     * @return a new {@code Date} set with the specified value
     * @since 2.4
     */
    public static Date setSeconds(final Date date, final int amount) {
        return set(date, Calendar.SECOND, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Sets the milliseconds field to a date returning a new object. The original {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param amount
     *            the amount to set
     * @return a new {@code Date} set with the specified value
     * @since 2.4
     */
    public static Date setMilliseconds(final Date date, final int amount) {
        return set(date, Calendar.MILLISECOND, amount);
    }

    // -----------------------------------------------------------------------
    /**
     * Sets the specified field to a date returning a new object. This does not use a lenient calendar. The original
     * {@code Date} is unchanged.
     *
     * @param date
     *            the date, not null
     * @param calendarField
     *            the {@code Calendar} field to set the amount to
     * @param amount
     *            the amount to set
     * @return a new {@code Date} set with the specified value
     * @since 2.4
     */
    private static Date set(final Date date, final int calendarField, final int amount) {
        validateDateNotNull(date);
        // getInstance() returns a new object, so this method is thread safe.
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(calendarField, amount);
        return c.getTime();
    }

    /**
     * Validate date not null.
     *
     * @param date
     *            the date
     */
    private static void validateDateNotNull(final Date date) {
        CoreUtils.checkNotNull(date, "The date must not be null");
    }

    /**
     * Create a new date with this year, month and day.
     *
     * @param year
     *            The year.
     * @param month
     *            The month (1-12).
     * @param day
     *            The day (1-31).
     * @return The date.
     */
    public static Date toDate(int year, int month, int day) {
        return new GregorianCalendar(year, month - 1, day).getTime();
    }

    /**
     * Checks if two date objects are on the same day ignoring time.
     *
     * <p>
     * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true. 28 Mar 2002 13:45 and 12 Mar 2002 13:45 would return
     * false.
     *
     * @param date1
     *            the first date, not altered, not null
     * @param date2
     *            the second date, not altered, not null
     * @return true if they represent the same day
     * @since 2.1
     */
    public static boolean isSameDay(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw nullDateIllegalArgumentException();
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    /**
     * Checks if two calendar objects are on the same day ignoring time.
     *
     * <p>
     * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true. 28 Mar 2002 13:45 and 12 Mar 2002 13:45 would return
     * false.
     *
     * @param cal1
     *            the first calendar, not altered, not null
     * @param cal2
     *            the second calendar, not altered, not null
     * @return true if they represent the same day
     * @since 2.1
     */
    public static boolean isSameDay(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw nullDateIllegalArgumentException();
        }
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
            && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // -----------------------------------------------------------------------
    /**
     * Checks if two date objects represent the same instant in time.
     *
     * <p>
     * This method compares the long millisecond time of the two objects.
     *
     * @param date1
     *            the first date, not altered, not null
     * @param date2
     *            the second date, not altered, not null
     * @return true if they represent the same millisecond instant
     * @since 2.1
     */
    public static boolean isSameInstant(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw nullDateIllegalArgumentException();
        }
        return date1.getTime() == date2.getTime();
    }

    /**
     * Checks if two calendar objects represent the same instant in time.
     *
     * <p>
     * This method compares the long millisecond time of the two objects.
     *
     * @param cal1
     *            the first calendar, not altered, not null
     * @param cal2
     *            the second calendar, not altered, not null
     * @return true if they represent the same millisecond instant
     * @since 2.1
     */
    public static boolean isSameInstant(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw nullDateIllegalArgumentException();
        }
        return cal1.getTime().getTime() == cal2.getTime().getTime();
    }

    /**
     * Null date illegal argument exception.
     *
     * @return the illegal argument exception
     */
    private static IllegalArgumentException nullDateIllegalArgumentException() {
        return new IllegalArgumentException("The date must not be null");
    }
}
