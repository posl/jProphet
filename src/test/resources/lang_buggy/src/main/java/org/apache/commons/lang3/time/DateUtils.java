















package org.apache.commons.lang3.time;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;



















public class DateUtils {





    public static final long MILLIS_PER_SECOND = 1000;




    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;




    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;




    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;





    public static final int SEMI_MONTH = 1001;

    private static final int[][] fields = {
            {Calendar.MILLISECOND},
            {Calendar.SECOND},
            {Calendar.MINUTE},
            {Calendar.HOUR_OF_DAY, Calendar.HOUR},
            {Calendar.DATE, Calendar.DAY_OF_MONTH, Calendar.AM_PM 
                
            },
            {Calendar.MONTH, DateUtils.SEMI_MONTH},
            {Calendar.YEAR},
            {Calendar.ERA}};




    public static final int RANGE_WEEK_SUNDAY = 1;



    public static final int RANGE_WEEK_MONDAY = 2;



    public static final int RANGE_WEEK_RELATIVE = 3;



    public static final int RANGE_WEEK_CENTER = 4;



    public static final int RANGE_MONTH_SUNDAY = 5;



    public static final int RANGE_MONTH_MONDAY = 6;





    private static final int MODIFY_TRUNCATE = 0;




    private static final int MODIFY_ROUND = 1;




    private static final int MODIFY_CEILING = 2;









    public DateUtils() {
        super();
    }

    













    public static boolean isSameDay(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }














    public static boolean isSameDay(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    











    public static boolean isSameInstant(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return date1.getTime() == date2.getTime();
    }












    public static boolean isSameInstant(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return cal1.getTime().getTime() == cal2.getTime().getTime();
    }

    












    public static boolean isSameLocalTime(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(Calendar.MILLISECOND) == cal2.get(Calendar.MILLISECOND) &&
                cal1.get(Calendar.SECOND) == cal2.get(Calendar.SECOND) &&
                cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE) &&
                cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.getClass() == cal2.getClass());
    }

    














    public static Date parseDate(final String str, final String... parsePatterns) throws ParseException {
        return parseDate(str, null, parsePatterns);
    }
    
    


















    public static Date parseDate(final String str, final Locale locale, final String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, locale, parsePatterns, true);
    }    

  















    public static Date parseDateStrictly(final String str, final String... parsePatterns) throws ParseException {
        return parseDateStrictly(str, null, parsePatterns);
    }



















    public static Date parseDateStrictly(final String str, final Locale locale, final String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, null, parsePatterns, false);
    }    


















    private static Date parseDateWithLeniency(
            final String str, final Locale locale, final String[] parsePatterns, final boolean lenient) throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
        
        SimpleDateFormat parser;
        if (locale == null) {
            parser = new SimpleDateFormat();
        } else {
            parser = new SimpleDateFormat("", locale);
        }
        
        parser.setLenient(lenient);
        final ParsePosition pos = new ParsePosition(0);
        for (final String parsePattern : parsePatterns) {

            String pattern = parsePattern;

            
            if (parsePattern.endsWith("ZZ")) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }
            
            parser.applyPattern(pattern);
            pos.setIndex(0);

            String str2 = str;
            
            if (parsePattern.endsWith("ZZ")) {
                str2 = str.replaceAll("([-+][0-9][0-9]):([0-9][0-9])$", "$1$2"); 
            }

            final Date date = parser.parse(str2, pos);
            if (date != null && pos.getIndex() == str2.length()) {
                return date;
            }
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }

    









    public static Date addYears(final Date date, final int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    









    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    









    public static Date addWeeks(final Date date, final int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    









    public static Date addDays(final Date date, final int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    









    public static Date addHours(final Date date, final int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    









    public static Date addMinutes(final Date date, final int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    









    public static Date addSeconds(final Date date, final int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    









    public static Date addMilliseconds(final Date date, final int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    










    private static Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }
    
    










    public static Date setYears(final Date date, final int amount) {
        return set(date, Calendar.YEAR, amount);
    }

    










    public static Date setMonths(final Date date, final int amount) {
        return set(date, Calendar.MONTH, amount);
    }

    










    public static Date setDays(final Date date, final int amount) {
        return set(date, Calendar.DAY_OF_MONTH, amount);
    }

    











    public static Date setHours(final Date date, final int amount) {
        return set(date, Calendar.HOUR_OF_DAY, amount);
    }

    










    public static Date setMinutes(final Date date, final int amount) {
        return set(date, Calendar.MINUTE, amount);
    }
    
    










    public static Date setSeconds(final Date date, final int amount) {
        return set(date, Calendar.SECOND, amount);
    }

    










    public static Date setMilliseconds(final Date date, final int amount) {
        return set(date, Calendar.MILLISECOND, amount);
    } 
    
    












    private static Date set(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(calendarField, amount);
        return c.getTime();
    }   

    








    public static Calendar toCalendar(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }
    
    


























    public static Date round(final Date date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, MODIFY_ROUND);
        return gval.getTime();
    }




























    public static Calendar round(final Calendar date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar rounded = (Calendar) date.clone();
        modify(rounded, field, MODIFY_ROUND);
        return rounded;
    }





























    public static Date round(final Object date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (date instanceof Date) {
            return round((Date) date, field);
        } else if (date instanceof Calendar) {
            return round((Calendar) date, field).getTime();
        } else {
            throw new ClassCastException("Could not round " + date);
        }
    }

    















    public static Date truncate(final Date date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, MODIFY_TRUNCATE);
        return gval.getTime();
    }
















    public static Calendar truncate(final Calendar date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar truncated = (Calendar) date.clone();
        modify(truncated, field, MODIFY_TRUNCATE);
        return truncated;
    }

















    public static Date truncate(final Object date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (date instanceof Date) {
            return truncate((Date) date, field);
        } else if (date instanceof Calendar) {
            return truncate((Calendar) date, field).getTime();
        } else {
            throw new ClassCastException("Could not truncate " + date);
        }
    }
    
  
















    public static Date ceiling(final Date date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, MODIFY_CEILING);
        return gval.getTime();
    }

















    public static Calendar ceiling(final Calendar date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar ceiled = (Calendar) date.clone();
        modify(ceiled, field, MODIFY_CEILING);
        return ceiled;
    }


















    public static Date ceiling(final Object date, final int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (date instanceof Date) {
            return ceiling((Date) date, field);
        } else if (date instanceof Calendar) {
            return ceiling((Calendar) date, field).getTime();
        } else {
            throw new ClassCastException("Could not find ceiling of for type: " + date.getClass());
        }
    }

    








    private static void modify(final Calendar val, final int field, final int modType) {
        if (val.get(Calendar.YEAR) > 280000000) {
            throw new ArithmeticException("Calendar value too large for accurate calculations");
        }
        
        if (field == Calendar.MILLISECOND) {
            return;
        }

        
        
        
        
        

        final Date date = val.getTime();
        long time = date.getTime();
        boolean done = false;

        
        final int millisecs = val.get(Calendar.MILLISECOND);
        if (MODIFY_TRUNCATE == modType || millisecs < 500) {
            time = time - millisecs;
        }
        if (field == Calendar.SECOND) {
            done = true;
        }

        
        final int seconds = val.get(Calendar.SECOND);
        if (!done && (MODIFY_TRUNCATE == modType || seconds < 30)) {
            time = time - (seconds * 1000L);
        }
        if (field == Calendar.MINUTE) {
            done = true;
        }

        
        final int minutes = val.get(Calendar.MINUTE);
        if (!done && (MODIFY_TRUNCATE == modType || minutes < 30)) {
            time = time - (minutes * 60000L);
        }

        
        if (date.getTime() != time) {
            date.setTime(time);
            val.setTime(date);
        }
        

        boolean roundUp = false;
        for (final int[] aField : fields) {
            for (final int element : aField) {
                if (element == field) {
                    
                    if (modType == MODIFY_CEILING || (modType == MODIFY_ROUND && roundUp)) {
                        if (field == DateUtils.SEMI_MONTH) {
                            
                            
                            
                            if (val.get(Calendar.DATE) == 1) {
                                val.add(Calendar.DATE, 15);
                            } else {
                                val.add(Calendar.DATE, -15);
                                val.add(Calendar.MONTH, 1);
                            }

                        } else if (field == Calendar.AM_PM) {
                            
                            
                            
                            if (val.get(Calendar.HOUR_OF_DAY) == 0) {
                                val.add(Calendar.HOUR_OF_DAY, 12);
                            } else {
                                val.add(Calendar.HOUR_OF_DAY, -12);
                                val.add(Calendar.DATE, 1);
                            }

                        } else {
                            
                            
                            val.add(aField[0], 1);
                        }
                    }
                    return;
                }
            }
            
            int offset = 0;
            boolean offsetSet = false;
            
            switch (field) {
                case DateUtils.SEMI_MONTH:
                    if (aField[0] == Calendar.DATE) {
                        
                        
                        
                        offset = val.get(Calendar.DATE) - 1;
                        
                        
                        if (offset >= 15) {
                            offset -= 15;
                        }
                        
                        roundUp = offset > 7;
                        offsetSet = true;
                    }
                    break;
                case Calendar.AM_PM:
                    if (aField[0] == Calendar.HOUR_OF_DAY) {
                        
                        
                        offset = val.get(Calendar.HOUR_OF_DAY);
                        if (offset >= 12) {
                            offset -= 12;
                        }
                        roundUp = offset >= 6;
                        offsetSet = true;
                    }
                    break;
            }
            if (!offsetSet) {
                final int min = val.getActualMinimum(aField[0]);
                final int max = val.getActualMaximum(aField[0]);
                
                offset = val.get(aField[0]) - min;
                
                roundUp = offset > ((max - min) / 2);
            }
            
            if (offset != 0) {
                val.set(aField[0], val.get(aField[0]) - offset);
            }
        }
        throw new IllegalArgumentException("The field " + field + " is not supported");

    }

    
























    public static Iterator<Calendar> iterator(final Date focus, final int rangeStyle) {
        if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar gval = Calendar.getInstance();
        gval.setTime(focus);
        return iterator(gval, rangeStyle);
    }

























    public static Iterator<Calendar> iterator(final Calendar focus, final int rangeStyle) {
        if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar start = null;
        Calendar end = null;
        int startCutoff = Calendar.SUNDAY;
        int endCutoff = Calendar.SATURDAY;
        switch (rangeStyle) {
            case RANGE_MONTH_SUNDAY:
            case RANGE_MONTH_MONDAY:
                
                start = truncate(focus, Calendar.MONTH);
                
                end = (Calendar) start.clone();
                end.add(Calendar.MONTH, 1);
                end.add(Calendar.DATE, -1);
                
                if (rangeStyle == RANGE_MONTH_MONDAY) {
                    startCutoff = Calendar.MONDAY;
                    endCutoff = Calendar.SUNDAY;
                }
                break;
            case RANGE_WEEK_SUNDAY:
            case RANGE_WEEK_MONDAY:
            case RANGE_WEEK_RELATIVE:
            case RANGE_WEEK_CENTER:
                
                start = truncate(focus, Calendar.DATE);
                end = truncate(focus, Calendar.DATE);
                switch (rangeStyle) {
                    case RANGE_WEEK_SUNDAY:
                        
                        break;
                    case RANGE_WEEK_MONDAY:
                        startCutoff = Calendar.MONDAY;
                        endCutoff = Calendar.SUNDAY;
                        break;
                    case RANGE_WEEK_RELATIVE:
                        startCutoff = focus.get(Calendar.DAY_OF_WEEK);
                        endCutoff = startCutoff - 1;
                        break;
                    case RANGE_WEEK_CENTER:
                        startCutoff = focus.get(Calendar.DAY_OF_WEEK) - 3;
                        endCutoff = focus.get(Calendar.DAY_OF_WEEK) + 3;
                        break;
                }
                break;
            default:
                throw new IllegalArgumentException("The range style " + rangeStyle + " is not valid.");
        }
        if (startCutoff < Calendar.SUNDAY) {
            startCutoff += 7;
        }
        if (startCutoff > Calendar.SATURDAY) {
            startCutoff -= 7;
        }
        if (endCutoff < Calendar.SUNDAY) {
            endCutoff += 7;
        }
        if (endCutoff > Calendar.SATURDAY) {
            endCutoff -= 7;
        }
        while (start.get(Calendar.DAY_OF_WEEK) != startCutoff) {
            start.add(Calendar.DATE, -1);
        }
        while (end.get(Calendar.DAY_OF_WEEK) != endCutoff) {
            end.add(Calendar.DATE, 1);
        }
        return new DateIterator(start, end);
    }

















    public static Iterator<?> iterator(final Object focus, final int rangeStyle) {
        if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (focus instanceof Date) {
            return iterator((Date) focus, rangeStyle);
        } else if (focus instanceof Calendar) {
            return iterator((Calendar) focus, rangeStyle);
        } else {
            throw new ClassCastException("Could not iterate based on " + focus);
        }
    }
    

































    public static long getFragmentInMilliseconds(final Date date, final int fragment) {
        return getFragment(date, fragment, Calendar.MILLISECOND);    
    }
    




































    public static long getFragmentInSeconds(final Date date, final int fragment) {
        return getFragment(date, fragment, Calendar.SECOND);
    }
    




































    public static long getFragmentInMinutes(final Date date, final int fragment) {
        return getFragment(date, fragment, Calendar.MINUTE);
    }
    




































    public static long getFragmentInHours(final Date date, final int fragment) {
        return getFragment(date, fragment, Calendar.HOUR_OF_DAY);
    }
    




































    public static long getFragmentInDays(final Date date, final int fragment) {
        return getFragment(date, fragment, Calendar.DAY_OF_YEAR);
    }





































  public static long getFragmentInMilliseconds(final Calendar calendar, final int fragment) {
    return getFragment(calendar, fragment, Calendar.MILLISECOND);
  }




































    public static long getFragmentInSeconds(final Calendar calendar, final int fragment) {
        return getFragment(calendar, fragment, Calendar.SECOND);
    }
    




































    public static long getFragmentInMinutes(final Calendar calendar, final int fragment) {
        return getFragment(calendar, fragment, Calendar.MINUTE);
    }
    




































    public static long getFragmentInHours(final Calendar calendar, final int fragment) {
        return getFragment(calendar, fragment, Calendar.HOUR_OF_DAY);
    }
    






































    public static long getFragmentInDays(final Calendar calendar, final int fragment) {
        return getFragment(calendar, fragment, Calendar.DAY_OF_YEAR);
    }
    











    private static long getFragment(final Date date, final int fragment, final int unit) {
        if(date == null) {
            throw  new IllegalArgumentException("The date must not be null");
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getFragment(calendar, fragment, unit);
    }












    private static long getFragment(final Calendar calendar, final int fragment, final int unit) {
        if(calendar == null) {
            throw  new IllegalArgumentException("The date must not be null"); 
        }
        final long millisPerUnit = getMillisPerUnit(unit);
        long result = 0;
        
        
        switch (fragment) {
            case Calendar.YEAR:
                result += (calendar.get(Calendar.DAY_OF_YEAR) * MILLIS_PER_DAY) / millisPerUnit;
                break;
            case Calendar.MONTH:
                result += (calendar.get(Calendar.DAY_OF_MONTH) * MILLIS_PER_DAY) / millisPerUnit;
                break;
        }

        switch (fragment) {
            
            case Calendar.YEAR:
            case Calendar.MONTH:
            
            
            case Calendar.DAY_OF_YEAR:
            case Calendar.DATE:
                result += (calendar.get(Calendar.HOUR_OF_DAY) * MILLIS_PER_HOUR) / millisPerUnit;
                
            case Calendar.HOUR_OF_DAY:
                result += (calendar.get(Calendar.MINUTE) * MILLIS_PER_MINUTE) / millisPerUnit;
                
            case Calendar.MINUTE:
                result += (calendar.get(Calendar.SECOND) * MILLIS_PER_SECOND) / millisPerUnit;
                
            case Calendar.SECOND:
                result += (calendar.get(Calendar.MILLISECOND) * 1) / millisPerUnit;
                break;
            case Calendar.MILLISECOND: break;
                default: throw new IllegalArgumentException("The fragment " + fragment + " is not supported");
        }
        return result;
    }
    













    public static boolean truncatedEquals(final Calendar cal1, final Calendar cal2, final int field) {
        return truncatedCompareTo(cal1, cal2, field) == 0;
    }














    public static boolean truncatedEquals(final Date date1, final Date date2, final int field) {
        return truncatedCompareTo(date1, date2, field) == 0;
    }















    public static int truncatedCompareTo(final Calendar cal1, final Calendar cal2, final int field) {
        final Calendar truncatedCal1 = truncate(cal1, field);
        final Calendar truncatedCal2 = truncate(cal2, field);
        return truncatedCal1.compareTo(truncatedCal2);
    }















    public static int truncatedCompareTo(final Date date1, final Date date2, final int field) {
        final Date truncatedDate1 = truncate(date1, field);
        final Date truncatedDate2 = truncate(date2, field);
        return truncatedDate1.compareTo(truncatedDate2);
    }










    private static long getMillisPerUnit(final int unit) {
        long result = Long.MAX_VALUE;
        switch (unit) {
            case Calendar.DAY_OF_YEAR:
            case Calendar.DATE:
                result = MILLIS_PER_DAY;
                break;
            case Calendar.HOUR_OF_DAY:
                result = MILLIS_PER_HOUR;
                break;
            case Calendar.MINUTE:
                result = MILLIS_PER_MINUTE;
                break;
            case Calendar.SECOND:
                result = MILLIS_PER_SECOND;
                break;
            case Calendar.MILLISECOND:
                result = 1;
                break;
            default: throw new IllegalArgumentException("The unit " + unit + " cannot be represented is milleseconds");
        }
        return result;
    }

    



    static class DateIterator implements Iterator<Calendar> {
        private final Calendar endFinal;
        private final Calendar spot;
        






        DateIterator(final Calendar startFinal, final Calendar endFinal) {
            super();
            this.endFinal = endFinal;
            spot = startFinal;
            spot.add(Calendar.DATE, -1);
        }






        @Override
        public boolean hasNext() {
            return spot.before(endFinal);
        }






        @Override
        public Calendar next() {
            if (spot.equals(endFinal)) {
                throw new NoSuchElementException();
            }
            spot.add(Calendar.DATE, 1);
            return (Calendar) spot.clone();
        }







        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
