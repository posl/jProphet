















package org.apache.commons.lang3.text;

import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;








































public class ExtendedMessageFormat extends MessageFormat {
    private static final long serialVersionUID = -2362048321261811743L;
    private static final int HASH_SEED = 31;

    private static final String DUMMY_PATTERN = "";
    private static final String ESCAPED_QUOTE = "''";
    private static final char START_FMT = ',';
    private static final char END_FE = '}';
    private static final char START_FE = '{';
    private static final char QUOTE = '\'';

    private String toPattern;
    private final Map<String, ? extends FormatFactory> registry;







    public ExtendedMessageFormat(final String pattern) {
        this(pattern, Locale.getDefault());
    }








    public ExtendedMessageFormat(final String pattern, final Locale locale) {
        this(pattern, locale, null);
    }








    public ExtendedMessageFormat(final String pattern, final Map<String, ? extends FormatFactory> registry) {
        this(pattern, Locale.getDefault(), registry);
    }









    public ExtendedMessageFormat(final String pattern, final Locale locale, final Map<String, ? extends FormatFactory> registry) {
        super(DUMMY_PATTERN);
        setLocale(locale);
        this.registry = registry;
        applyPattern(pattern);
    }




    @Override
    public String toPattern() {
        return toPattern;
    }






    @Override
    public final void applyPattern(final String pattern) {
        if (registry == null) {
            super.applyPattern(pattern);
            toPattern = super.toPattern();
            return;
        }
        final ArrayList<Format> foundFormats = new ArrayList<Format>();
        final ArrayList<String> foundDescriptions = new ArrayList<String>();
        final StringBuilder stripCustom = new StringBuilder(pattern.length());

        final ParsePosition pos = new ParsePosition(0);
        final char[] c = pattern.toCharArray();
        int fmtCount = 0;
        while (pos.getIndex() < pattern.length()) {
            switch (c[pos.getIndex()]) {
            case QUOTE:
                appendQuotedString(pattern, pos, stripCustom, true);
                break;
            case START_FE:
                fmtCount++;
                seekNonWs(pattern, pos);
                final int start = pos.getIndex();
                final int index = readArgumentIndex(pattern, next(pos));
                stripCustom.append(START_FE).append(index);
                seekNonWs(pattern, pos);
                Format format = null;
                String formatDescription = null;
                if (c[pos.getIndex()] == START_FMT) {
                    formatDescription = parseFormatDescription(pattern,
                            next(pos));
                    format = getFormat(formatDescription);
                    if (format == null) {
                        stripCustom.append(START_FMT).append(formatDescription);
                    }
                }
                foundFormats.add(format);
                foundDescriptions.add(format == null ? null : formatDescription);
                Validate.isTrue(foundFormats.size() == fmtCount);
                Validate.isTrue(foundDescriptions.size() == fmtCount);
                if (c[pos.getIndex()] != END_FE) {
                    throw new IllegalArgumentException(
                            "Unreadable format element at position " + start);
                }
                
            default:
                stripCustom.append(c[pos.getIndex()]);
                next(pos);
            }
        }
        super.applyPattern(stripCustom.toString());
        toPattern = insertFormats(super.toPattern(), foundDescriptions);
        if (containsElements(foundFormats)) {
            final Format[] origFormats = getFormats();
            
            
            int i = 0;
            for (final Iterator<Format> it = foundFormats.iterator(); it.hasNext(); i++) {
                final Format f = it.next();
                if (f != null) {
                    origFormats[i] = f;
                }
            }
            super.setFormats(origFormats);
        }
    }








    @Override
    public void setFormat(final int formatElementIndex, final Format newFormat) {
        throw new UnsupportedOperationException();
    }








    @Override
    public void setFormatByArgumentIndex(final int argumentIndex, final Format newFormat) {
        throw new UnsupportedOperationException();
    }







    @Override
    public void setFormats(final Format[] newFormats) {
        throw new UnsupportedOperationException();
    }







    @Override
    public void setFormatsByArgumentIndex(final Format[] newFormats) {
        throw new UnsupportedOperationException();
    }







    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (ObjectUtils.notEqual(getClass(), obj.getClass())) {
          return false;
        }
        final ExtendedMessageFormat rhs = (ExtendedMessageFormat)obj;
        if (ObjectUtils.notEqual(toPattern, rhs.toPattern)) {
            return false;
        }
        if (ObjectUtils.notEqual(registry, rhs.registry)) {
            return false;
        }
        return true;
    }






    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = HASH_SEED * result + ObjectUtils.hashCode(registry);
        result = HASH_SEED * result + ObjectUtils.hashCode(toPattern);
        return result;
    }







    private Format getFormat(final String desc) {
        if (registry != null) {
            String name = desc;
            String args = null;
            final int i = desc.indexOf(START_FMT);
            if (i > 0) {
                name = desc.substring(0, i).trim();
                args = desc.substring(i + 1).trim();
            }
            final FormatFactory factory = registry.get(name);
            if (factory != null) {
                return factory.getFormat(name, args, getLocale());
            }
        }
        return null;
    }








    private int readArgumentIndex(final String pattern, final ParsePosition pos) {
        final int start = pos.getIndex();
        seekNonWs(pattern, pos);
        final StringBuilder result = new StringBuilder();
        boolean error = false;
        for (; !error && pos.getIndex() < pattern.length(); next(pos)) {
            char c = pattern.charAt(pos.getIndex());
            if (Character.isWhitespace(c)) {
                seekNonWs(pattern, pos);
                c = pattern.charAt(pos.getIndex());
                if (c != START_FMT && c != END_FE) {
                    error = true;
                    continue;
                }
            }
            if ((c == START_FMT || c == END_FE) && result.length() > 0) {
                try {
                    return Integer.parseInt(result.toString());
                } catch (final NumberFormatException e) { 
                    
                    
                }
            }
            error = !Character.isDigit(c);
            result.append(c);
        }
        if (error) {
            throw new IllegalArgumentException(
                    "Invalid format argument index at position " + start + ": "
                            + pattern.substring(start, pos.getIndex()));
        }
        throw new IllegalArgumentException(
                "Unterminated format element at position " + start);
    }








    private String parseFormatDescription(final String pattern, final ParsePosition pos) {
        final int start = pos.getIndex();
        seekNonWs(pattern, pos);
        final int text = pos.getIndex();
        int depth = 1;
        for (; pos.getIndex() < pattern.length(); next(pos)) {
            switch (pattern.charAt(pos.getIndex())) {
            case START_FE:
                depth++;
                break;
            case END_FE:
                depth--;
                if (depth == 0) {
                    return pattern.substring(text, pos.getIndex());
                }
                break;
            case QUOTE:
                getQuotedString(pattern, pos, false);
                break;
            }
        }
        throw new IllegalArgumentException(
                "Unterminated format element at position " + start);
    }








    private String insertFormats(final String pattern, final ArrayList<String> customPatterns) {
        if (!containsElements(customPatterns)) {
            return pattern;
        }
        final StringBuilder sb = new StringBuilder(pattern.length() * 2);
        final ParsePosition pos = new ParsePosition(0);
        int fe = -1;
        int depth = 0;
        while (pos.getIndex() < pattern.length()) {
            final char c = pattern.charAt(pos.getIndex());
            switch (c) {
            case QUOTE:
                appendQuotedString(pattern, pos, sb, false);
                break;
            case START_FE:
                depth++;
                if (depth == 1) {
                    fe++;
                    sb.append(START_FE).append(
                            readArgumentIndex(pattern, next(pos)));
                    final String customPattern = customPatterns.get(fe);
                    if (customPattern != null) {
                        sb.append(START_FMT).append(customPattern);
                    }
                }
                break;
            case END_FE:
                depth--;
                
            default:
                sb.append(c);
                next(pos);
            }
        }
        return sb.toString();
    }







    private void seekNonWs(final String pattern, final ParsePosition pos) {
        int len = 0;
        final char[] buffer = pattern.toCharArray();
        do {
            len = StrMatcher.splitMatcher().isMatch(buffer, pos.getIndex());
            pos.setIndex(pos.getIndex() + len);
        } while (len > 0 && pos.getIndex() < pattern.length());
    }







    private ParsePosition next(final ParsePosition pos) {
        pos.setIndex(pos.getIndex() + 1);
        return pos;
    }











    private StringBuilder appendQuotedString(final String pattern, final ParsePosition pos,
            final StringBuilder appendTo, final boolean escapingOn) {
        final int start = pos.getIndex();
        final char[] c = pattern.toCharArray();
        if (escapingOn && c[start] == QUOTE) {
            next(pos);
            return appendTo == null ? null : appendTo.append(QUOTE);
        }
        int lastHold = start;
        for (int i = pos.getIndex(); i < pattern.length(); i++) {
            if (escapingOn && pattern.substring(i).startsWith(ESCAPED_QUOTE)) {
                appendTo.append(c, lastHold, pos.getIndex() - lastHold).append(
                        QUOTE);
                pos.setIndex(i + ESCAPED_QUOTE.length());
                lastHold = pos.getIndex();
                continue;
            }
            switch (c[pos.getIndex()]) {
            case QUOTE:
                next(pos);
                return appendTo == null ? null : appendTo.append(c, lastHold,
                        pos.getIndex() - lastHold);
            default:
                next(pos);
            }
        }
        throw new IllegalArgumentException(
                "Unterminated quoted string at position " + start);
    }








    private void getQuotedString(final String pattern, final ParsePosition pos,
            final boolean escapingOn) {
        appendQuotedString(pattern, pos, null, escapingOn);
    }






    private boolean containsElements(final Collection<?> coll) {
        if (coll == null || coll.isEmpty()) {
            return false;
        }
        for (final Object name : coll) {
            if (name != null) {
                return true;
            }
        }
        return false;
    }
}
