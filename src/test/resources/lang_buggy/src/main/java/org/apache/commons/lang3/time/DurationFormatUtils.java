















package org.apache.commons.lang3.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;


















public class DurationFormatUtils {







    public DurationFormatUtils() {
        super();
    }








    public static final String ISO_EXTENDED_FORMAT_PATTERN = "'P'yyyy'Y'M'M'd'DT'H'H'm'M's.S'S'";

    









    public static String formatDurationHMS(final long durationMillis) {
        return formatDuration(durationMillis, "H:mm:ss.SSS");
    }












    public static String formatDurationISO(final long durationMillis) {
        return formatDuration(durationMillis, ISO_EXTENDED_FORMAT_PATTERN, false);
    }












    public static String formatDuration(final long durationMillis, final String format) {
        return formatDuration(durationMillis, format, true);
    }














    public static String formatDuration(long durationMillis, final String format, final boolean padWithZeros) {

        final Token[] tokens = lexx(format);

        int days         = 0;
        int hours        = 0;
        int minutes      = 0;
        int seconds      = 0;
        int milliseconds = 0;
        
        if (Token.containsTokenWithValue(tokens, d) ) {
            days = (int) (durationMillis / DateUtils.MILLIS_PER_DAY);
            durationMillis = durationMillis - (days * DateUtils.MILLIS_PER_DAY);
        }
        if (Token.containsTokenWithValue(tokens, H) ) {
            hours = (int) (durationMillis / DateUtils.MILLIS_PER_HOUR);
            durationMillis = durationMillis - (hours * DateUtils.MILLIS_PER_HOUR);
        }
        if (Token.containsTokenWithValue(tokens, m) ) {
            minutes = (int) (durationMillis / DateUtils.MILLIS_PER_MINUTE);
            durationMillis = durationMillis - (minutes * DateUtils.MILLIS_PER_MINUTE);
        }
        if (Token.containsTokenWithValue(tokens, s) ) {
            seconds = (int) (durationMillis / DateUtils.MILLIS_PER_SECOND);
            durationMillis = durationMillis - (seconds * DateUtils.MILLIS_PER_SECOND);
        }
        if (Token.containsTokenWithValue(tokens, S) ) {
            milliseconds = (int) durationMillis;
        }

        return format(tokens, 0, 0, days, hours, minutes, seconds, milliseconds, padWithZeros);
    }












    public static String formatDurationWords(
        final long durationMillis,
        final boolean suppressLeadingZeroElements,
        final boolean suppressTrailingZeroElements) {

        
        
        
        String duration = formatDuration(durationMillis, "d' days 'H' hours 'm' minutes 's' seconds'");
        if (suppressLeadingZeroElements) {
            
            duration = " " + duration;
            String tmp = StringUtils.replaceOnce(duration, " 0 days", "");
            if (tmp.length() != duration.length()) {
                duration = tmp;
                tmp = StringUtils.replaceOnce(duration, " 0 hours", "");
                if (tmp.length() != duration.length()) {
                    duration = tmp;
                    tmp = StringUtils.replaceOnce(duration, " 0 minutes", "");
                    duration = tmp;
                    if (tmp.length() != duration.length()) {
                        duration = StringUtils.replaceOnce(tmp, " 0 seconds", "");
                    }
                }
            }
            if (duration.length() != 0) {
                
                duration = duration.substring(1);
            }
        }
        if (suppressTrailingZeroElements) {
            String tmp = StringUtils.replaceOnce(duration, " 0 seconds", "");
            if (tmp.length() != duration.length()) {
                duration = tmp;
                tmp = StringUtils.replaceOnce(duration, " 0 minutes", "");
                if (tmp.length() != duration.length()) {
                    duration = tmp;
                    tmp = StringUtils.replaceOnce(duration, " 0 hours", "");
                    if (tmp.length() != duration.length()) {
                        duration = StringUtils.replaceOnce(tmp, " 0 days", "");
                    }
                }
            }
        }
        
        duration = " " + duration;
        duration = StringUtils.replaceOnce(duration, " 1 seconds", " 1 second");
        duration = StringUtils.replaceOnce(duration, " 1 minutes", " 1 minute");
        duration = StringUtils.replaceOnce(duration, " 1 hours", " 1 hour");
        duration = StringUtils.replaceOnce(duration, " 1 days", " 1 day");
        return duration.trim();
    }

    









    public static String formatPeriodISO(final long startMillis, final long endMillis) {
        return formatPeriod(startMillis, endMillis, ISO_EXTENDED_FORMAT_PATTERN, false, TimeZone.getDefault());
    }










    public static String formatPeriod(final long startMillis, final long endMillis, final String format) {
        return formatPeriod(startMillis, endMillis, format, true, TimeZone.getDefault());
    }
























    public static String formatPeriod(final long startMillis, final long endMillis, final String format, final boolean padWithZeros, 
            final TimeZone timezone) {

        
        
        
        
        
        
        final Token[] tokens = lexx(format);

        
        
        final Calendar start = Calendar.getInstance(timezone);
        start.setTime(new Date(startMillis));
        final Calendar end = Calendar.getInstance(timezone);
        end.setTime(new Date(endMillis));

        
        int milliseconds = end.get(Calendar.MILLISECOND) - start.get(Calendar.MILLISECOND);
        int seconds = end.get(Calendar.SECOND) - start.get(Calendar.SECOND);
        int minutes = end.get(Calendar.MINUTE) - start.get(Calendar.MINUTE);
        int hours = end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY);
        int days = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH);
        int months = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
        int years = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);

        
        while (milliseconds < 0) {
            milliseconds += 1000;
            seconds -= 1;
        }
        while (seconds < 0) {
            seconds += 60;
            minutes -= 1;
        }
        while (minutes < 0) {
            minutes += 60;
            hours -= 1;
        }
        while (hours < 0) {
            hours += 24;
            days -= 1;
        }
       
        if (Token.containsTokenWithValue(tokens, M)) {
            while (days < 0) {
                days += start.getActualMaximum(Calendar.DAY_OF_MONTH);
                months -= 1;
                start.add(Calendar.MONTH, 1);
            }

            while (months < 0) {
                months += 12;
                years -= 1;
            }

            if (!Token.containsTokenWithValue(tokens, y) && years != 0) {
                while (years != 0) {
                    months += 12 * years;
                    years = 0;
                }
            }
        } else {
            

            if( !Token.containsTokenWithValue(tokens, y) ) {
                int target = end.get(Calendar.YEAR);
                if (months < 0) {
                    
                    target -= 1;
                }
                
                while (start.get(Calendar.YEAR) != target) {
                    days += start.getActualMaximum(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
                    
                    
                    if (start instanceof GregorianCalendar &&
                            start.get(Calendar.MONTH) == Calendar.FEBRUARY &&
                            start.get(Calendar.DAY_OF_MONTH) == 29) {
                        days += 1;
                    }
                    
                    start.add(Calendar.YEAR, 1);
                    
                    days += start.get(Calendar.DAY_OF_YEAR);
                }
                
                years = 0;
            }
            
            while( start.get(Calendar.MONTH) != end.get(Calendar.MONTH) ) {
                days += start.getActualMaximum(Calendar.DAY_OF_MONTH);
                start.add(Calendar.MONTH, 1);
            }
            
            months = 0;            

            while (days < 0) {
                days += start.getActualMaximum(Calendar.DAY_OF_MONTH);
                months -= 1;
                start.add(Calendar.MONTH, 1);
            }
            
        }

        
        
        

        if (!Token.containsTokenWithValue(tokens, d)) {
            hours += 24 * days;
            days = 0;
        }
        if (!Token.containsTokenWithValue(tokens, H)) {
            minutes += 60 * hours;
            hours = 0;
        }
        if (!Token.containsTokenWithValue(tokens, m)) {
            seconds += 60 * minutes;
            minutes = 0;
        }
        if (!Token.containsTokenWithValue(tokens, s)) {
            milliseconds += 1000 * seconds;
            seconds = 0;
        }

        return format(tokens, years, months, days, hours, minutes, seconds, milliseconds, padWithZeros);
    }

    














    static String format(final Token[] tokens, final int years, final int months, final int days, final int hours, final int minutes, final int seconds,
            int milliseconds, final boolean padWithZeros) {
        final StringBuilder buffer = new StringBuilder();
        boolean lastOutputSeconds = false;
        final int sz = tokens.length;
        for (int i = 0; i < sz; i++) {
            final Token token = tokens[i];
            final Object value = token.getValue();
            final int count = token.getCount();
            if (value instanceof StringBuilder) {
                buffer.append(value.toString());
            } else {
                if (value == y) {
                    buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(years), count, '0') : Integer
                            .toString(years));
                    lastOutputSeconds = false;
                } else if (value == M) {
                    buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(months), count, '0') : Integer
                            .toString(months));
                    lastOutputSeconds = false;
                } else if (value == d) {
                    buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(days), count, '0') : Integer
                            .toString(days));
                    lastOutputSeconds = false;
                } else if (value == H) {
                    buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(hours), count, '0') : Integer
                            .toString(hours));
                    lastOutputSeconds = false;
                } else if (value == m) {
                    buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(minutes), count, '0') : Integer
                            .toString(minutes));
                    lastOutputSeconds = false;
                } else if (value == s) {
                    buffer.append(padWithZeros ? StringUtils.leftPad(Integer.toString(seconds), count, '0') : Integer
                            .toString(seconds));
                    lastOutputSeconds = true;
                } else if (value == S) {
                    if (lastOutputSeconds) {
                        milliseconds += 1000;
                        final String str = padWithZeros
                                ? StringUtils.leftPad(Integer.toString(milliseconds), count, '0')
                                : Integer.toString(milliseconds);
                        buffer.append(str.substring(1));
                    } else {
                        buffer.append(padWithZeros
                                ? StringUtils.leftPad(Integer.toString(milliseconds), count, '0')
                                : Integer.toString(milliseconds));
                    }
                    lastOutputSeconds = false;
                }
            }
        }
        return buffer.toString();
    }

    static final Object y = "y";
    static final Object M = "M";
    static final Object d = "d";
    static final Object H = "H";
    static final Object m = "m";
    static final Object s = "s";
    static final Object S = "S";
    






    static Token[] lexx(final String format) {
        final char[] array = format.toCharArray();
        final ArrayList<Token> list = new ArrayList<Token>(array.length);

        boolean inLiteral = false;
        
        
        StringBuilder buffer = null;
        Token previous = null;
        final int sz = array.length;
        for (int i = 0; i < sz; i++) {
            final char ch = array[i];
            if (inLiteral && ch != '\'') {
                buffer.append(ch); 
                continue;
            }
            Object value = null;
            switch (ch) {
            
            case '\'':
                if (inLiteral) {
                    buffer = null;
                    inLiteral = false;
                } else {
                    buffer = new StringBuilder();
                    list.add(new Token(buffer));
                    inLiteral = true;
                }
                break;
            case 'y':
                value = y;
                break;
            case 'M':
                value = M;
                break;
            case 'd':
                value = d;
                break;
            case 'H':
                value = H;
                break;
            case 'm':
                value = m;
                break;
            case 's':
                value = s;
                break;
            case 'S':
                value = S;
                break;
            default:
                if (buffer == null) {
                    buffer = new StringBuilder();
                    list.add(new Token(buffer));
                }
                buffer.append(ch);
            }

            if (value != null) {
                if (previous != null && previous.getValue() == value) {
                    previous.increment();
                } else {
                    final Token token = new Token(value);
                    list.add(token);
                    previous = token;
                }
                buffer = null;
            }
        }
        return list.toArray(new Token[list.size()]);
    }

    



    static class Token {








        static boolean containsTokenWithValue(final Token[] tokens, final Object value) {
            final int sz = tokens.length;
            for (int i = 0; i < sz; i++) {
                if (tokens[i].getValue() == value) {
                    return true;
                }
            }
            return false;
        }

        private final Object value;
        private int count;






        Token(final Object value) {
            this.value = value;
            this.count = 1;
        }








        Token(final Object value, final int count) {
            this.value = value;
            this.count = count;
        }




        void increment() { 
            count++;
        }






        int getCount() {
            return count;
        }






        Object getValue() {
            return value;
        }







        @Override
        public boolean equals(final Object obj2) {
            if (obj2 instanceof Token) {
                final Token tok2 = (Token) obj2;
                if (this.value.getClass() != tok2.value.getClass()) {
                    return false;
                }
                if (this.count != tok2.count) {
                    return false;
                }
                if (this.value instanceof StringBuilder) {
                    return this.value.toString().equals(tok2.value.toString());
                } else if (this.value instanceof Number) {
                    return this.value.equals(tok2.value);
                } else {
                    return this.value == tok2.value;
                }
            }
            return false;
        }








        @Override
        public int hashCode() {
            return this.value.hashCode();
        }






        @Override
        public String toString() {
            return StringUtils.repeat(this.value.toString(), this.count);
        }
    }

}
