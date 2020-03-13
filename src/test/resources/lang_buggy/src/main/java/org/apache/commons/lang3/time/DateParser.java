















package org.apache.commons.lang3.time;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;







public interface DateParser {









    Date parse(String source) throws ParseException;












    Date parse(String source, ParsePosition pos);

    
    





    String getPattern();













    TimeZone getTimeZone();






    Locale getLocale();






    Object parseObject(String source) throws ParseException;






    Object parseObject(String source, ParsePosition pos);
}
