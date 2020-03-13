















package org.apache.commons.lang3.time;

import java.text.FieldPosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;







public interface DatePrinter {








    String format(long millis);







    String format(Date date);







    String format(Calendar calendar);









    StringBuffer format(long millis, StringBuffer buf);









    StringBuffer format(Date date, StringBuffer buf);









    StringBuffer format(Calendar calendar, StringBuffer buf);

    
    





    String getPattern();








    TimeZone getTimeZone();






    Locale getLocale();












    StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos);
}
