















package org.apache.commons.lang3.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;








public class ExceptionUtils {
    






    static final String WRAPPED_MARKER = " [wrapped] ";




    
    private static final String[] CAUSE_METHOD_NAMES = {
        "getCause",
        "getNextException",
        "getTargetException",
        "getException",
        "getSourceException",
        "getRootCause",
        "getCausedByException",
        "getNested",
        "getLinkedException",
        "getNestedException",
        "getLinkedCause",
        "getThrowable",
    };







    public ExceptionUtils() {
        super();
    }

    









    @Deprecated
    public static String[] getDefaultCauseMethodNames() {
        return ArrayUtils.clone(CAUSE_METHOD_NAMES);
    }

    



























    @Deprecated
    public static Throwable getCause(final Throwable throwable) {
        return getCause(throwable, CAUSE_METHOD_NAMES);
    }














    @Deprecated
    public static Throwable getCause(final Throwable throwable, String[] methodNames) {
        if (throwable == null) {
            return null;
        }

        if (methodNames == null) {
            methodNames = CAUSE_METHOD_NAMES;
        }

        for (final String methodName : methodNames) {
            if (methodName != null) {
                final Throwable cause = getCauseUsingMethodName(throwable, methodName);
                if (cause != null) {
                    return cause;
                }
            }
        }

        return null;
    }


















    public static Throwable getRootCause(final Throwable throwable) {
        final List<Throwable> list = getThrowableList(throwable);
        return list.size() < 2 ? null : (Throwable)list.get(list.size() - 1);
    }








    
    private static Throwable getCauseUsingMethodName(final Throwable throwable, final String methodName) {
        Method method = null;
        try {
            method = throwable.getClass().getMethod(methodName);
        } catch (final NoSuchMethodException ignored) { 
            
        } catch (final SecurityException ignored) { 
            
        }

        if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            try {
                return (Throwable) method.invoke(throwable);
            } catch (final IllegalAccessException ignored) { 
                
            } catch (final IllegalArgumentException ignored) { 
                
            } catch (final InvocationTargetException ignored) { 
                
            }
        }
        return null;
    }

    
















    public static int getThrowableCount(final Throwable throwable) {
        return getThrowableList(throwable).size();
    }




















    public static Throwable[] getThrowables(final Throwable throwable) {
        final List<Throwable> list = getThrowableList(throwable);
        return list.toArray(new Throwable[list.size()]);
    }




















    public static List<Throwable> getThrowableList(Throwable throwable) {
        final List<Throwable> list = new ArrayList<Throwable>();
        while (throwable != null && list.contains(throwable) == false) {
            list.add(throwable);
            throwable = ExceptionUtils.getCause(throwable);
        }
        return list;
    }

    














    public static int indexOfThrowable(final Throwable throwable, final Class<?> clazz) {
        return indexOf(throwable, clazz, 0, false);
    }




















    public static int indexOfThrowable(final Throwable throwable, final Class<?> clazz, final int fromIndex) {
        return indexOf(throwable, clazz, fromIndex, false);
    }

    















    public static int indexOfType(final Throwable throwable, final Class<?> type) {
        return indexOf(throwable, type, 0, true);
    }





















    public static int indexOfType(final Throwable throwable, final Class<?> type, final int fromIndex) {
        return indexOf(throwable, type, fromIndex, true);
    }












    private static int indexOf(final Throwable throwable, final Class<?> type, int fromIndex, final boolean subclass) {
        if (throwable == null || type == null) {
            return -1;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        final Throwable[] throwables = ExceptionUtils.getThrowables(throwable);
        if (fromIndex >= throwables.length) {
            return -1;
        }
        if (subclass) {
            for (int i = fromIndex; i < throwables.length; i++) {
                if (type.isAssignableFrom(throwables[i].getClass())) {
                    return i;
                }
            }
        } else {
            for (int i = fromIndex; i < throwables.length; i++) {
                if (type.equals(throwables[i].getClass())) {
                    return i;
                }
            }
        }
        return -1;
    }

    


















    public static void printRootCauseStackTrace(final Throwable throwable) {
        printRootCauseStackTrace(throwable, System.err);
    }




















    public static void printRootCauseStackTrace(final Throwable throwable, final PrintStream stream) {
        if (throwable == null) {
            return;
        }
        if (stream == null) {
            throw new IllegalArgumentException("The PrintStream must not be null");
        }
        final String trace[] = getRootCauseStackTrace(throwable);
        for (final String element : trace) {
            stream.println(element);
        }
        stream.flush();
    }




















    public static void printRootCauseStackTrace(final Throwable throwable, final PrintWriter writer) {
        if (throwable == null) {
            return;
        }
        if (writer == null) {
            throw new IllegalArgumentException("The PrintWriter must not be null");
        }
        final String trace[] = getRootCauseStackTrace(throwable);
        for (final String element : trace) {
            writer.println(element);
        }
        writer.flush();
    }

    













    public static String[] getRootCauseStackTrace(final Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final Throwable throwables[] = getThrowables(throwable);
        final int count = throwables.length;
        final List<String> frames = new ArrayList<String>();
        List<String> nextTrace = getStackFrameList(throwables[count - 1]);
        for (int i = count; --i >= 0;) {
            final List<String> trace = nextTrace;
            if (i != 0) {
                nextTrace = getStackFrameList(throwables[i - 1]);
                removeCommonFrames(trace, nextTrace);
            }
            if (i == count - 1) {
                frames.add(throwables[i].toString());
            } else {
                frames.add(WRAPPED_MARKER + throwables[i].toString());
            }
            for (int j = 0; j < trace.size(); j++) {
                frames.add(trace.get(j));
            }
        }
        return frames.toArray(new String[frames.size()]);
    }









    public static void removeCommonFrames(final List<String> causeFrames, final List<String> wrapperFrames) {
        if (causeFrames == null || wrapperFrames == null) {
            throw new IllegalArgumentException("The List must not be null");
        }
        int causeFrameIndex = causeFrames.size() - 1;
        int wrapperFrameIndex = wrapperFrames.size() - 1;
        while (causeFrameIndex >= 0 && wrapperFrameIndex >= 0) {
            
            
            final String causeFrame = causeFrames.get(causeFrameIndex);
            final String wrapperFrame = wrapperFrames.get(wrapperFrameIndex);
            if (causeFrame.equals(wrapperFrame)) {
                causeFrames.remove(causeFrameIndex);
            }
            causeFrameIndex--;
            wrapperFrameIndex--;
        }
    }

    












    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }














    public static String[] getStackFrames(final Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return getStackFrames(getStackTrace(throwable));
    }

    








    static String[] getStackFrames(final String stackTrace) {
        final String linebreak = SystemUtils.LINE_SEPARATOR;
        final StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        final List<String> list = new ArrayList<String>();
        while (frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }
        return list.toArray(new String[list.size()]);
    }













    static List<String> getStackFrameList(final Throwable t) {
        final String stackTrace = getStackTrace(t);
        final String linebreak = SystemUtils.LINE_SEPARATOR;
        final StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        final List<String> list = new ArrayList<String>();
        boolean traceStarted = false;
        while (frames.hasMoreTokens()) {
            final String token = frames.nextToken();
            
            final int at = token.indexOf("at");
            if (at != -1 && token.substring(0, at).trim().isEmpty()) {
                traceStarted = true;
                list.add(token);
            } else if (traceStarted) {
                break;
            }
        }
        return list;
    }

    










    public static String getMessage(final Throwable th) {
        if (th == null) {
            return "";
        }
        final String clsName = ClassUtils.getShortClassName(th, null);
        final String msg = th.getMessage();
        return clsName + ": " + StringUtils.defaultString(msg);
    }

    










    public static String getRootCauseMessage(final Throwable th) {
        Throwable root = ExceptionUtils.getRootCause(th);
        root = root == null ? th : root;
        return getMessage(root);
    }

}
